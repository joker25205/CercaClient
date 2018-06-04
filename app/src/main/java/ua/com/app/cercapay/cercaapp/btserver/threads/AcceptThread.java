package ua.com.app.cercapay.cercaapp.btserver.threads;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

import ua.com.app.cercapay.cercaapp.btserver.BluetoothServerImpl;
import ua.com.app.cercapay.cercaapp.btserver.BluetoothSettings;
import ua.com.app.cercapay.cercaapp.btserver.exceptions.AcceptSocketException;
import ua.com.app.cercapay.cercaapp.btserver.exceptions.GettingSocketException;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.AcceptThreadListener;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.BluetoothAcceptSocketProvider;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.ConnectionStateProvider;

public class AcceptThread extends Thread {

    private BluetoothServerSocket mmServerSocket;
    private BluetoothSettings mSettings;
    private BluetoothAcceptSocketProvider mSocketProvider;
    private AcceptThreadListener mAcceptThreadListener;
    private ConnectionStateProvider mConnectionStateProvider;

    public AcceptThread(final BluetoothSettings settings,
                        final BluetoothAcceptSocketProvider socketProvider,
                        final AcceptThreadListener acceptThreadListener,
                        final ConnectionStateProvider connectionStateProvider) {
        mSettings = settings;
        mSocketProvider = socketProvider;
        mAcceptThreadListener = acceptThreadListener;
        mConnectionStateProvider = connectionStateProvider;
    }


    public boolean setupSocket() {
        try {
            mmServerSocket = mSocketProvider.getSocketUsingRfcommWithServiceRecord(mSettings.getServerName(), mSettings.getServerId());
            return true;
        } catch (IOException e) {
            mAcceptThreadListener.onAcceptThreadError(new GettingSocketException(e.getMessage()));
            return false;
        }
    }

    @Override
    public void run() {
        BluetoothSocket socket;
        while (mConnectionStateProvider.provideConnectionState() == BluetoothServerImpl.STATE_LISTENING) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                mAcceptThreadListener.onAcceptThreadError(new AcceptSocketException(e.getMessage()));
                break;
            }

            if (socket != null) {
                mAcceptThreadListener.onClientConnected(socket);
                break;
            }
        }
    }

    public void cancel() {
        try {
            if (mmServerSocket != null) mmServerSocket.close();
        } catch (IOException e) {
            mAcceptThreadListener.onAcceptThreadError(e);
        }
    }
}
