package com.best.grocery.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.best.grocery.database.DBContentProvider;
import com.best.grocery.entity.Category;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TienTruong on 7/20/2018.
 */

public class CategoryDaoImpl extends DBContentProvider implements DefinitionSchema, CategoryDao {
    private Cursor cursor;
    private ContentValues initialValues;

    public CategoryDaoImpl(SQLiteDatabase db) {
        super(db);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Category cursorToEntity(Cursor cursor) {
        Category category = new Category();
        int idIndex;
        int nameIndex;
        int orderViewIndex;

        if (cursor != null) {
            if (cursor.getColumnIndex(COLUMN_ID) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                category.setId(cursor.getString(idIndex));
            }
            if (cursor.getColumnIndex(COLUMN_NAME) != -1) {
                nameIndex = cursor.getColumnIndexOrThrow(
                        COLUMN_NAME);
                category.setName(cursor.getString(nameIndex));
            }
            if(cursor.getColumnIndex(COLUMN_ORDER_VIEW) != -1){
                orderViewIndex = cursor.getColumnIndexOrThrow(COLUMN_ORDER_VIEW);
                category.setOrderView(cursor.getInt(orderViewIndex));
            }
        }
        return category;
    }

    @Override
    public List<Category> fetchAll() {
        List<Category> list = new ArrayList<>();
        cursor = super.query(CATEGORY_TABLE, CATEGORY_COLUMNS, null,
                null, COLUMN_ORDER_VIEW);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Category category = cursorToEntity(cursor);
                list.add(category);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return list;
    }

    @Override
    public Category findById(String id) {
        Category category = new Category();
        final String selectionArgs[] = {String.valueOf(id)};
        final String selection = COLUMN_ID + " = ?";
        cursor = super.query(CATEGORY_TABLE, CATEGORY_COLUMNS, selection,
                selectionArgs, COLUMN_ID);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                category = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return category;
    }

    @Override
    public Category findByName(String name) {
        Category category = new Category();
        final String selectionArgs[] = {String.valueOf(name)};
        final String selection = COLUMN_NAME + " = ?";
        cursor = super.query(CATEGORY_TABLE, CATEGORY_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                category = cursorToEntity(cursor);
                cursor.moveToNext();
                //Log.d("find item", category.getName());
            }
            cursor.close();
        }
        return category;
    }

    @Override
    public Boolean createCategory(Category category) {
        setContentValue(category);
        try {
            return super.insert(CATEGORY_TABLE, getContentValue()) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Create database: ", ex.getMessage());
            return false;
        }
    }

    @Override
    public Boolean deleteCategory(Category category) {
        final String selectionArgs[] = {String.valueOf(category.getId())};
        final String selection = COLUMN_ID + " = ?";
        try {
            return super.delete(CATEGORY_TABLE, selection,selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Delete database: ", ex.getMessage());
            return false;
        }
    }

    @Override
    public Boolean updateCategory(Category category) {
        final String selectionArgs[] = {String.valueOf(category.getId())};
        final String selection = COLUMN_ID + " = ?";
        setContentValue(category);
        try {
            return super.update(CATEGORY_TABLE, getContentValue(), selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Update database", ex.getMessage());
            return false;
        }
    }

    private void setContentValue(Category category) {
        initialValues = new ContentValues();
        initialValues.put(COLUMN_ID, category.getId());
        initialValues.put(COLUMN_NAME, category.getName());
        initialValues.put(COLUMN_ORDER_VIEW, category.getOrderView());
    }

    private ContentValues getContentValue() {
        return initialValues;
    }
}
