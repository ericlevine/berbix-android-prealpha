package com.berbix.sdk;

import android.content.Context;

public class BerbixSDK {

    public static BerbixSDK shared = new BerbixSDK();

    BerbixConfiguration config;

    private BerbixApiManager apiManager;
    private BerbixAuthFlow authFlow;

    public BerbixSDK() {
        config = new BerbixConfiguration();
        authFlow = new BerbixAuthFlow();
        apiManager = new BerbixApiManager(authFlow);
    }

    public void configure(String clientID, String roleKey) {
        config.clientID = clientID;
        config.roleKey = roleKey;
        config.mode = "rn";
    }

    public void setEnvironment(BerbixEnvironment environment) {
        apiManager.environment = environment;
    }

    public BerbixApiManager api() {
        return apiManager;
    }

    public BerbixAuthFlow auth() {
        return authFlow;
    }

    public static void getAuthorized(Context context) {
        shared.authFlow.context = context;
        shared.authFlow.startAuthFlow();
    }
}
