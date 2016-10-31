package com.brightcove.castlabs.client.request;

public class AddSubMerchantAccountRequest {

    private String name;
    private String apiNameSuffix;
    private String linkedUserUuid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiNameSuffix() {
        return apiNameSuffix;
    }

    public void setApiNameSuffix(String apiNameSuffix) {
        this.apiNameSuffix = apiNameSuffix;
    }

    public String getLinkedUserUuid() {
        return linkedUserUuid;
    }

    public void setLinkedUserUuid(String linkedUserUuid) {
        this.linkedUserUuid = linkedUserUuid;
    }

    @Override
    public String toString() {
        return "AddSubMerchantAccountRequest{" +
                "name='" + name + '\'' +
                ", apiNameSuffix='" + apiNameSuffix + '\'' +
                ", linkedUserUuid='" + linkedUserUuid + '\'' +
                '}';
    }

}
