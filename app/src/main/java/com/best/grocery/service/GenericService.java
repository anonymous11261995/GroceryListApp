package com.best.grocery.service;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.best.grocery.AppConfig;
import com.best.grocery.dao.CategoryDao;
import com.best.grocery.dao.PantryListDao;
import com.best.grocery.dao.ProductDao;
import com.best.grocery.dao.ShoppingListDao;
import com.best.grocery.database.DatabaseHelper;
import com.best.grocery.entity.Category;
import com.best.grocery.entity.PantryList;
import com.best.grocery.entity.Product;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.utils.DefinitionSchema;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TienTruong on 8/2/2018.
 */

@SuppressWarnings("CanBeFinal")
public class GenericService implements DefinitionSchema {
    ProductDao mProductDao;
    ShoppingListDao mShoppingListDao;
    CategoryDao mCategoryDao;
    PantryListDao mPantryListDao;
    Context mContext;

    public GenericService(Context context) {
        this.mProductDao = DatabaseHelper.mProductDao;
        this.mShoppingListDao = DatabaseHelper.mShoppingListDao;
        this.mCategoryDao = DatabaseHelper.mCategoryDao;
        this.mPantryListDao = DatabaseHelper.mPantryListDao;
        this.mContext = context;
    }


    public String createCodeId(String name, Date date) {
        String textTime = String.valueOf(date.getTime());
        String code = name + "-" + textTime;
        return convertStringToUrl(code);
    }

    public boolean checkBeforeUpdateList(String name){
        if (name.trim().equals("")) {
            return false;
        }
        ArrayList<ShoppingList> shoppingLists = mShoppingListDao.fetchAllShoppingList();
        ArrayList<PantryList> pantryLists = mPantryListDao.fetchAll();
        for (ShoppingList shoppingList : shoppingLists) {
            if (name.equals(shoppingList.getName())) {
                return false;
            }
        }
        for(PantryList pantryList: pantryLists){
            if(name.equals(pantryList.getName())){
                return false;
            }
        }
        return true;
    }

    private String convertStringToUrl(String str) {
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll(" ", "-").replaceAll("đ", "d").replaceAll("[^a-zA-Z0-9-]", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    void orderProductInGroup(Category category, ShoppingList shoppingList) {
        String idCategory = category.getId();
        ArrayList<Product> list = mProductDao.getProductByCategoryAndShopping(idCategory, shoppingList.getId());
        for (int i = 0; i < list.size(); i++) {
            Product product = list.get(i);
            product.setOrderInGroup(i + 1);
            mProductDao.update(product);
        }
    }

    public Map<String, Object> fetchToFirestore() {
        Map<String, Object> result = new HashMap<>();
        ArrayList<Product> products = mProductDao.getAllProductUser();
        ArrayList<Map<String, Object>> mapProducts = new ArrayList<>();
        for (Product p : products) {
            Map<String, Object> map = new HashMap<>();
            map.put("category", p.getCategory().getId());
            map.put("shopping_list", p.getShoppingList().getId());
            map.put("pantry", p.getPantryList().getId());
            map.put("name", p.getName());
            map.put("created", p.getCreated());
            if (p.getExpired().getTime() == (new Date(0)).getTime()) {
                map.put("expired", null);
            } else {
                map.put("expired", p.getExpired());
            }
            map.put("note", p.getNote());
            map.put("quantity", p.getQuantity());
            map.put("unit", p.getUnit());
            map.put("unit_price", p.getUnitPrice());
            map.put("is_autocomplete", p.isHistory());
            map.put("is_bought", p.isChecked());
            map.put("state", p.getState());
            mapProducts.add(map);
        }
        result.put("products", mapProducts);
        result.put("total_products", products.size());
        //Shopping
        ArrayList<ShoppingList> shoppingLists = mShoppingListDao.fetchAllShoppingList();
        ArrayList<Map<String, Object>> mapShopping = new ArrayList<>();
        for (ShoppingList item : shoppingLists) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", item.getName());
            ArrayList<Product> productShopping = mProductDao.getAllProductShopping(item.getId());
            ArrayList<String> names = new ArrayList<>();
            for (Product product : productShopping) {
                names.add(product.getName());
            }
            map.put("items", names);
            map.put("total_items", productShopping.size());
            mapShopping.add(map);

        }
        result.put("total_list_shopping", shoppingLists.size());
        result.put("shoppings", mapShopping);
        //Pantry
        ArrayList<PantryList> pantryLists = mPantryListDao.fetchAll();
        ArrayList<Map<String, Object>> mapPantry = new ArrayList<>();
        for (PantryList item : pantryLists) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", item.getName());
            ArrayList<Product> productPantry = mProductDao.getAllProductPantry(item.getId());
            ArrayList<String> names = new ArrayList<>();
            for (Product product : productPantry) {
                names.add(product.getName());
            }
            map.put("items", names);
            map.put("total_items", productPantry.size());
            mapPantry.add(map);

        }
        result.put("total_list_pantry", pantryLists.size());
        result.put("pantries", mapPantry);
        return result;
    }

