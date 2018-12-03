package com.best.grocery.entity;

import android.util.Log;

import com.best.grocery.utils.DefinitionSchema;

import java.util.Date;

/**
 * Created by TienTruong on 7/12/2018.
 */

public class Product implements DefinitionSchema {
    private String id;
    private String name;
    private Date created;
    private Date modified;
    private int quantity;
    private String unit;
    private Double unitPrice;
    private String note;
    private boolean isHistory;
    private boolean isChecked;
    private Date lastChecked;
    private int orderInGroup;
    private String state;
    private String url;
    private Date expired;
    private boolean isHide;
    private PantryList pantryList;
    private Category category;
    private ShoppingList shoppingList;

    public Product() {
        this.note = "";
        this.unit = "";
        this.unitPrice = 0.0;
        this.quantity = 1;
        this.created = new Date();
        this.modified = new Date();
        this.setLastChecked(new Date());
        this.isHistory = true;
        this.isChecked = false;
        this.orderInGroup = 0;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public PantryList getPantryList() {
        return pantryList;
    }

    public void setPantryList(PantryList pantryList) {
        this.pantryList = pantryList;
    }

    public Date getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Date lastChecked) {
        this.lastChecked = lastChecked;
    }

    public int getOrderInGroup() {
        return orderInGroup;
    }

    public void setOrderInGroup(int orderInGroup) {
        this.orderInGroup = orderInGroup;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public String getContent() {
        String content = this.name;
        try {
            if (unit == null) {
                unit = "";
            }
            if (quantity != 1 && quantity != 0) {
                if (unit.equals("")) {
                    content = content + " (" + String.valueOf(quantity) + ")";
                } else {
                    content = content + " (" + String.valueOf(quantity) + " " + unit + ")";
                }
            }
            return content;
        } catch (Exception e) {
            Log.e("Error", "Product...");
            return content;
        }
    }


    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }
}
