package com.best.grocery.entity;



import java.util.List;

/**
 * Created by TienTruong on 7/20/2018.
 */

public class Category {
    private String id;
    private String name;
    private int orderView;
    @SuppressWarnings("FieldCanBeLocal")
    private List<Product> products;

    public Category() {
    }

    public Category(String id, String name, int orderView, List<Product> products) {
        this.id = id;
        this.name = name;
        this.orderView = orderView;
        this.products = products;
    }

    public int getOrderView() {
        return orderView;
    }

    public void setOrderView(int orderView) {
        this.orderView = orderView;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}

