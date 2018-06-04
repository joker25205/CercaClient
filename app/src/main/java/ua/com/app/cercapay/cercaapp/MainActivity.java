package ua.com.app.cercapay.cercaapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ua.com.app.cercapay.cercaapp.btserver.BluetoothServer;
import ua.com.app.cercapay.cercaapp.btserver.BluetoothServerImpl;
import ua.com.app.cercapay.cercaapp.btserver.BluetoothSettings;
import ua.com.app.cercapay.cercaapp.btserver.Logger;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.AcceptThreadListener;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.BluetoothAcceptSocketProvider;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.BluetoothConnectionStateProvider;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.BluetoothServerListener;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.BluetoothThreadsFabric;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.ConnectedThreadListener;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.ConnectionStateProvider;
import ua.com.app.cercapay.cercaapp.btserver.threads.AcceptThread;
import ua.com.app.cercapay.cercaapp.btserver.threads.ConnectedThread;

/*
 */

public class MainActivity extends AppCompatActivity implements BluetoothServerListener {

    private static final int MESSAGE = 02;
    private int LOG_ID = 01;
    public static final int PERMISSION_REQUEST_CODE = 007;
    private BluetoothServer mBluetoothServer;
    private UUID mId = UUID.fromString("c90c5b86-536f-11e8-9c2d-fa7ae01bbebc");
    private RecyclerView mLogsRecycler;
    private Handler mHandler;
    private Button mBtnSend;
    List<String> mLogsList = new ArrayList<>();
    LogsAdapter mLogsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogsRecycler = findViewById(R.id.logs_recycler_view);
        mBtnSend = findViewById(R.id.send);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mBluetoothServer.sendToClient((byte) 1);
            }
        });
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothServer.startServer();
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothServer.stopServer();
            }
        });

        mLogsAdapter = new LogsAdapter(mLogsList);
        mLogsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mLogsRecycler.setAdapter(mLogsAdapter);

        // init
        mHandler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                if (msg.what == LOG_ID) {
                    log((String) msg.obj);
                }
                if (msg.what == MESSAGE) {
                    log((String) msg.obj);
                }
            }
        };
        mBluetoothServer = new BluetoothServerImpl(
                new BluetoothSettings("MyServer", mId),
                new BluetoothAcceptSocketProvider() {
                    @NonNull
                    @Override
                    public BluetoothServerSocket getSocketUsingRfcommWithServiceRecord(String name, UUID uuid) throws IOException {
                        return BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord(name, uuid);
                    }
                },
                new BluetoothConnectionStateProvider() {
                    @Override
                    public boolean isEnabled() {
                        return BluetoothAdapter.getDefaultAdapter().isEnabled();
                    }
                },
                this,
                new BluetoothThreadsFabric() {
                    @Override
                    public AcceptThread getAcceptThread(BluetoothSettings settings, BluetoothAcceptSocketProvider socketProvider, AcceptThreadListener acceptThreadListener, ConnectionStateProvider connectionStateProvider) {
                        return new AcceptThread(settings, socketProvider, acceptThreadListener, connectionStateProvider);
                    }

                    @Override
                    public ConnectedThread getConnectedThread(BluetoothSocket socket, ConnectedThreadListener connectedThreadListener, ConnectionStateProvider provider) {
                        return new ConnectedThread(socket, connectedThreadListener, provider);
                    }
                },
                new Logger() {
                    @Override
                    public void logAction(String action) {
                        Log.d(TAG + "Server", action);
                    }

                    @Override
                    public void logError(String error) {
                        Log.e(TAG + "Server", error);
                    }
                }
        );
        requestBluetoothPermissions();
    }


    public void requestBluetoothPermissions() {
        final String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    void log(String log) {
        Log.d("Activity_Thread", log);
        mLogsList.add(log);
        mLogsAdapter.notifyDataSetChanged();
        mLogsRecycler.scrollToPosition(mLogsList.size() - 1);
    }

    @Override
    public void onByteCame(final byte[] b) {
        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE, "< iCabby: " + new String(b)));
    }

    @Override
    public void onError(Throwable e) {
        Log.e("Activity_Thread", e.getMessage(), e);
    }

    @Override
    public void onStateChanged(final int state) {
        switch (state) {
            case BluetoothServerImpl.STATE_CONNECTED:
                mHandler.sendMessage(mHandler.obtainMessage(LOG_ID, "<BT: STATE_CONNECTED"));
                break;
            case BluetoothServerImpl.STATE_STOPPED:
                mHandler.sendMessage(mHandler.obtainMessage(LOG_ID, "<BT: STATE_STOPPED"));
                break;
            case BluetoothServerImpl.STATE_LISTENING:
                mHandler.sendMessage(mHandler.obtainMessage(LOG_ID, "<BT: STATE_LISTENING"));
                break;
        }
    }
}
