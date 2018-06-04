package ua.com.app.cercapay.cercaapp.btserver;

public interface Logger {
    String TAG = "Rtg_";

    void logAction(final String action);

    void logError(final String error);
}
