package com.best.grocery.dao;

import android.content.Context;
import android.util.Log;

import com.best.grocery.AppConfig;
import com.best.grocery.database.InternalStorage;
import com.best.grocery.entity.Recipe;
import com.best.grocery.utils.DefinitionSchema;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TienTruong on 8/1/2018.
 */

@SuppressWarnings("CanBeFinal")
public class RecipeDaoImpl implements DefinitionSchema, RecipeDao {
    private InternalStorage mSore;

    public RecipeDaoImpl(Context context) {
        this.mSore = new InternalStorage(context);
    }

    private Recipe jsonToEntity(JSONObject jsonObj) {
        Recipe recipe = new Recipe();
        try {
            recipe.setCode(jsonObj.getString(JSON_KEY_CODE));
            recipe.setName(jsonObj.getString(JSON_KEY_NAME));
            recipe.setDescription(jsonObj.getString(JSON_KEY_DESCRIPTION));
            recipe.setImage(jsonObj.getString(JSON_KEY_IMAGE));
            recipe.setUrl(jsonObj.getString(JSON_KEY_URL));
            ArrayList<String> instrutions = new ArrayList<>();
            JSONArray jsonArrayInstrutions = jsonObj.getJSONArray(JSON_KEY_INSTRUCTIONS);
            for (int i = 0; i < jsonArrayInstrutions.length(); i++) {
                instrutions.add(jsonArrayInstrutions.getString(i));
            }
            recipe.setInstrutions(instrutions);
            ArrayList<String> ingredients = new ArrayList<>();
            JSONArray jsonArray = jsonObj.getJSONArray(JSON_KEY_INGREDIENTS);
            for (int i = 0; i < jsonArray.length(); i++)
                ingredients.add(jsonArray.getString(i));
            recipe.setIngredients(ingredients);

        } catch (JSONException e) {
            Log.e("Error", " " + e.getMessage());
        }
        return recipe;
    }

    private JSONObject entityToJson(Recipe recipe) {
        JSONObject json = new JSONObject();
        try {
            json.put(JSON_KEY_CODE, recipe.getCode());
            json.put(JSON_KEY_NAME, recipe.getName());
            json.put(JSON_KEY_DESCRIPTION, recipe.getDescription());
            JSONArray jsonArrayInstrution = new JSONArray();
            for (String text : recipe.getInstrutions()) {
                jsonArrayInstrution.put(text);
            }
            json.put(JSON_KEY_INSTRUCTIONS, jsonArrayInstrution);
            json.put(JSON_KEY_IMAGE, recipe.getImage());
            json.put(JSON_KEY_URL, recipe.getUrl());
            JSONArray jsonArray = new JSONArray();
            for (String text : recipe.getIngredients()) {
                jsonArray.put(text);
            }
            json.put(JSON_KEY_INGREDIENTS, jsonArray);

        } catch (JSONException e) {
            Log.e("Error", "RecipeDaoImpl: " + e);
        }
        return json;
    }

    private JSONArray readFile() {
        String data = this.mSore.readFromFile(AppConfig.DATEBASE_FILE_NAME);
        try {
            return new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    @Override
    public ArrayList<Recipe> getAllRecipe() {
        ArrayList<Recipe> arrayList = new ArrayList<>();
        JSONArray jsonArray = readFile();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                Recipe recipe = jsonToEntity(object);
                arrayList.add(recipe);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    @Override
    public boolean addRecipeBook(Recipe recipe) {
        try {
            JSONObject jsonObject = entityToJson(recipe);
            JSONArray jsonArray = readFile();
            jsonArray.put(jsonObject);
            mSore.writeToFile(jsonArray.toString(), AppConfig.DATEBASE_FILE_NAME);
            return true;
        } catch (Exception e) {
            Log.e("Error", " " + e.getStackTrace());
            return false;
        }
    }

    @Override
    public boolean deleteRecipeBook(Recipe recipe) {
        try {
            String codeRecipe = recipe.getCode();
            JSONArray jsonArray = readFile();
            JSONArray jsonArrayNew = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String codeObject = jsonObject.getString(JSON_KEY_CODE);
                if (codeRecipe.equals(codeObject)) {
                    continue;
                }
                jsonArrayNew.put(jsonObject);
            }
            mSore.writeToFile(jsonArrayNew.toString(), AppConfig.DATEBASE_FILE_NAME);
            return true;
        } catch (Exception e) {
            Log.e("Error", "RecipeDaoImpl -> delete recipe_book");
            return false;
        }

    }

    @Override
    public ArrayList<String> getWikiIngredient() {
        ArrayList<String> list = new ArrayList<>();
        try {
            String data = this.mSore.readFromFile(AppConfig.DATABASE_FILE_WIKI);
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add((String) jsonArray.get(i));
            }
            return list;
        } catch (Exception e) {
            Log.e("Error", "Read wiki ingredient");
            return new ArrayList<>();
        }
    }
}
