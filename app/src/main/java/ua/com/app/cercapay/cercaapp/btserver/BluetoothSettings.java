package ua.com.app.cercapay.cercaapp.btserver;

import java.util.UUID;

public class BluetoothSettings {
    private String mServerName;
    private UUID mServerId;

    public BluetoothSettings(final String serverName, final UUID serverId) {
        mServerName = serverName;
        mServerId = serverId;
    }

    public String getServerName() {
        return mServerName;
    }

    public UUID getServerId() {
        return mServerId;
    }
}
