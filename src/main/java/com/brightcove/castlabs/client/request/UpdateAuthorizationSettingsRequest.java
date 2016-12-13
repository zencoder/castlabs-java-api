package com.brightcove.castlabs.client.request;

public class UpdateAuthorizationSettingsRequest {

    private String mode;
    private String callbackUrl;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) { this.mode = mode; }

    public String getCallbackUrl() { return callbackUrl; }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    @Override
    public String toString() {
        return "UpdateAuthorizationSettingsRequest{" +
                "mode='" + mode + '\'' +
                ", callbackUrl='" + callbackUrl + '\'' +
                '}';
    }

}
