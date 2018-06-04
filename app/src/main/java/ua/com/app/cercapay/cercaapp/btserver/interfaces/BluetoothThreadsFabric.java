package ua.com.app.cercapay.cercaapp.btserver.interfaces;

import android.bluetooth.BluetoothSocket;

import ua.com.app.cercapay.cercaapp.btserver.BluetoothSettings;
import ua.com.app.cercapay.cercaapp.btserver.threads.AcceptThread;
import ua.com.app.cercapay.cercaapp.btserver.threads.ConnectedThread;

public interface BluetoothThreadsFabric {
    AcceptThread getAcceptThread(final BluetoothSettings settings,
                                 final BluetoothAcceptSocketProvider socketProvider,
                                 final AcceptThreadListener acceptThreadListener,
                                 final ConnectionStateProvider connectionStateProvider);

    ConnectedThread getConnectedThread(final BluetoothSocket socket,
                                       final ConnectedThreadListener connectedThreadListener,
                                       final ConnectionStateProvider provider);

}
