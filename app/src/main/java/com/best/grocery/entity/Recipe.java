package com.best.grocery.entity;

import java.util.ArrayList;

/**
 * Created by TienTruong on 7/30/2018.
 */

public class Recipe {
    private String code;
    private String name;
    private String description;
    private ArrayList<String> instrutions;
    private String image;
    private String url;
    private String type;
    private ArrayList<String> ingredients;

    public Recipe() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getInstrutions() {
        return instrutions;
    }

    public void setInstrutions(ArrayList<String> instrutions) {
        this.instrutions = instrutions;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
