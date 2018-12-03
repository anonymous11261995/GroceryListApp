package com.best.grocery.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.best.grocery.database.DBContentProvider;
import com.best.grocery.entity.Category;
import com.best.grocery.entity.PantryList;
import com.best.grocery.entity.Product;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by TienTruong on 7/20/2018.
 */

public class ProductDaoImpl extends DBContentProvider implements ProductDao, DefinitionSchema {
    private Cursor cursor;
    private ContentValues initialValues;

    public ProductDaoImpl(SQLiteDatabase db) {
        super(db);
    }


    @Override
    public ArrayList<Product> fetchAll() {
        ArrayList<Product> list = new ArrayList<>();
        cursor = super.query(PRODUCT_USER_TABLE, PRODUCT_COLUMNS, null,
                null, COLUMN_CREATE);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product product = cursorToEntity(cursor);
                list.add(product);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return list;
    }

    @Override
    public ArrayList<Product> findByIdShopping(String id) {
        final String selectionArgs[] = {String.valueOf(id)};
        final String selection = COLUMN_ID_SHOPPING_LIST + " = ?";
        ArrayList<Product> list = new ArrayList<>();
        cursor = super.query(PRODUCT_USER_TABLE, PRODUCT_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product product = cursorToEntity(cursor);
                list.add(product);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return list;
    }

    @Override
    public ArrayList<Product> findByIdPantry(String id) {
        final String selectionArgs[] = {String.valueOf(id)};
        final String selection = COLUMN_ID_PANTRY_LIST + " = ?";
        ArrayList<Product> list = new ArrayList<>();
        cursor = super.query(PRODUCT_USER_TABLE, PRODUCT_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product product = cursorToEntity(cursor);
                list.add(product);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return list;
    }

    @Override
    public ArrayList<Product> findByName(String name) {
        name = escape(name);
        final String selection = "lower(" + COLUMN_NAME + ") = '" + name.toLowerCase() + "'";
        ArrayList<Product> list = new ArrayList<>();
        cursor = super.query(PRODUCT_USER_TABLE, PRODUCT_COLUMNS, selection,
                null, COLUMN_MODIFIED);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product product = cursorToEntity(cursor);
                list.add(product);
                cursor.moveToNext();
            }
            cursor.close();
        }
        Collections.reverse(list);
        return list;
    }

