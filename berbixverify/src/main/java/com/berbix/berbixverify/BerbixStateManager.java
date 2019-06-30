package com.berbix.berbixverify;

public class BerbixStateManager {
    private static final BerbixStateManager instance = new BerbixStateManager();

    private BerbixAPIManager apiManager;
    private BerbixAuthFlow authFlow;

    private BerbixStateManager() {}

    private void configureState(BerbixAPIManager apiManager, BerbixAuthFlow authFlow) {
        this.apiManager = apiManager;
        this.authFlow = authFlow;
    }

    static void configure(BerbixAPIManager apiManager, BerbixAuthFlow authFlow) {
        instance.configureState(apiManager, authFlow);
    }

    public static BerbixAPIManager getApiManager() {
        return instance.apiManager;
    }

    public static BerbixAuthFlow getAuthFlow() {
        return instance.authFlow;
    }
}
