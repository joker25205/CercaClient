package ua.com.app.cercapay.cercaapp.btserver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ICabbyData {

    @SerializedName("cmd")
    @Expose
    private String cmd;
    @SerializedName("args")
    @Expose
    private Args args;

    public String getCmd() {
        return cmd;
    }

    public Args getArgs() {
        return args;
    }
}