    @Override
    public Product findById(String id) {
        final String selectionArgs[] = {String.valueOf(id)};
        final String selection = COLUMN_ID + " = ?";
        Product product = new Product();
        cursor = super.query(PRODUCT_USER_TABLE, PRODUCT_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                product = cursorToEntity(cursor);
                return product;
            }
            cursor.close();
        }
        return product;

    }

    @Override
    public boolean update(Product product) {
        final String selectionArgs[] = {String.valueOf(product.getId())};
        final String selection = COLUMN_ID + " = ?";
        setContentValue(product);
        try {
            return super.update(PRODUCT_USER_TABLE, getContentValue(), selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("DatabaseHelper", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean create(Product product) {
        setContentValue(product);
        try {
            return super.insert(PRODUCT_USER_TABLE, getContentValue()) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("DatabaseHelper", ex.getMessage());
            return false;
        }

    }

    @Override
    public ArrayList<Product> query(String query) {
        ArrayList<Product> list = new ArrayList<>();
        cursor = super.query(PRODUCT_USER_TABLE, PRODUCT_COLUMNS, query, null, COLUMN_CREATE);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product product = cursorToEntity(cursor);
                list.add(product);
                cursor.moveToNext();
//                Log.d("find item last update ", "result : " + item.getName());
            }
            cursor.close();
        }
        return list;
    }


    @Override
    public ArrayList<Product> getProductByCategory(String idCategory) {
        final String selectionArgs[] = {String.valueOf(idCategory)};
        final String selection = COLUMN_ID_CATEGORY + " = ?";
        ArrayList<Product> list = new ArrayList<>();
        cursor = super.query(PRODUCT_USER_TABLE, PRODUCT_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product product = cursorToEntity(cursor);
                list.add(product);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return list;
    }


    @Override
    public ArrayList<Product> getProductByCategoryAndShopping(String idCategory, String idShoppingList) {
        final String selectionArgs[] = {idShoppingList, idCategory};
        final String selection = COLUMN_ID_SHOPPING_LIST + " = ? and " + COLUMN_ID_CATEGORY + " = ? ";
        ArrayList<Product> itemList = new ArrayList<>();
        cursor = super.query(PRODUCT_USER_TABLE, PRODUCT_COLUMNS, selection,
                selectionArgs, COLUMN_ORDER_IN_GROUP);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product p = cursorToEntity(cursor);
                itemList.add(p);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return itemList;
    }

    @Override
    public ArrayList<Product> getAllProductUser() {
        ArrayList<Product> list = new ArrayList<>();
        String query = "select * from product_user\n" +
                "order by product_user.created asc";
        cursor = super.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product p = cursorToEntity(cursor);
                list.add(p);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }

    @Override
    public ArrayList<Product> findByQuery(String query) {
        ArrayList<Product> list = new ArrayList<>();
        cursor = super.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product p = cursorToEntity(cursor);
                list.add(p);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return list;
    }

    @Override
    public ArrayList<Product> getProductByCategoryAndPantry(String idCategory, String idPantry) {
        final String selectionArgs[] = {idPantry, idCategory};
        final String selection = COLUMN_ID_PANTRY_LIST + " = ? and " + COLUMN_ID_CATEGORY + " = ? ";
        ArrayList<Product> itemList = new ArrayList<>();
        cursor = super.query(PRODUCT_USER_TABLE, PRODUCT_COLUMNS, selection,
                selectionArgs, COLUMN_ORDER_IN_GROUP);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product p = cursorToEntity(cursor);
                itemList.add(p);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return itemList;
    }

    @Override
    public ArrayList<Product> getAllProductShopping(String idList) {
        final String selectionArgs[] = {idList};
        final String selection = COLUMN_ID_SHOPPING_LIST + " = ? ";
        ArrayList<Product> result = new ArrayList<>();
        cursor = super.query(PRODUCT_USER_TABLE, PRODUCT_COLUMNS, selection,
                selectionArgs, COLUMN_ORDER_IN_GROUP);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product p = cursorToEntity(cursor);
                result.add(p);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return result;
    }

    @Override
    public ArrayList<Product> getAllProductPantry(String idList) {
        final String selectionArgs[] = {idList};
        final String selection = COLUMN_ID_PANTRY_LIST + " = ? ";
        ArrayList<Product> result = new ArrayList<>();
        cursor = super.query(PRODUCT_USER_TABLE, PRODUCT_COLUMNS, selection,
                selectionArgs, COLUMN_ORDER_IN_GROUP);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product p = cursorToEntity(cursor);
                result.add(p);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return result;
    }


    @Override
    protected Product cursorToEntity(Cursor cursor) {
        Product product = new Product();
        int idIndex, nameIndex, createIndex, modifiedIndex, noteIndex, quanityIndex, unitPriceIndex, isHistoryIndex,
                isCheckedIndex, idCategoryIndex, idShoppingListIndex, lastCheckedIndex, orderIndex, unitIndex,
                stateIndex, idPantryList, urlIndex, expiredIndex, hidenIndex;
        if (cursor != null) {
            if (cursor.getColumnIndex(COLUMN_ID) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                product.setId(cursor.getString(idIndex));
            }
            if (cursor.getColumnIndex(COLUMN_NAME) != -1) {
                nameIndex = cursor.getColumnIndexOrThrow(
                        COLUMN_NAME);
                String name = upperCaseFirstChar(cursor.getString(nameIndex));
                product.setName(upcape(name));
            }
            if (cursor.getColumnIndex(COLUMN_CREATE) != -1) {
                createIndex = cursor.getColumnIndexOrThrow(
                        COLUMN_CREATE);
                product.setCreated(new Date(cursor.getLong(createIndex)));
            }
            if (cursor.getColumnIndex(COLUMN_MODIFIED) != -1) {
                modifiedIndex = cursor.getColumnIndexOrThrow(COLUMN_MODIFIED);
                product.setModified(new Date(cursor.getLong(modifiedIndex)));
            }
            if (cursor.getColumnIndex(COLUMN_NOTE) != -1) {
                noteIndex = cursor.getColumnIndexOrThrow(COLUMN_NOTE);
                product.setNote(cursor.getString(noteIndex));
            }
            if (cursor.getColumnIndex(COLUMN_QUANITY) != -1) {
                quanityIndex = cursor.getColumnIndexOrThrow(COLUMN_QUANITY);
                product.setQuantity(cursor.getInt(quanityIndex));
            }
            if (cursor.getColumnIndex(COLUMN_UNIT) != -1) {
                unitIndex = cursor.getColumnIndexOrThrow(COLUMN_UNIT);
                product.setUnit(cursor.getString(unitIndex));
            }
            if (cursor.getColumnIndex(COLUMN_UNIT_PRICE) != -1) {
                unitPriceIndex = cursor.getColumnIndexOrThrow(COLUMN_UNIT_PRICE);
                product.setUnitPrice(cursor.getDouble(unitPriceIndex));
            }
            if (cursor.getColumnIndex(COLUMN_IS_HISTORY) != -1) {
                isHistoryIndex = cursor.getColumnIndexOrThrow(COLUMN_IS_HISTORY);
                if (cursor.getInt(isHistoryIndex) == 0) {
                    product.setHistory(false);
                } else {
                    product.setHistory(true);
                }
            }
            if (cursor.getColumnIndex(COLUMN_IS_CHECKED) != -1) {
                isCheckedIndex = cursor.getColumnIndexOrThrow(COLUMN_IS_CHECKED);
                if (cursor.getInt(isCheckedIndex) == 0) {
                    product.setChecked(false);
                } else {
                    product.setChecked(true);
                }
            }
            if (cursor.getColumnIndex(COLUMN_ID_CATEGORY) != -1) {
                idCategoryIndex = cursor.getColumnIndexOrThrow(COLUMN_ID_CATEGORY);
                Category category = new Category();
                category.setId(cursor.getString(idCategoryIndex));
                product.setCategory(category);
            }
            if (cursor.getColumnIndex(COLUMN_ID_SHOPPING_LIST) != -1) {
                idShoppingListIndex = cursor.getColumnIndexOrThrow(COLUMN_ID_SHOPPING_LIST);
                ShoppingList shoppingList = new ShoppingList();
                shoppingList.setId(cursor.getString(idShoppingListIndex));
                product.setShoppingList(shoppingList);
            }
            if (cursor.getColumnIndex(COLUMN_LAST_CHECKED) != -1) {
                lastCheckedIndex = cursor.getColumnIndexOrThrow(COLUMN_LAST_CHECKED);
                product.setLastChecked(new Date(cursor.getLong(lastCheckedIndex)));
            }
            if (cursor.getColumnIndex(COLUMN_ORDER_IN_GROUP) != -1) {
                orderIndex = cursor.getColumnIndexOrThrow(COLUMN_ORDER_IN_GROUP);
                product.setOrderInGroup(cursor.getInt(orderIndex));
            }
            if (cursor.getColumnIndex(COLUMN_ID_STATE) != -1) {
                stateIndex = cursor.getColumnIndexOrThrow(COLUMN_ID_STATE);
                product.setState(cursor.getString(stateIndex));
            }
            if (cursor.getColumnIndex(COLUMN_ID_PANTRY_LIST) != -1) {
                idPantryList = cursor.getColumnIndexOrThrow(COLUMN_ID_PANTRY_LIST);
                PantryList pantryList = new PantryList();
                pantryList.setId(cursor.getString(idPantryList));
                product.setPantryList(pantryList);
            }
            if (cursor.getColumnIndex(COLUMN_SRC_URL) != -1) {
                urlIndex = cursor.getColumnIndexOrThrow(COLUMN_SRC_URL);
                product.setUrl(cursor.getString(urlIndex));
            }

            if (cursor.getColumnIndex(COLUMN_EXPIRED) != -1) {
                expiredIndex = cursor.getColumnIndexOrThrow(COLUMN_EXPIRED);
                long expiredTime = cursor.getLong(expiredIndex);
                if (expiredIndex != 0) {
                    product.setExpired(new Date(expiredTime));
                }
            }
            if (cursor.getColumnIndex(COLUMN_PRODUCT_HIDE) != -1) {
                hidenIndex = cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_HIDE);
                boolean state;
                String text = cursor.getString(hidenIndex);
                if (text == null) {
                    state = false;
                } else if (text.equals("")) {
                    state = false;
                } else {
                    state = true;
                }
                product.setHide(state);
            }

        }

        return product;
    }


    private void setContentValue(Product p) {
        try {
            initialValues = new ContentValues();
            initialValues.put(COLUMN_ID, p.getId());
            initialValues.put(COLUMN_NAME, escape(p.getName()));
            initialValues.put(COLUMN_CREATE, p.getCreated().getTime());
            initialValues.put(COLUMN_MODIFIED, p.getModified().getTime());
            initialValues.put(COLUMN_NOTE, p.getNote());
            initialValues.put(COLUMN_SRC_URL, p.getUrl());
            initialValues.put(COLUMN_QUANITY, p.getQuantity());
            initialValues.put(COLUMN_UNIT, p.getUnit());
            initialValues.put(COLUMN_UNIT_PRICE, p.getUnitPrice());
            initialValues.put(COLUMN_IS_HISTORY, p.isHistory());
            initialValues.put(COLUMN_IS_CHECKED, p.isChecked());
            initialValues.put(COLUMN_ID_CATEGORY, p.getCategory().getId());
            if (p.getExpired() != null) {
                initialValues.put(COLUMN_EXPIRED, p.getExpired().getTime());
            }
            if (p.getShoppingList() != null)
                initialValues.put(COLUMN_ID_SHOPPING_LIST, p.getShoppingList().getId());
            initialValues.put(COLUMN_LAST_CHECKED, p.getLastChecked().getTime());
            initialValues.put(COLUMN_ORDER_IN_GROUP, p.getOrderInGroup());
            if (p.getState() != null)
                initialValues.put(COLUMN_ID_STATE, p.getState());
            if (p.getPantryList() != null)
                initialValues.put(COLUMN_ID_PANTRY_LIST, p.getPantryList().getId());
            if (p.isHide()) {
                initialValues.put(COLUMN_PRODUCT_HIDE, 1);
            } else {
                initialValues.put(COLUMN_PRODUCT_HIDE, "");
            }
        } catch (Exception e) {
            Log.e("Errror", "[ProductDaoImpl] initValue: " + e.getMessage());
            initialValues = new ContentValues();
        }

    }

    private ContentValues getContentValue() {
        return initialValues;
    }

    private String upperCaseFirstChar(String text) {
        if (text == null) return "";
        if (text.equals("")) return "";
        return text.toUpperCase().charAt(0) + text.substring(1, text.length());
    }

}
