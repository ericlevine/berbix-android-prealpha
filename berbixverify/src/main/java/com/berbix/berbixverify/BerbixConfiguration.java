package com.berbix.berbixverify;


import com.google.gson.annotations.SerializedName;

class BerbixConfiguration {

    @SerializedName("client_id")
    String clientID = "";

    @SerializedName("role")
    String roleKey = "";

    String mode = "";

}

