package com.mygy.tanyafinances;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Category implements Serializable {
    private String name;
    private String description;
    private int imageRes;
    private static final ArrayList<Category> allCategories = new ArrayList<>();

    public Category(String name, String description, int imageRes) {
        this.name = name;
        this.description = description;
        this.imageRes = imageRes;
        allCategories.add(this);
        MainActivity.saveUser();
    }
    public Category(HashMap<String,Object> doc) {
        this.name = (String)doc.get("name");
        this.description = (String)doc.get("description");
        this.imageRes = ((Long)doc.get("imageRes")).intValue();
        allCategories.add(this);
        MainActivity.saveUser();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageRes() {
        return imageRes;
    }

    public static ArrayList<Category> getAllCategories() {
        return allCategories;
    }
    public static List<String> getAllCategoriesNames(){
        return allCategories.stream().map(Category::getName).collect(Collectors.toList());
    }
    public static Category getCategoryByName(String name){
        for(Category c:allCategories){
            if(c.getName().equals(name)) return c;
        }
        return null;
    }
}
