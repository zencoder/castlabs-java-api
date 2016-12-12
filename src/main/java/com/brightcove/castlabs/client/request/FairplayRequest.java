package com.brightcove.castlabs.client.request;

public class FairplayRequest {

    private String providerCertificate;
    private String providerPrivateKey;
    private String applicationSecretKey;

    public String getProviderCertificate() {
        return providerCertificate;
    }

    public void setProviderCertificate(String providerCertificate) {
        this.providerCertificate = providerCertificate;
    }

    public String getProviderPrivateKey() { return providerPrivateKey; }

    public void setProviderPrivateKey(String providerPrivateKey) {
        this.providerPrivateKey = providerPrivateKey;
    }

    public String getApplicationSecretKey() { return applicationSecretKey; }

    public void setApplicationSecretKey(String applicationSecretKey) { this.applicationSecretKey = applicationSecretKey; }

    @Override
    public String toString() {
        return "UpdateAuthorizationSettingsRequest{" +
                "providerCertificate='" + providerCertificate + '\'' +
                ", providerPrivateKey='" + providerPrivateKey + '\'' +
                ", applicationSecretKey='" + applicationSecretKey + '\'' +
                '}';
    }

}
