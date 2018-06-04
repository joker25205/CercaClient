package ua.com.app.cercapay.cercaapp.btserver.interfaces;

public interface ConnectedThreadListener {
    void onIncomingBtByte(byte[] b);

    void onConnectedThreadError(final Throwable reason);
}
