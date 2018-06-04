package ua.com.app.cercapay.cercaapp.btserver;

import android.bluetooth.BluetoothSocket;


import ua.com.app.cercapay.cercaapp.btserver.exceptions.AcceptSocketException;
import ua.com.app.cercapay.cercaapp.btserver.exceptions.GettingSocketException;
import ua.com.app.cercapay.cercaapp.btserver.exceptions.StreamInitializeException;
import ua.com.app.cercapay.cercaapp.btserver.exceptions.StreamReadingException;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.AcceptThreadListener;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.BluetoothAcceptSocketProvider;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.BluetoothConnectionStateProvider;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.BluetoothServerListener;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.BluetoothThreadsFabric;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.ConnectedThreadListener;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.ConnectionStateProvider;
import ua.com.app.cercapay.cercaapp.btserver.threads.AcceptThread;
import ua.com.app.cercapay.cercaapp.btserver.threads.ConnectedThread;

public class BluetoothServerImpl implements
        BluetoothServer,
        AcceptThreadListener,
        ConnectedThreadListener,
        ConnectionStateProvider {
    private final String TAG = "BluetoothServer";

    public static final int STATE_STOPPED = 0;
    public static final int STATE_LISTENING = 1;
    public static final int STATE_CONNECTED = 3;

    private BluetoothSettings mSettings;
    private BluetoothAcceptSocketProvider mSocketProvider;
    private BluetoothServerListener mServerListener;
    private AcceptThread mAcceptThread;
    private ConnectedThread mConnectedThread;
    private BluetoothThreadsFabric mFabric;
    private Logger mLogger;

    private BluetoothConnectionStateProvider mBluetoothConnectionStateProvider;

    private int mConnectionState;

    public BluetoothServerImpl(final BluetoothSettings settings,
                               final BluetoothAcceptSocketProvider socketProvider,
                               final BluetoothConnectionStateProvider bluetoothConnectionStateProvider,
                               final BluetoothServerListener serverListener,
                               final BluetoothThreadsFabric fabric,
                               final Logger logger) {
        mSettings = settings;
        mSocketProvider = socketProvider;
        mServerListener = serverListener;

        mBluetoothConnectionStateProvider = bluetoothConnectionStateProvider;

        mFabric = fabric;
        mLogger = logger;
    }

    @Override
    public synchronized void startServer() {
        if (mConnectionState != STATE_STOPPED) stopServer();

        if (mBluetoothConnectionStateProvider.isEnabled()) {
            mAcceptThread = mFabric.getAcceptThread(mSettings, mSocketProvider, this, this);
            if (mAcceptThread.setupSocket()) {
                setConnectionState(STATE_LISTENING);
                mAcceptThread.start();
            }
        } else {
            setConnectionState(STATE_STOPPED);
        }
    }

    @Override
    public void onClientConnected(final BluetoothSocket socket) {
        stopThreads();

        mConnectedThread = mFabric.getConnectedThread(socket, this, this);
        if (mConnectedThread.initStreams()) {
            setConnectionState(STATE_CONNECTED);
            mConnectedThread.start();
        }
    }

    @Override
    public void onAcceptThreadError(Throwable reason) {
        if (mConnectionState != STATE_STOPPED) {
            if (reason instanceof GettingSocketException) {
                stopServer();
            } else if (reason instanceof AcceptSocketException) {
                stopServer();
            }
        }
        mServerListener.onError(reason);
    }

    @Override
    public void onConnectedThreadError(Throwable reason) {
        if (mConnectionState != STATE_STOPPED) {
            if (reason instanceof StreamInitializeException) {
                startServer();
            } else if (reason instanceof StreamReadingException) {
                startServer();
            }
        }
        mServerListener.onError(reason);
    }

    @Override
    public void onIncomingBtByte(final byte[] b) {
        mServerListener.onByteCame(b);
    }

    @Override
    public void sendToClient(byte b) {
        mConnectedThread.write(b);
    }

    @Override
    public synchronized void stopServer() {
        setConnectionState(STATE_STOPPED);
        stopThreads();
    }

    @Override
    public int provideConnectionState() {
        return mConnectionState;
    }

    private void stopThreads() {
        if (mConnectedThread != null) {
            mConnectedThread.interrupt();
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.interrupt();
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
    }

    private void setConnectionState(int connectionState) {
        mConnectionState = connectionState;
        mServerListener.onStateChanged(mConnectionState);
    }
}

