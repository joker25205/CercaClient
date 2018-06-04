package ua.com.app.cercapay.cercaapp.btserver;

public interface BluetoothServer {
    void startServer();

    void sendToClient(byte b);

    void stopServer();
}
