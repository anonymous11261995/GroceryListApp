package com.best.grocery.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by TienTruong on 7/20/2018.
 */

@SuppressWarnings("CanBeFinal")
public abstract class DBContentProvider {
    private SQLiteDatabase mDb;

    public int delete(String tableName, String selection,
                      String[] selectionArgs) {
        return mDb.delete(tableName, selection, selectionArgs);
    }

    public long insert(String tableName, ContentValues values) {
        return mDb.insert(tableName, null, values);
    }

    protected abstract <T> T cursorToEntity(Cursor cursor);

    protected DBContentProvider(SQLiteDatabase db) {
        this.mDb = db;
    }

    public Cursor query(String tableName, String[] columns,
                        String selection, String[] selectionArgs, String sortOrder) {

        return mDb.query(tableName, columns,
                selection, selectionArgs, null, null, sortOrder);
    }

    public Cursor query(String tableName, String[] columns,
                        String selection, String[] selectionArgs, String sortOrder,
                        String limit) {

        return mDb.query(tableName, columns, selection,
                selectionArgs, null, null, sortOrder, limit);
    }

    public Cursor query(String tableName, String[] columns,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy, String limit) {

        return mDb.query(tableName, columns, selection,
                selectionArgs, groupBy, having, orderBy, limit);
    }

    public int update(String tableName, ContentValues values,
                      String selection, String[] selectionArgs) {
        return mDb.update(tableName, values, selection,
                selectionArgs);
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return mDb.rawQuery(sql, selectionArgs);
    }

    public String escape(String text) {
        return text.replace("'", "&#39;");

    }

    public String upcape(String text) {
        return text.replace("&#39;", "'");
    }

}