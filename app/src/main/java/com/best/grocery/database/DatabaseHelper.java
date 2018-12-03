package com.best.grocery.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.best.grocery.AppConfig;
import com.best.grocery.dao.CategoryDao;
import com.best.grocery.dao.CategoryDaoImpl;
import com.best.grocery.dao.PantryListDao;
import com.best.grocery.dao.PantryListDaoImpl;
import com.best.grocery.dao.ProductDao;
import com.best.grocery.dao.ProductDaoImpl;
import com.best.grocery.dao.RecipeDao;
import com.best.grocery.dao.RecipeDaoImpl;
import com.best.grocery.dao.ShoppingListDao;
import com.best.grocery.dao.ShoppingListDaoImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by TienTruong on 7/20/2018.
 */

@SuppressWarnings("CanBeFinal")
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";
    private static String DB_NAME = AppConfig.DATABASE_NAME;
    private static int DB_VERSION = AppConfig.DATABASE_VERSION;
    private SQLiteDatabase myDatabase;
    private Context mContext;
    public static ProductDao mProductDao;
    public static ShoppingListDao mShoppingListDao;
    public static CategoryDao mCategoryDao;
    public static RecipeDao mRecipeDao;
    public static PantryListDao mPantryListDao;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    public synchronized void openDataBase() {
        String dbPath = mContext.getDatabasePath(DB_NAME).getPath();
        Log.d(TAG, "Database openning...");
        myDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
        mProductDao = new ProductDaoImpl(myDatabase);
        mShoppingListDao = new ShoppingListDaoImpl(myDatabase);
        mCategoryDao = new CategoryDaoImpl(myDatabase);
        mRecipeDao = new RecipeDaoImpl(mContext);
        mPantryListDao = new PantryListDaoImpl(myDatabase);
    }

    public boolean isOpening() {
        if (myDatabase == null) {
            Log.d(TAG, "Database null");
            return false;
        }
        if (!myDatabase.isOpen()) {
            Log.d(TAG, "Database closed");
            return false;
        }
        Log.d(TAG, "Database openned");
        return true;

    }

    @Override
    public synchronized void close() {
        Log.d(TAG, "Database closed");
        super.close();
    }

    public boolean copyDatabase(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(DB_NAME);
            Log.d(TAG, "copy data");
            String outFilename = "data" + File.separator + "data" + File.separator + context.getPackageName() + File.separator + "databases" + File.separator + DB_NAME;
            Log.d(TAG, "Save to dir: " + outFilename);
            OutputStream outputStream = new FileOutputStream(outFilename);

            byte[] out = new byte[1024];
            int length;
            while ((length = inputStream.read(out)) > 0) {
                outputStream.write(out, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Eror " + e);
        }
        return false;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate sqlite");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade sqlite");

    }

}
