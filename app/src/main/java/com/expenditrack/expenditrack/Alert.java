package com.expenditrack.expenditrack;

/**
 * Created by Aaron on 22/03/2018.
 */

public class Alert {

    protected String alertID;
    protected String limit;
    protected String message;
    protected boolean active;

    public Alert(String alertID, String limit, String message, boolean active) {
        this.alertID = alertID;
        this.limit = limit;
        this.message = message;
        this.active = active;
    }

    public String getAlertID() {
        return alertID;
    }

    public String getLimit() {
        return limit;
    }

    public String getMessage() {
        return message;
    }

    public boolean isActive() {
        return active;
    }

    public void setAlertID(String alertID) {
        this.alertID = alertID;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
