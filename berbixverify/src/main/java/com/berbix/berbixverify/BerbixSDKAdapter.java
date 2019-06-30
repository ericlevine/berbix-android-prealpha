package com.berbix.berbixverify;

public interface BerbixSDKAdapter {
    void onComplete();
    void onReady();
    void onError(Throwable t);
}
