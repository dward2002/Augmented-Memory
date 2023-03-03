package uk.ac.wlv.augmentedmemory;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String email;
    private String account;
    private String monitoredAccount;
    private String lastLocationLat;
    private String lastLocationLong;

    public User() {
    }

    public User(String email, String account) {
        this.email = email;
        this.account = account;
        this.lastLocationLat = lastLocationLat;
        this.lastLocationLong = lastLocationLong;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMonitoredAccount() {
        return monitoredAccount;
    }

    public void setMonitoredAccount(String monitoredAccount) {
        this.monitoredAccount = monitoredAccount;
    }

    public String getLastLocationLat() {
        return lastLocationLat;
    }

    public void setLastLocationLat(String lastLocationLat) {
        this.lastLocationLat = lastLocationLat;
    }

    public String getLastLocationLong() {
        return lastLocationLong;
    }

    public void setLastLocationLong(String lastLocationLong) {
        this.lastLocationLong = lastLocationLong;
    }

}
