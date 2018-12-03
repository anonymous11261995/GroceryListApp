package com.best.grocery.utils;

/**
 * Created by TienTruong on 7/20/2018.
 */

public interface DefinitionSchema {
    //denifition database
    String HISTORY_TABLE = "product_system";
    String PRODUCT_USER_TABLE = "product_user";
    String SHOPPING_LIST_TABLE = "shopping_list";
    String PANTRY_LIST_TABLE = "pantry_list";
    String CATEGORY_TABLE = "category";
    String COLUMN_ID = "id";
    String COLUMN_NAME = "name";
    String COLUMN_ID_CATEGORY = "id_category";
    String COLUMN_ID_SHOPPING_LIST = "id_shopping_list";
    String COLUMN_CREATE = "created";
    String COLUMN_MODIFIED = "modified";
    String COLUMN_NOTE = "note";
    String COLUMN_QUANITY = "quanity";
    String COLUMN_UNIT = "unit";
    String COLUMN_UNIT_PRICE = "unit_price";
    String COLUMN_IS_HISTORY = "is_history";
    String COLUMN_IS_CHECKED = "is_checked";
    String COLUMN_IS_ACVITE = "is_active";
    String COLUMN_ORDER_VIEW = "order_view";
    String COLUMN_ORDER_IN_GROUP = "order_in_group";
    String COLUMN_LAST_CHECKED = "last_checked";
    String COLUMN_ID_PANTRY_LIST = "id_pantry_list";
    String COLUMN_ID_STATE = "state";
    String COLUMN_SRC_URL = "src_url";
    String COLUMN_EXPIRED = "field_1";
    String COLUMN_CODE_COLOR = "field_1";
    String COLUMN_PRODUCT_HIDE = "field_2";

    String[] PANTRY_LIST_COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_IS_ACVITE};
    String[] PRODUCT_COLUMNS = new String[]{COLUMN_ID, COLUMN_ORDER_IN_GROUP, COLUMN_LAST_CHECKED, COLUMN_ID_CATEGORY, COLUMN_ID_SHOPPING_LIST, COLUMN_NAME, COLUMN_CREATE, COLUMN_MODIFIED, COLUMN_NOTE, COLUMN_QUANITY, COLUMN_UNIT, COLUMN_UNIT_PRICE, COLUMN_IS_HISTORY, COLUMN_IS_CHECKED, COLUMN_ID_PANTRY_LIST, COLUMN_ID_STATE, COLUMN_SRC_URL, COLUMN_PRODUCT_HIDE};
    String[] SHOPPING_LIST_COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_IS_ACVITE, COLUMN_CODE_COLOR};
    String[] CATEGORY_COLUMNS = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_ORDER_VIEW};

    String JSON_KEY_CODE = "code";
    String JSON_KEY_NAME = "name";
    String JSON_KEY_DESCRIPTION = "description";
    String JSON_KEY_INGREDIENTS = "recipeIngredient";
    String JSON_KEY_INSTRUCTIONS = "recipeInstructions";
    String JSON_KEY_IMAGE = "image";
    String JSON_KEY_URL = "url";

    String TEXT_SIGNATURE_BLIST = "Open by BList,";
    String TEXT_SEPARATOR = "============";

    String PRODUCT_LOW = "low";
    String PRODUCT_FULL = "full";
    Double DEFAULT_UNIT_PRICE = 0.0;
    int DEFAULT_QUANITY = 1;
    String DEFAULT_CATEGORY_ID = "default_category";
    String DEFUALT_TIME_INSTALL = "2018-08-28";
    //recipe detail
    String RECIPE_TYPE_HEADER = "type_header";
    String RECIPE_TYPE_INGREDIENTS_HEADER = "type_ingredients_header";
    String RECIPE_TYPE_INGREDIENTS = "type_ingredients";
    String RECIPE_TYPE_INSTRUCTIONS_HEADER = "type_instruction_header";
    String RECIPE_TYPE_INSTRUCTION = " type_instruction";

    String SHARE_PREFERENCES_PREF_NAME = "com.best.grocery.list.key_value_data";
    String SHARE_PREFERENCES_KEY_SHOWCASE_PRODUCT_SWIPE = "showcase_product_swipe";
    String SHARE_PREFERENCES_KEY_SHOWCASE_PRODUCT_MOVE = "showcase_product_move";
    String SHARE_PREFERENCES_KEY_SHOWCASE_RECIPE_BOOK_ADD = "showcase_recipe_book_add";
    String SHARE_PREFERENCES_KEY_INGREDIENT_CHECKED = "ingredient_checked";
    String SHARE_PREFERENCES_KEY_APP_TIME_INSTALL = "first_install";
    String SHARE_PREFERENCES_KEY_INSTALL_DATE = "rta_install_date";
    String SHARE_PREFERENCES_KEY_LAUNCH_TIMES = "rta_launch_times";
    String SHARE_PREFERENCES_KEY_OPT_OUT = "rta_opt_out";
    String SHARE_PREFERENCES_KEY_ASK_LATER_DATE = "rta_ask_later_date";
    String SHARE_PREFERENCES_KEY_VERSION_APP_PENDING_UPDATE = "version_app_pending_update";
    String SHARE_PREFERENCES_LANGUAGE_CODE = "app_language_code";
    String SHARE_PREFERENCES_UNIT_PRODUCT = "product_unit";
    String SHARE_PREFERENCES_AUTO_BACKUP = "app_auto_backup";
    String SHARE_PREFERENCES_LAST_BACKUP = "app_last_backup";
    String SHARE_PREFERENCES_IS_FIRST_TIME_LAUNCH = "is_first_time_launch";

    String SHARE_PREFERENCES_FRAGMENT_ACTIVE = "fragment_active";
    String SHOPPING_LIST_ACTIVE = "shopping_list";
    String RECIPE_BOOK_ACITVE = "recipe_book";
    String PANTRY_LIST_ACTIVE = "pantry_list";

    //Firebase
    String FIREBASE_ADS_SHOPPING_LIST = "ads_shopping_list";
    String FIREBASE_ADS_RECIPE_BOOK = "ads_recipe_book";
    String FIREBASE_ADS_LAST_ADS_PROMO = "last_ads_promo";
    String FIREBASE_ADS_LAUNCH_TIME = "ads_launch_time";
    String FIREBASE_RATE_APP_INSTALL_DAY = "rate_app_install_day";
    String FIREBASE_RATE_APP_LAUNCH_TIME = "rate_app_launch_time";
    String FIREBASE_BACKUP_SCHEDULE = "backup_schedule_days";
    //values
    String SIGNRATURE_OUTOFMILK = "http://outofmilk.com/download";
    String SIGNRAURE_OURGROCETIES = "www.ourgroceries.com";
    String SIGNRATURE_BUYMEAPIE = "https://bnc.lt/iljk/QtVupOqITQ";
    String SIGNRATURE_APP_COOKBOOK = "MyCookBook";
    //
    String TAG_ERROR = "TAG_ERROR";
    String TAG_DIALOG = "TAG_DIALOG";


}
