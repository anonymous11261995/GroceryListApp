package com.best.grocery.dao;


import com.best.grocery.entity.Recipe;

import java.util.ArrayList;

/**
 * Created by TienTruong on 8/1/2018.
 */

public interface RecipeDao {
    ArrayList<Recipe> getAllRecipe();
    boolean addRecipeBook(Recipe recipe);
    boolean deleteRecipeBook(Recipe recipe);
    ArrayList<String> getWikiIngredient();
}
