package com.mygy.tanyafinances;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Operation implements Serializable {
    private double deltaMoney;
    private Category category;
    private String description;
    private Date date;

    public Operation(double deltaMoney, Category category,Date date, String description) {
        this.deltaMoney = deltaMoney;
        this.category = category;
        this.date = date;
        this.description = description;
    }
    public Operation(HashMap<String,Object> doc) {
        this.deltaMoney = (Double)doc.get("deltaMoney");
        this.description = (String) doc.get("description");
        this.date = ((Timestamp) doc.get("date")).toDate();

        HashMap<String,Object> categryMap = (HashMap<String,Object>)doc.get("category");
        String categoryName = (String)(categryMap).get("name");
        for(Category c:Category.getAllCategories()){
            if(c.getName().equals(categoryName)) {
                this.category = c;
                break;
            }
        }
    }
    public static List<Operation> getOperationsInDates(Date start, Date end){
        return MainActivity.user.getAllOperations().stream()
                .filter(o -> (start==null || !o.getDate().before(start)))
                .filter(o -> (end == null || !o.getDate().after(end)))
                .collect(Collectors.toList());
    }

    public double getDeltaMoney() {
        return deltaMoney;
    }
    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }
}
