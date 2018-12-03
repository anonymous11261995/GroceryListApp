package com.best.grocery.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.util.Log;

import com.best.grocery.AppConfig;
import com.best.grocery.dao.RecipeDao;
import com.best.grocery.database.DatabaseHelper;
import com.best.grocery.entity.Product;
import com.best.grocery.entity.Recipe;
import com.best.grocery.entity.ShoppingList;
import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by TienTruong on 7/30/2018.
 */

@SuppressWarnings("CanBeFinal")
public class RecipeService extends GenericService {
    private static final String TAG = RecipeService.class.getSimpleName();
    private RecipeDao mRecipeDao;
    private ProductService mProductService;
    private Context mContext;

    public RecipeService(Context context) {
        super(context);
        mRecipeDao = DatabaseHelper.mRecipeDao;
        mProductService = new ProductService(context);
        mContext = context;
    }

    public ArrayList<Recipe> myRecipe() {
        ArrayList<Recipe> result = new ArrayList<>();
        for (Recipe recipe : mRecipeDao.getAllRecipe()) {
            recipe.setName(decode(recipe.getName()));
            recipe.setDescription(decode(recipe.getDescription()));
            ArrayList<String> ingredients = new ArrayList<>();
            for (String text : recipe.getIngredients()) {
                ingredients.add(decode(text));
            }
            recipe.setIngredients(ingredients);
            ArrayList<String> intructions = new ArrayList<>();
            for (String text : recipe.getInstrutions()) {
                intructions.add(decode(text));
            }
            recipe.setInstrutions(intructions);
            result.add(recipe);
        }
        return result;
    }

    public ArrayList<Recipe> builDataList(Recipe recipe) {
        //Mỗi row là 1 recipe: dùng type để phân biệt
        ArrayList<Recipe> data = new ArrayList<>();
        Recipe recipeHeader = new Recipe();
        recipeHeader.setCode(recipe.getCode());
        recipeHeader.setName(recipe.getName());
        recipeHeader.setImage(recipe.getImage());
        recipeHeader.setDescription(recipe.getDescription());
        recipeHeader.setUrl(recipe.getUrl());
        recipeHeader.setType(RECIPE_TYPE_HEADER);
        data.add(recipeHeader);
        ArrayList<String> ingredients = recipe.getIngredients();
        Recipe recipeIngredientHeader = new Recipe();
        recipeIngredientHeader.setType(RECIPE_TYPE_INGREDIENTS_HEADER);
        if (ingredients.size() != 0) {
            data.add(recipeIngredientHeader);
        }
        for (String text : ingredients) {
            Recipe recipeItem = new Recipe();
            ArrayList<String> ing = new ArrayList<>();
            ing.add(text);
            recipeItem.setIngredients(ing);
            recipeItem.setType(RECIPE_TYPE_INGREDIENTS);
            data.add(recipeItem);
        }
        ArrayList<String> instructions = recipe.getInstrutions();
        Recipe recipeInstructionHeader = new Recipe();
        recipeInstructionHeader.setType(RECIPE_TYPE_INSTRUCTIONS_HEADER);
        if (instructions.size() != 0) {
            data.add(recipeInstructionHeader);
        }
        for (String text : instructions) {
            Recipe recipeItem = new Recipe();
            ArrayList<String> ins = new ArrayList<>();
            ins.add(upperCaseFirstChar(text));
            recipeItem.setInstrutions(ins);
            recipeItem.setType(RECIPE_TYPE_INSTRUCTION);
            data.add(recipeItem);
        }
        Log.d("ABCD", "size: " + data.size());
        return data;
    }


    public void addIngredientToList(String nameRecipe, ArrayList<String> mDataChecked, String nameShoppingList) {
        ShoppingListService shoppingListService = new ShoppingListService(mContext);
        ShoppingList shoppingList = shoppingListService.getListByName(nameShoppingList);
        shoppingListService.activeShopping(nameShoppingList);
        for (String text : mDataChecked) {
            Log.d(TAG, "Intgredient : " + text + " to shopping list: " + nameShoppingList);
            addToList(text, shoppingList, nameRecipe);
        }
    }


