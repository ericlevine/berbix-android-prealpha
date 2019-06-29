package com.berbix.sdk;

public class BerbixStateManager {
    private static final BerbixStateManager instance = new BerbixStateManager();

    private BerbixApiManager apiManager;
    private BerbixAuthFlow authFlow;

    private BerbixStateManager() {}

    private void configureState(BerbixApiManager apiManager, BerbixAuthFlow authFlow) {
        this.apiManager = apiManager;
        this.authFlow = authFlow;
    }

    static void configure(BerbixApiManager apiManager, BerbixAuthFlow authFlow) {
        instance.configureState(apiManager, authFlow);
    }

    public static BerbixApiManager getApiManager() {
        return instance.apiManager;
    }

    public static BerbixAuthFlow getAuthFlow() {
        return instance.authFlow;
    }
}
