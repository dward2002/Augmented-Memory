package uk.ac.wlv.augmentedmemory;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String email;
    private String account;
    private String monitoredAccount;
    private String lastLocation;
    private String lastDate;

    public User() {
    }

    public User(String email, String account) {
        this.email = email;
        this.account = account;

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

    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }
}
