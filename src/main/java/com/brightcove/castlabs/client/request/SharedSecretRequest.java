package com.brightcove.castlabs.client.request;

public class SharedSecretRequest {

    private Boolean enabled;
    private String description;
    private String secret;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSecret() { return secret; }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return "SharedSecretRequest{" +
                "enabled='" + enabled + '\'' +
                ", description='" + description + '\'' +
                ", secret='" + secret + '\'' +
                '}';
    }

}
