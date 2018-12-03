package com.best.grocery.dao;


import com.best.grocery.entity.Product;

import java.util.ArrayList;

/**
 * Created by TienTruong on 7/20/2018.
 */

public interface ProductDao {
    ArrayList<Product> fetchAll();

    ArrayList<Product> findByIdShopping(String name);

    ArrayList<Product> findByIdPantry(String name);

    ArrayList<Product> findByName(String name);

    Product findById(String id);

    boolean update(Product item);

    boolean create(Product item);

    ArrayList<Product> query(String query);

    ArrayList<Product> getProductByCategory(String idCategory);

    ArrayList<Product> getProductByCategoryAndShopping(String idCategory, String idShoppingList);

    ArrayList<Product> getAllProductUser();

//    ArrayList<Product> productPantryCategory(String idPantry);
//
//    ArrayList<Product> productPantryNoCategory(String idPantry);

    ArrayList<Product> getProductByCategoryAndPantry(String idCategory, String idPantry);

    ArrayList<Product> getAllProductShopping(String idList);

    ArrayList<Product> getAllProductPantry(String idList);

    ArrayList<Product> findByQuery(String query);


}
