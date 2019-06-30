package com.berbix.berbixverify;

public class BerbixSDKOptions {
    private String baseURL;
    private String email;
    private String phone;
    private String clientToken;
    private String roleKey;
    private BerbixEnvironment environment;

    BerbixSDKOptions(String baseURL, String email, String phone, String clientToken, String roleKey, BerbixEnvironment environment) {
        this.baseURL = baseURL;
        this.email = email;
        this.phone = phone;
        this.clientToken = clientToken;
        this.roleKey = roleKey;
        this.environment = environment;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getClientToken() {
        return clientToken;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public BerbixEnvironment getEnvironment() {
        return environment;
    }
}
