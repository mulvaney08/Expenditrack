package com.expenditrack.expenditrack;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aaron on 29/11/2017.
 */

public class Receipt {

    private String username;
    private String supplierName;
    private double totalSpent;
    private String timeStamp;


    public Receipt(String username, String supplierName, double totalSpent, String timeStamp) {
        this.username = username;
        this.supplierName = supplierName;
        this.totalSpent = totalSpent;
        this.timeStamp = timeStamp;
    }

    public String getUsername() {
        return username;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
