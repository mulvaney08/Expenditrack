package com.expenditrack.expenditrack;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Aaron on 29/11/2017.
 */

public class Receipt {

    protected String username;
    protected String supplierName;
    protected String totalSpent;
    protected String timeStamp;
    protected String category;
    protected String id;


    public Receipt() {
    }

    public Receipt(String id) {
        this.id = id;
    }

    public Receipt(String username, String supplierName, String totalSpent, String timeStamp, String category) {
        this.username = username;
        this.supplierName = supplierName;
        this.totalSpent = totalSpent;
        this.timeStamp = timeStamp;
        this.category = category;
        this.id = UUID.randomUUID().toString();
    }

    public Receipt(String username, String supplierName, String totalSpent, String timeStamp, String category, String id) {
        this.username = username;
        this.supplierName = supplierName;
        this.totalSpent = totalSpent;
        this.timeStamp = timeStamp;
        this.category = category;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getTotalSpent() {
        return totalSpent;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setTotalSpent(String totalSpent) {
        this.totalSpent = totalSpent;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("supplierName", supplierName);
        result.put("timestamp", timeStamp);
        result.put("totalSpent", totalSpent);
        result.put("username", username);
        result.put("category", category);
        return result;
    }
}