    public String buildQueryIn(ArrayList<String> list) {
        String query;
        int size = list.size();
        if (size == 0) {
            query = "''";
        } else if (size == 1) {
            query = "('" + list.get(0) + "')";
        } else {
            query = "(";
            for (int i = 0; i < size - 1; i++) {
                query = query + "'" + list.get(i) + "',";
            }
            query = query + "'" + list.get(size - 1) + "')";
        }
        return query;
    }


    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public int regexNumber(String text) {
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(text);
        while (m.find()) {
            return Integer.parseInt(m.group());
        }
        return 0;
    }

    List<String> sortByFrequencyItem(List<String> input) {
        List<String> listLowerCase = new ArrayList<>();
        for (String item : input) {
            listLowerCase.add(item.toLowerCase().trim());
        }

        Map<String, Integer> map = new HashMap<>();
        for (String s : listLowerCase) {
            if (map.containsKey(s)) {
                map.put(s, map.get(s) + 1);
            } else {
                map.put(s, 1);
            }
        }
        Set<Map.Entry<String, Integer>> set = map.entrySet();
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(set);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        List<String> output = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : list) {
            output.add(upperCaseFirstChar(entry.getKey()));
        }
        return output;
    }

    public String upperCaseFirstChar(String text) {
        if (text == null) return "";
        if (text.equals("")) return "";
        return text.toUpperCase().charAt(0) + text.substring(1, text.length());
    }

    public String saveToInternalStorage(Bitmap bitmapImage, Context context, String name) {


        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir

        String name_ = AppConfig.RECIPE_BOOK_FOLDER_NAME; //Folder name in device android/data/
        File directory = cw.getDir(name_, Context.MODE_PRIVATE);

        // Create imageDir
        File mypath = new File(directory, name + ".png");

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.d("ABCD", "Save imgae sucess");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("absolutepath ", directory.getAbsolutePath());
        return directory.getAbsolutePath();
    }

    /**
     * Method to retrieve image from your device
     **/
    public void deleteImage(Context context, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        String name_ = AppConfig.RECIPE_BOOK_FOLDER_NAME; //Folder name in device android/data/
        File directory = cw.getDir(name_, Context.MODE_PRIVATE);
        try {
            File f = new File(directory, name + ".png");
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap loadImageFromStorage(Context context, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        String name_ = AppConfig.RECIPE_BOOK_FOLDER_NAME; //Folder name in device android/data/
        File directory = cw.getDir(name_, Context.MODE_PRIVATE);
        Bitmap b;
        try {
            File f = new File(directory, name + ".png");
            b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, Character> map = new LinkedHashMap<>();

    static {
        map.put("&quot;", (char) 34);
        map.put("&amp;", (char) 38);
        map.put("&lt;", (char) 60);
        map.put("&gt;", (char) 62);
        map.put("&nbsp;", (char) 160);
        map.put("&iexcl;", (char) 161);
        map.put("&cent;", (char) 162);
        map.put("&pound;", (char) 163);
        map.put("&curren;", (char) 164);
        map.put("&yen;", (char) 165);
        map.put("&brvbar;", (char) 166);
        map.put("&sect;", (char) 167);
        map.put("&uml;", (char) 168);
        map.put("&copy;", (char) 169);
        map.put("&ordf;", (char) 170);
        map.put("&laquo;", (char) 171);
        map.put("&not;", (char) 172);
        map.put("&shy;", (char) 173);
        map.put("&reg;", (char) 174);
        map.put("&macr;", (char) 175);
        map.put("&deg;", (char) 176);
        map.put("&plusmn;", (char) 177);
        map.put("&sup2;", (char) 178);
        map.put("&sup3;", (char) 179);
        map.put("&acute;", (char) 180);
        map.put("&micro;", (char) 181);
        map.put("&para;", (char) 182);
        map.put("&middot;", (char) 183);
        map.put("&cedil;", (char) 184);
        map.put("&sup1;", (char) 185);
        map.put("&ordm;", (char) 186);
        map.put("&raquo;", (char) 187);
        map.put("&frac14;", (char) 188);
        map.put("&frac12;", (char) 189);
        map.put("&frac34;", (char) 190);
        map.put("&iquest;", (char) 191);
        map.put("&times;", (char) 215);
        map.put("&divide;", (char) 247);
        map.put("&Agrave;", (char) 192);
        map.put("&Aacute;", (char) 193);
        map.put("&Acirc;", (char) 194);
        map.put("&Atilde;", (char) 195);
        map.put("&Auml;", (char) 196);
        map.put("&Aring;", (char) 197);
        map.put("&AElig;", (char) 198);
        map.put("&Ccedil;", (char) 199);
        map.put("&Egrave;", (char) 200);
        map.put("&Eacute;", (char) 201);
        map.put("&Ecirc;", (char) 202);
        map.put("&Euml;", (char) 203);
        map.put("&Igrave;", (char) 204);
        map.put("&Iacute;", (char) 205);
        map.put("&Icirc;", (char) 206);
        map.put("&Iuml;", (char) 207);
        map.put("&ETH;", (char) 208);
        map.put("&Ntilde;", (char) 209);
        map.put("&Ograve;", (char) 210);
        map.put("&Oacute;", (char) 211);
        map.put("&Ocirc;", (char) 212);
        map.put("&Otilde;", (char) 213);
        map.put("&Ouml;", (char) 214);
        map.put("&Oslash;", (char) 216);
        map.put("&Ugrave;", (char) 217);
        map.put("&Uacute;", (char) 218);
        map.put("&Ucirc;", (char) 219);
        map.put("&Uuml;", (char) 220);
        map.put("&Yacute;", (char) 221);
        map.put("&THORN;", (char) 222);
        map.put("&szlig;", (char) 223);
        map.put("&agrave;", (char) 224);
        map.put("&aacute;", (char) 225);
        map.put("&acirc;", (char) 226);
        map.put("&atilde;", (char) 227);
        map.put("&auml;", (char) 228);
        map.put("&aring;", (char) 229);
        map.put("&aelig;", (char) 230);
        map.put("&ccedil;", (char) 231);
        map.put("&egrave;", (char) 232);
        map.put("&eacute;", (char) 233);
        map.put("&ecirc;", (char) 234);
        map.put("&euml;", (char) 235);
        map.put("&igrave;", (char) 236);
        map.put("&iacute;", (char) 237);
        map.put("&icirc;", (char) 238);
        map.put("&iuml;", (char) 239);
        map.put("&eth;", (char) 240);
        map.put("&ntilde;", (char) 241);
        map.put("&ograve;", (char) 242);
        map.put("&oacute;", (char) 243);
        map.put("&ocirc;", (char) 244);
        map.put("&otilde;", (char) 245);
        map.put("&ouml;", (char) 246);
        map.put("&oslash;", (char) 248);
        map.put("&ugrave;", (char) 249);
        map.put("&uacute;", (char) 250);
        map.put("&ucirc;", (char) 251);
        map.put("&uuml;", (char) 252);
        map.put("&yacute;", (char) 253);
        map.put("&thorn;", (char) 254);
        map.put("&yuml;", (char) 255);
    }

    /**
     * Find the Html Entity and convert it back to a regular character if the
     * entity exists, otherwise return the same string.
     *
     * @param str
     * @return Character represented by HTML Entity or the same string if unknown entity.
     */
    private static String fromHtmlEntity(String str) {
        Character ch = map.get(str);
        return (ch != null) ? ch.toString() : str;
    }

    /**
     * Converts html entities (e.g. &amp;) to html (&amp; -> &)
     *
     * @param decode A string to be decoded.
     * @return The string decoded with no HTML entities.
     */
    public static String decode(String decode) {
        StringBuilder str = new StringBuilder(decode);
        Matcher m = Pattern.compile("&[A-Za-z]+;").matcher(str);
        String replaceStr = null;

        int matchPointer = 0;
        while (m.find(matchPointer)) {
            // check if we have a corresponding key in our map
            replaceStr = fromHtmlEntity(m.group());
            str.replace(m.start(), m.end(), replaceStr);
            matchPointer = m.start() + replaceStr.length();
        }

        return str.toString();
    }

    public JSONObject paserQuantityUnit(String text) {
        JSONObject json = new JSONObject();
        try {
            String name = text;
            float quanity = 1;
            String unit = "";
            String array[] = text.split(" ");
            for (int i = 0; i < array.length; i++) {
                //System.out.println(array[i]);
                if (array[i].matches("[+-]?([0-9]*[.])?[0-9]+") && i + 1 < array.length) {
                    quanity = Float.valueOf(array[i]);
                    unit = array[i + 1];
                }
            }
            String unitQuantity = String.valueOf(quanity).replace(".0", "") + " " + unit;
            json.put("name", name.replace(unitQuantity, "").trim());
            json.put("unit", unit);
            json.put("quantity", quanity);

        } catch (Exception e) {

        }
        return json;

    }

    public String escapeCharacterSpecial(String text) {
        return text.replace("'", "&#39;");

    }

}
