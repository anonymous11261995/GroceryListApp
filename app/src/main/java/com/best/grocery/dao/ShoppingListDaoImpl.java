package com.best.grocery.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.best.grocery.database.DBContentProvider;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;

/**
 * Created by TienTruong on 7/20/2018.
 */

public class ShoppingListDaoImpl extends DBContentProvider implements ShoppingListDao, DefinitionSchema {
    private Cursor cursor;
    private ContentValues initialValues;

    public ShoppingListDaoImpl(SQLiteDatabase db) {
        super(db);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ShoppingList cursorToEntity(Cursor cursor) {
        ShoppingList shoppingList = new ShoppingList();
        int idIndex, nameIndex, isActiveIndex, colorIndex;

        if (cursor != null) {
            if (cursor.getColumnIndex(COLUMN_ID) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                shoppingList.setId(cursor.getString(idIndex));
            }
            if (cursor.getColumnIndex(COLUMN_NAME) != -1) {
                nameIndex = cursor.getColumnIndexOrThrow(
                        COLUMN_NAME);
                shoppingList.setName(cursor.getString(nameIndex));
            }

            if (cursor.getColumnIndex(COLUMN_IS_ACVITE) != -1) {
                isActiveIndex = cursor.getColumnIndexOrThrow(COLUMN_IS_ACVITE);
                if (cursor.getInt(isActiveIndex) == 0) {
                    shoppingList.setActive(false);
                } else {
                    shoppingList.setActive(true);
                }
            }
            if(cursor.getColumnIndex(COLUMN_CODE_COLOR) != -1){
                colorIndex = cursor.getColumnIndexOrThrow(COLUMN_CODE_COLOR);
                shoppingList.setColor(cursor.getInt(colorIndex));
            }
        }

        return shoppingList;
    }


    @Override
    public ShoppingList fetchShoppingListById(String id) {
        final String selectionArgs[] = {String.valueOf(id)};
        final String selection = COLUMN_ID + " = ?";
        ShoppingList shoppingList = new ShoppingList();
        cursor = super.query(SHOPPING_LIST_TABLE, SHOPPING_LIST_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                shoppingList = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return shoppingList;
    }

    @Override
    public ShoppingList fetchShoppingListByName(String name) {
        final String selectionArgs[] = {String.valueOf(name)};
        final String selection = COLUMN_NAME + " = ?";
        ShoppingList shoppingList = new ShoppingList();
        cursor = super.query(SHOPPING_LIST_TABLE, SHOPPING_LIST_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                shoppingList = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return shoppingList;
    }

    @Override
    public ArrayList<ShoppingList> fetchAllShoppingList() {
        ArrayList<ShoppingList> list = new ArrayList<>();
        cursor = super.query(SHOPPING_LIST_TABLE, SHOPPING_LIST_COLUMNS, null,
                null, COLUMN_NAME);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ShoppingList item = cursorToEntity(cursor);
                list.add(item);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return list;
    }

    @Override
    public boolean create(ShoppingList shoppingList) {
        setContentValue(shoppingList);
        try {
            return super.insert(SHOPPING_LIST_TABLE, getContentValue()) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("DatabaseHelper", ex.getMessage());
            return false;
        }

    }

    @Override
    public boolean update(ShoppingList shoppingList) {
        final String selectionArgs[] = {String.valueOf(shoppingList.getId())};
        final String selection = COLUMN_ID + " = ?";
        setContentValue(shoppingList);
        try {
            return super.update(SHOPPING_LIST_TABLE, getContentValue(), selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Update database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(ShoppingList shoppingList) {
        final String selectionArgs[] = {String.valueOf(shoppingList.getId())};
        final String selection = COLUMN_ID + " = ?";
        try {
            return super.delete(SHOPPING_LIST_TABLE, selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Update database", ex.getMessage());
            return false;
        }
    }

    @Override
    public ShoppingList fetchShoppingListActive() {
        final String selectionArgs[] = {String.valueOf(1)};
        final String selection = COLUMN_IS_ACVITE + " = ?";
        ShoppingList shoppingList = new ShoppingList();
        cursor = super.query(SHOPPING_LIST_TABLE, SHOPPING_LIST_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                shoppingList = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return shoppingList;
    }

    private void setContentValue(ShoppingList shoppingList) {
        initialValues = new ContentValues();
        initialValues.put(COLUMN_ID, shoppingList.getId());
        initialValues.put(COLUMN_NAME, shoppingList.getName());
        initialValues.put(COLUMN_IS_ACVITE, shoppingList.isActive());
        initialValues.put(COLUMN_CODE_COLOR,shoppingList.getColor());
    }

    private ContentValues getContentValue() {
        return initialValues;
    }
}
