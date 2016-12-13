package com.brightcove.castlabs.client.response;

import java.util.List;

public class Account {

    private String id;
    private Boolean enabled;
    private Boolean api;
    private String login;
    private String firstName;
    private String lastName;
    private List<String> permissions;

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getAPI() {
        return api;
    }

    public void setAPI(Boolean api) {
        this.api = api;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", enabled='" + enabled + '\'' +
                ", api='" + api + '\'' +
                ", login='" + login + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", permissions='" + permissions + '\'' +
                '}';
    }
}
