package com.berbix.sdk.response;

import com.google.gson.annotations.SerializedName;

public class BerbixNextResponse {
    @SerializedName("type")
    public String type;

    @SerializedName("code")
    public String code;

    @SerializedName("payload")
    public BerbixNextPayload payload;
}
