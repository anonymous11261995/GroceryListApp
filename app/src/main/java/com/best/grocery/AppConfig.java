package com.best.grocery;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Currency;
import java.util.Locale;

/**
 * Created by TienTruong on 7/31/2018.
 */

public class AppConfig {
    public static final String URL_STORE = "https://play.google.com/store/apps/details?id=";
    public static final String URL_REDIRECT_TO_STORE = "http://get.blistapp.com";
    //
    public static final String DATABASE_NAME = "data.db";
    public static final int DATABASE_VERSION = 1;
    public static final String DATEBASE_FILE_NAME = "recipe_book.json";
    public static final String DATABASE_FILE_WIKI = "wiki_ingredient.json";
    public static final String RECIPE_BOOK_FOLDER_NAME = "recipe_book";
    public static final String SITE_COOKPAD = "https://cookpad.com";
    public static final String SITE_ALLRECIPES = "https://www.allrecipes.com";
    public static final String SITE_GENIUSKITCHEN = "https://www.geniuskitchen.com/recipe";
    public static final String URL_FEEDBACK = "https://www.messenger.com/t/blistapp";
    public static final String ADMOB_APP_ID = "ca-app-pub-6672972571022224~7914884706";
    // time 12h:  12 * 3600
    public static final int PENDING_REMOVAL_TIMEOUT = 2000;
    public static final int DELAY_EFFECT = 350;
    public static final int DELAY_TEXT_CHANED = 500;
    public static final long CONFIG_EXPIRE_SECOND = 60;
    public static final String DATE_SERVER_PATTERN = "yyyy-MM-dd";
    public static final String DATE_SERVER_PATTERN_FULL = "yyyy-MM-dd HH:mm";
    public static final int REQ_CODE_SPEECH_INPUT = 100;
    public static int ITEM_CACHE_LIST = 40;
    //firebase
    private static AppConfig _instance;
    private FirebaseRemoteConfig config;

    public static String getCurrencySymbol() {
        try {
            Currency currency = Currency.getInstance(Locale.getDefault());
            return currency.getSymbol();
        } catch (Exception e) {
            return "$";
        }

    }

    private AppConfig() {
    }

    public FirebaseRemoteConfig getConfig() {
        return this.config;
    }

    public void setConfig(FirebaseRemoteConfig config) {
        this.config = config;
    }

    public static AppConfig getInstance() {
        if (_instance == null) {
            _instance = new AppConfig();
        }
        return _instance;
    }

}
