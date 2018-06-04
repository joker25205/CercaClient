package ua.com.app.cercapay.cercaapp.btserver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Args {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("fare")
    @Expose
    private Integer fare;
    @SerializedName("extras")
    @Expose
    private Integer extras;
    @SerializedName("total_amount")
    @Expose
    private Integer totalAmount;
    @SerializedName("flat_rate")
    @Expose
    private Boolean flatRate;
    @SerializedName("trip_id")
    @Expose
    private String tripId;
    @SerializedName("account")
    @Expose
    private Boolean account;

    public String getStatus() {
        return status;
    }

    public Integer getFare() {
        return fare;
    }

    public Integer getExtras() {
        return extras;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public Boolean getFlatRate() {
        return flatRate;
    }

    public String getTripId() {
        return tripId;
    }

    public Boolean getAccount() {
        return account;
    }

}