    public Recipe htmlToRecipe(String url) {
        Recipe recipe = null;
        try {
            Log.d(TAG, "Url get recipe: " + url);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Connection.Response response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .method(Connection.Method.GET) //or Method.POST
                    .execute();
            Document doc = response.parse();
            if (url.contains(AppConfig.SITE_ALLRECIPES)) {
                recipe = xpathSiteAllRecipe(doc);
            } else if (url.contains(AppConfig.SITE_COOKPAD)) {
                recipe = xpathSiteCookpad(doc);
            } else {
                recipe = xpathSiteGeninusKitchen(doc);
                recipe.setUrl(url);
            }
        } catch (Exception e) {
            Log.e("Error", "RecipeService -> : " + e);

        }
        return recipe;
    }

    public boolean addRecipeBook(Recipe recipe, Context context) {
        Bitmap theBitmap = getBitmapFromURL(recipe.getImage());
        if (theBitmap == null) {
            Log.e("Error", " Imgae not found");
        }
        saveToInternalStorage(theBitmap, context, recipe.getCode());
        return mRecipeDao.addRecipeBook(recipe);
    }

    public boolean deleteRecipeBook(Recipe recipe, Context context) {
        deleteImage(context, recipe.getCode());
        return mRecipeDao.deleteRecipeBook(recipe);
    }

    private boolean addToList(String nameIngredient, ShoppingList list, String nameRecipe) {

        Product p = mProductService.getProductSameName(nameIngredient);
        String name = "", unit = "";
        int quantity = 1;
        try {
            JSONObject json = paserQuantityUnit(nameIngredient);
            name = json.getString("name");
            unit = json.getString("unit");
            quantity = (int) json.getDouble("quantity");

        } catch (JSONException e) {
            Crashlytics.logException(e);
            Crashlytics.log(Log.ERROR, TAG_ERROR, "Error parser unit, quantity: " + e.getMessage());
        }
        if (name.equals("")) {
            name = nameIngredient;
            unit = "";
            quantity = 1;
        }
        p.setId(createCodeId(name, new Date()));
        p.setShoppingList(list);
        p.setName(name);
        p.setUnit(unit);
        p.setQuantity(quantity);
        p.setNote(nameRecipe);
        //default
        p.setOrderInGroup(0);
        orderProductInGroup(p.getCategory(), list);
        return mProductDao.create(p);
    }


    private Recipe xpathSiteCookpad(Document doc) {
        try {
            String url = doc.select("link[itemprop=url]").get(0).attr("href");
            String image = doc.select("meta[itemprop=image]").get(0).attr("content");
            String name = doc.select("h1[itemprop=name]").get(0).html();
            String description = doc.select("meta[itemprop=description]").get(0).attr("content");
            Elements eIngredients = doc.select("div[itemprop=ingredients]");
            Elements eInstructions = doc.select("div[itemprop=recipeInstructions]");
            ArrayList<String> ingredients = new ArrayList<>();
            for (Element element : eIngredients) {
                String ingredient = element.ownText();
                ingredients.add(ingredient);
            }
            ArrayList<String> instructions = new ArrayList<>();
            for (Element element : eInstructions) {
                Element e = element.select("p").first();
                String instruction = e.ownText();
                instructions.add(upperCaseFirstChar(instruction));
            }
            Recipe recipe = new Recipe();
            String code = createCodeId(name, new Date());
            recipe.setCode(code);
            recipe.setUrl(url);
            recipe.setName(name);
            recipe.setDescription(description);
            recipe.setImage(image);
            recipe.setIngredients(ingredients);
            recipe.setInstrutions(instructions);
            return recipe;
        } catch (Exception e) {
            Log.e("Error", "xpath_recipe: " + e);
            return null;
        }
    }


