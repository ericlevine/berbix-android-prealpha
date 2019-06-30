package com.berbix.berbixverify;

public class BerbixSDKOptionsBuilder {
    private String baseURL;
    private String email;
    private String phone;
    private String clientToken;
    private String roleKey;
    private BerbixEnvironment environment;

    public BerbixSDKOptionsBuilder setBaseURL(String baseURL) {
        this.baseURL = baseURL;
        return this;
    }

    public BerbixSDKOptionsBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public BerbixSDKOptionsBuilder setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public BerbixSDKOptionsBuilder setClientToken(String clientToken) {
        this.clientToken = clientToken;
        return this;
    }

    public BerbixSDKOptionsBuilder setRoleKey(String roleKey) {
        this.roleKey = roleKey;
        return this;
    }

    public BerbixSDKOptionsBuilder setEnvironment(BerbixEnvironment environment) {
        this.environment = environment;
        return this;
    }

    public BerbixSDKOptions build() {
        return new BerbixSDKOptions(baseURL, email, phone, clientToken, roleKey, environment);
    }
}