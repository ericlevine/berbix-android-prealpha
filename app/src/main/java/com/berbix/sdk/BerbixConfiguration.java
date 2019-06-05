package com.berbix.sdk;


import com.google.gson.annotations.SerializedName;

class BerbixConfiguration {

    @SerializedName("client_id")
    String clientID = "";

    @SerializedName("role")
    String roleKey = "";

    String mode = "";

}

class BerbixIDType {
    @SerializedName("id_type")
    String idType = null;
}