package com.brightcove.castlabs.client.request;

import java.util.List;

public class LinkAccountToSubMerchantRequest {

    private String subMerchantUuid;
    private List<String> accountUuids;

    public String getSubMerchantUuid() {
        return subMerchantUuid;
    }

    public void setSubMerchantUuid(String subMerchantUuid) {
        this.subMerchantUuid = subMerchantUuid;
    }

    public List<String> getAccountUuids() {
        return accountUuids;
    }

    public void setAccountUuids(List<String> accountUuids) {
        this.accountUuids = accountUuids;
    }

    @Override
    public String toString() {
        return "LinkAccountToSubMerchantRequest{" +
                "subMerchantUuid='" + subMerchantUuid + '\'' +
                ", accountUuids=" + accountUuids +
                '}';
    }

}
