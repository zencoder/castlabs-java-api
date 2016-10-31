package com.brightcove.castlabs.client.response;

public class AddSubMerchantAccountResponse {

    private String subMerchantUuid;

    public String getSubMerchantUuid() {
        return subMerchantUuid;
    }

    public void setSubMerchantUuid(String subMerchantUuid) {
        this.subMerchantUuid = subMerchantUuid;
    }

    @Override
    public String toString() {
        return "AddSubMerchantAccountResponse{" +
                "subMerchantUuid='" + subMerchantUuid + '\'' +
                '}';
    }

}
