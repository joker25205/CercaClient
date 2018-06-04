package ua.com.app.cercapay.cercaapp.btserver.interfaces;

import android.bluetooth.BluetoothSocket;

public interface AcceptThreadListener {
    void onClientConnected(BluetoothSocket socket);

    void onAcceptThreadError(Throwable reason);
}
