package ua.com.app.cercapay.cercaapp.btserver.interfaces;

public interface BluetoothServerListener {
    void onByteCame(byte[] b);

    void onError(Throwable e);

    void onStateChanged(int state);

}
