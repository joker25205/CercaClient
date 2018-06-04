package ua.com.app.cercapay.cercaapp.btserver.interfaces;

import android.bluetooth.BluetoothServerSocket;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.UUID;

public interface BluetoothAcceptSocketProvider {
    @NonNull
    BluetoothServerSocket getSocketUsingRfcommWithServiceRecord(final String name, final UUID uuid) throws IOException;
}
