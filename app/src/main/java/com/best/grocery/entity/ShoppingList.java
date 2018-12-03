package com.best.grocery.entity;

import java.util.ArrayList;

/**
 * Created by TienTruong on 7/12/2018.
 */

public class ShoppingList {
    private String id;
    private String name;
    private int color;
    private boolean isActive;
    private ArrayList<Product> products;

    public ShoppingList() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}
