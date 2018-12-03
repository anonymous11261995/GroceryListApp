package com.best.grocery.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.best.grocery.database.DBContentProvider;
import com.best.grocery.entity.PantryList;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;

public class PantryListDaoImpl extends DBContentProvider implements PantryListDao, DefinitionSchema {
    private Cursor cursor;
    private ContentValues initialValues;

    public PantryListDaoImpl(SQLiteDatabase db) {
        super(db);
    }

    private void setContentValue(PantryList pantryList) {
        initialValues = new ContentValues();
        initialValues.put(COLUMN_ID, pantryList.getId());
        initialValues.put(COLUMN_NAME, pantryList.getName());
        initialValues.put(COLUMN_IS_ACVITE, pantryList.isActive());
    }

    private ContentValues getContentValue() {
        return initialValues;
    }

    @Override
    protected PantryList cursorToEntity(Cursor cursor) {
        PantryList list = new PantryList();
        int idIndex;
        int nameIndex;
        int isActiveIndex;

        if (cursor != null) {
            if (cursor.getColumnIndex(COLUMN_ID) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                list.setId(cursor.getString(idIndex));
            }
            if (cursor.getColumnIndex(COLUMN_NAME) != -1) {
                nameIndex = cursor.getColumnIndexOrThrow(
                        COLUMN_NAME);
                list.setName(cursor.getString(nameIndex));
            }

            if (cursor.getColumnIndex(COLUMN_IS_ACVITE) != -1) {
                isActiveIndex = cursor.getColumnIndexOrThrow(COLUMN_IS_ACVITE);
                if (cursor.getInt(isActiveIndex) == 0) {
                    list.setActive(false);
                } else {
                    list.setActive(true);
                }
            }
        }

        return list;
    }

    @Override
    public PantryList findById(String id) {
        final String selectionArgs[] = {String.valueOf(id)};
        final String selection = COLUMN_ID + " = ?";
        PantryList list = new PantryList();
        cursor = super.query(PANTRY_LIST_TABLE, PANTRY_LIST_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return list;
    }

    @Override
    public PantryList findByName(String name) {
        final String selectionArgs[] = {String.valueOf(name)};
        final String selection = COLUMN_NAME + " = ?";
        PantryList list = new PantryList();
        cursor = super.query(PANTRY_LIST_TABLE, PANTRY_LIST_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return list;
    }

    @Override
    public ArrayList<PantryList> fetchAll() {
        ArrayList<PantryList> list = new ArrayList<>();
        cursor = super.query(PANTRY_LIST_TABLE, PANTRY_LIST_COLUMNS, null,
                null, COLUMN_IS_ACVITE);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                PantryList item = cursorToEntity(cursor);
                list.add(item);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return list;
    }

    @Override
    public boolean create(PantryList pantryList) {
        setContentValue(pantryList);
        try {
            return super.insert(PANTRY_LIST_TABLE, getContentValue()) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("DatabaseHelper", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(PantryList pantryList) {
        final String selectionArgs[] = {String.valueOf(pantryList.getId())};
        final String selection = COLUMN_ID + " = ?";
        setContentValue(pantryList);
        try {
            return super.update(PANTRY_LIST_TABLE, getContentValue(), selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Update database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(PantryList pantryList) {
        final String selectionArgs[] = {String.valueOf(pantryList.getId())};
        final String selection = COLUMN_ID + " = ?";
        try {
            return super.delete(PANTRY_LIST_TABLE, selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Update database", ex.getMessage());
            return false;
        }
    }

    @Override
    public PantryList fetchListActive() {
        final String selectionArgs[] = {String.valueOf(1)};
        final String selection = COLUMN_IS_ACVITE + " = ?";
        PantryList pantryList = new PantryList();
        cursor = super.query(PANTRY_LIST_TABLE, PANTRY_LIST_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                pantryList = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return pantryList;
    }
}
