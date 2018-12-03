package com.best.grocery.dao;

import com.best.grocery.entity.Category;

import java.util.List;

/**
 * Created by TienTruong on 7/20/2018.
 */

public interface CategoryDao {
    List<Category> fetchAll();
    Category findById(String id);
    Category findByName(String name);

    Boolean createCategory(Category category);
    Boolean updateCategory(Category category);
    Boolean deleteCategory(Category category);
}
