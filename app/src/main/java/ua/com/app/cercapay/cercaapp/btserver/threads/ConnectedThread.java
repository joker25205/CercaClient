package ua.com.app.cercapay.cercaapp.btserver.threads;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ua.com.app.cercapay.cercaapp.btserver.BluetoothServerImpl;
import ua.com.app.cercapay.cercaapp.btserver.exceptions.SendingDataException;
import ua.com.app.cercapay.cercaapp.btserver.exceptions.StreamInitializeException;
import ua.com.app.cercapay.cercaapp.btserver.exceptions.StreamReadingException;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.ConnectedThreadListener;
import ua.com.app.cercapay.cercaapp.btserver.interfaces.ConnectionStateProvider;

public class ConnectedThread extends Thread {

    private final ConnectedThreadListener mConnectedThreadListener;
    private final ConnectionStateProvider mProvider;

    private final BluetoothSocket mSocket;
    private InputStream mInStream;
    private OutputStream mOutStream;

    public ConnectedThread(BluetoothSocket socket,
                           ConnectedThreadListener connectedThreadListener,
                           ConnectionStateProvider provider) {
        mProvider = provider;
        mConnectedThreadListener = connectedThreadListener;
        mSocket = socket;
    }

    public boolean initStreams() {
        try {
            mInStream = mSocket.getInputStream();
            mOutStream = mSocket.getOutputStream();
            return true;
        } catch (IOException e) {
            mConnectedThreadListener.onConnectedThreadError(new StreamInitializeException(e.getMessage()));
            return false;
        }
    }

    @Override
    public void run() {
        while (mProvider.provideConnectionState() == BluetoothServerImpl.STATE_CONNECTED) {
            try {
                byte[] buffer = new byte[1024];
                int length = mInStream.read(buffer);

                byte[] b1 = new byte[length];

                System.arraycopy(buffer, 0, b1, 0, length);

                mConnectedThreadListener.onIncomingBtByte(b1);
//                String s = new String(b1);
//                Log.d("Message", "run: " + s);

            } catch (IOException e) {
                mConnectedThreadListener.onConnectedThreadError(new StreamReadingException(e.getMessage()));
                break;
            }
        }
    }

    /**
     * Send to connected device.
     *
     * @param b - data.
     */
    public void write(byte b) {
        try {
            mOutStream.write(b);
        } catch (IOException e) {
            mConnectedThreadListener.onConnectedThreadError(new SendingDataException(e.getMessage()));
        }
    }

    public void cancel() {
        try {
            if (mSocket != null) mSocket.close();
        } catch (IOException e) {
            mConnectedThreadListener.onConnectedThreadError(e);
        }
    }

}
