package com.brightcove.castlabs.client.response;

import java.util.List;

public class ListAccountsResponse {

    private List<Account> accounts;

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return "ListAccountsResponse{" +
                "accounts='" + accounts + '\'' +
                '}';
    }

}
