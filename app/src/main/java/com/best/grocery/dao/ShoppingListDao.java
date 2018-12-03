package com.best.grocery.dao;


import com.best.grocery.entity.ShoppingList;

import java.util.ArrayList;

/**
 * Created by TienTruong on 7/20/2018.
 */

public interface ShoppingListDao {
     ShoppingList fetchShoppingListById(String id);
     ShoppingList fetchShoppingListByName(String name);
     ArrayList<ShoppingList> fetchAllShoppingList();
    // add user
     boolean create(ShoppingList shoppingList);
     boolean update(ShoppingList shoppingList);
     boolean delete(ShoppingList shoppingList);
     ShoppingList fetchShoppingListActive();
}
