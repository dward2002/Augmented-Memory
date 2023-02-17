package uk.ac.wlv.augmentedmemory;

import java.io.Serializable;
import java.util.Date;

class Reminder implements Serializable {
    private String id;

    private String mTitle;
    private String name;
    private boolean read;
    private String date;
    private int requestCode;

    public Reminder() {
    }

    public Reminder(String mTitle, String name, String date, int requestCode) {
        this.mTitle = mTitle;
        this.name = name;
        this.date = date;
        this.requestCode = requestCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getmTitle() {return mTitle;}

    public void setmTitle(String mTitle) {this.mTitle = mTitle;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRead() {return read;}

    public void setRead(boolean read) {this.read = read;}

    public String getDate() {return date;}

    public void setDate(String date) {this.date = date;}

    public int getRequestCode() {return requestCode;}

    public void setRequestCode(int requestCode) {this.requestCode = requestCode;}
}
