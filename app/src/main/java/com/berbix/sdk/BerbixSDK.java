package com.berbix.sdk;

import android.content.Context;

public class BerbixSDK implements BerbixAuthFlow.BerbixAuthFlowAdapter {
    private final BerbixAuthFlow authFlow;
    private final BerbixApiManager apiManager;

    private BerbixSDKAdapter adapter;

    public BerbixSDK(String clientID, BerbixSDKOptions options) {
        BerbixConfiguration config = new BerbixConfiguration();
        config.clientID = clientID;
        config.roleKey = options.getRoleKey();
        config.mode = "android";

        this.authFlow = new BerbixAuthFlow(this);
        this.apiManager = new BerbixApiManager(
                this.authFlow,
                config,
                options.getEnvironment(),
                options.getBaseURL());
    }

    public void startFlow(Context context, BerbixSDKAdapter adapter) {
        BerbixStateManager.configure(apiManager, authFlow);
        this.adapter = adapter;
        this.authFlow.startAuthFlow(context);
    }

    @Override
    public void receiveCode(String code) {
        adapter.onComplete();
    }
}
