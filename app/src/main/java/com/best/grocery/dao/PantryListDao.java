package com.best.grocery.dao;


import com.best.grocery.entity.PantryList;

import java.util.ArrayList;

public interface PantryListDao {
    PantryList findById(String id);
    PantryList findByName(String name);
    ArrayList<PantryList> fetchAll();
    // add user
    boolean create(PantryList pantryList);
    boolean update(PantryList pantryList);
    boolean delete(PantryList pantryList);
    PantryList fetchListActive();
}
