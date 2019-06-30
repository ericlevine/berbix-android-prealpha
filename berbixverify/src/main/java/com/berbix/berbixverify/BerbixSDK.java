package com.berbix.berbixverify;

import android.content.Context;

public class BerbixSDK implements BerbixAuthFlow.BerbixAuthFlowAdapter {
    private final String clientID;

    private BerbixSDKAdapter adapter;

    public BerbixSDK(String clientID) {
        this.clientID = clientID;
    }

    private void configure(BerbixSDKOptions options) {
        BerbixConfiguration config = new BerbixConfiguration();
        config.clientID = clientID;
        config.roleKey = options.getRoleKey();
        config.mode = "android";

        BerbixAuthFlow authFlow = new BerbixAuthFlow(this);
        BerbixAPIManager apiManager = new BerbixAPIManager(
                authFlow,
                config,
                options.getEnvironment(),
                options.getBaseURL());

        BerbixStateManager.configure(apiManager, authFlow);
    }

    public void startFlow(Context context, BerbixSDKAdapter adapter, BerbixSDKOptions options) {
        configure(options);
        this.adapter = adapter;
        BerbixStateManager.getAuthFlow().startAuthFlow(context);
    }

    public void createSession(BerbixSDKAdapter adapter, BerbixSDKOptions options, Runnable callback) {
        configure(options);
        this.adapter = adapter;
        BerbixStateManager.getAuthFlow().createSession(callback);
    }

    public void display(Context context) {
        BerbixStateManager.getAuthFlow().startAuthFlow(context);
    }

    @Override
    public void receiveCode(String code) {
        adapter.onComplete();
    }
}
