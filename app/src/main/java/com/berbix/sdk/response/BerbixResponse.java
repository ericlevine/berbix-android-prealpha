package com.berbix.sdk.response;

import com.google.gson.annotations.SerializedName;

public class BerbixResponse {

    @SerializedName("code")
    public int code = 0;

    @SerializedName("token")
    public String token;

    @SerializedName("readable")
    public String error;

    @SerializedName("id")
    public long id = 0;

    @SerializedName("parent_id")
    public long parentId = 0;

    @SerializedName("next")
    public BerbixNextResponse next;
}