    public Bitmap loadRecipeImage(Context context, String name) {
        Bitmap b = loadImageFromStorage(context, name);
        return b;
    }

    private Recipe xpathSiteAllRecipe(Document document) {
        try {
            Recipe recipe = new Recipe();
            String url = document.select("link[itemprop=url]").get(0).attr("href");
            String image = document.select("img[itemprop=image]").get(0).attr("src");
            String name = document.select("h1[itemprop=name]").get(0).text();
            String description = document.select("div[itemprop=description]").get(0).text();
            Elements eIngredients = document.select("span[itemprop=recipeIngredient]");
            ArrayList<String> ingredients = new ArrayList<>();
            for (Element element : eIngredients) {
                String ingredient = element.ownText().trim();
                ingredients.add(ingredient);
                Log.d("xpath_recipe", "ingredient: " + ingredient);
            }
            Elements eInstructions = document.select("ol[itemprop=recipeInstructions] > li > span");
            ArrayList<String> instructions = new ArrayList<>();
            for (Element element : eInstructions) {
                String instruction = element.ownText().trim();
                instructions.add(upperCaseFirstChar(instruction));
                Log.d("xpath_recipe", "instruction: " + upperCaseFirstChar(instruction));
            }
            Log.d("xpath_recipe", "url: " + url + ", name: " + name + ", description: " + description
                    + ", image: " + image);
            String code = createCodeId(name, new Date());
            recipe.setCode(code);
            recipe.setUrl(url);
            recipe.setName(name.trim());
            recipe.setDescription(description.replace("\"", "").trim());
            recipe.setImage(image);
            recipe.setIngredients(ingredients);
            recipe.setInstrutions(instructions);
            return recipe;
        } catch (Exception e) {
            Log.e("Error", "xpath_recipe: " + e);
        }
        return null;
    }

    private Recipe xpathSiteGeninusKitchen(Document document) {
        try {
            Recipe recipe = new Recipe();
            Elements metaTags = document.select("script[type=application/ld+json]");
            for (Element e : metaTags) {
                String textJson = e.toString().replace("<script type=\"application/ld+json\">", "").replace("</script>", "");
                System.out.println(textJson);
                JSONObject json = new JSONObject(textJson);
                System.out.println(json.getString("@type"));
                if (json.getString("@type").equals("Recipe")) {
                    String name = json.getString("name");
                    String image = json.getString("image");
                    String description = json.getString("description");
                    System.out.println(name);
                    JSONArray arrIngredient = json.getJSONArray("recipeIngredient");
                    ArrayList<String> ingredients = new ArrayList<>();
                    for (int i = 0; i < arrIngredient.length(); i++) {
                        String ingredient = arrIngredient.getString(i);
                        ingredients.add(ingredient);
                        Log.d("xpath_recipe", "ingredient: " + ingredient);
                    }
                    JSONArray arrInstruction = json.getJSONArray("recipeInstructions");
                    ArrayList<String> instructions = new ArrayList<>();
                    for (int i = 0; i < arrInstruction.length(); i++) {
                        JSONObject jSONObject = arrInstruction.getJSONObject(i);
                        String instruction = jSONObject.getString("text");
                        instructions.add(upperCaseFirstChar(instruction));
                        Log.d("xpath_recipe", "instruction: " + upperCaseFirstChar(instruction));
                    }
                    Log.d("xpath_recipe", ", name: " + name + ", description: " + description
                            + ", image: " + image);

                    String code = createCodeId(name, new Date());
                    recipe.setCode(code);
                    recipe.setName(name.trim());
                    recipe.setDescription(description.trim());
                    recipe.setImage(image);
                    recipe.setIngredients(ingredients);
                    recipe.setInstrutions(instructions);
                    return recipe;

                }
            }


        } catch (Exception e) {
            Log.e("Error", "xpath_recipe: " + e);
        }
        return null;
    }

}
