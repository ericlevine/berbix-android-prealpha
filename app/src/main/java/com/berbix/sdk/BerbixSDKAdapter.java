package com.berbix.sdk;

public interface BerbixSDKAdapter {
    void onComplete();
    void onReady();
    void onError(Throwable t);
}
