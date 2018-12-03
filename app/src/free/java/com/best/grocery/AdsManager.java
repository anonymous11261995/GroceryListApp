package com.best.grocery;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.best.grocery.utils.DefinitionSchema;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class AdsManager implements DefinitionSchema {
    private static final String TAG = AdsManager.class.getSimpleName();
    private Context mContext;
    private Activity mActivity;
    private SharedPreferences mSharedPref;

    public AdsManager(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        mSharedPref = context.getSharedPreferences(SHARE_PREFERENCES_PREF_NAME, Context.MODE_PRIVATE);
    }

    public void adsShoppingList() {
        try {
            AdView adView = mActivity.findViewById(R.id.shopping_list_adView);
            if (BuildConfig.DEBUG) {
                Log.d("AAA","DEBUG");
                MobileAds.initialize(mContext, AppConfig.ADMOB_APP_ID);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            } else {
                String isShowAds = AppConfig.getInstance().getConfig().getString(FIREBASE_ADS_SHOPPING_LIST);
                String promoAds = AppConfig.getInstance().getConfig().getString(FIREBASE_ADS_LAST_ADS_PROMO);
                String firstInstall = mSharedPref.getString(SHARE_PREFERENCES_KEY_APP_TIME_INSTALL, DEFUALT_TIME_INSTALL);
                DateFormat dateFormat = new SimpleDateFormat(AppConfig.DATE_SERVER_PATTERN);
                boolean isPromotion = dateFormat.parse(firstInstall).after(dateFormat.parse(promoAds));
                int launchTimes = mSharedPref.getInt(SHARE_PREFERENCES_KEY_LAUNCH_TIMES, 0);
                int adsLaunchTimes = Integer.valueOf(AppConfig.getInstance().getConfig().getString(FIREBASE_ADS_LAUNCH_TIME));
                Log.d("ads_mob", "showAds: " + isShowAds + ", IsPromotion: " + isPromotion + ", lauchTimes: " + launchTimes + ", adsLaunchTime: " + adsLaunchTimes + ", promoAds: " + promoAds);
                if (isShowAds.toLowerCase().equals("on") && isPromotion && launchTimes > adsLaunchTimes) {
                    Log.d(TAG, "Show Ads");
                    MobileAds.initialize(mContext, AppConfig.ADMOB_APP_ID);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                } else {
                    adView.setVisibility(View.GONE);
                    Log.d(TAG, "Hidden Ads");
                }
            }
        } catch (Exception e) {
            Log.e("Error: ", "[ShoppingListFragment] Show ads: " + e.getMessage());
        }

    }

    public void adsRecipeBook() {
        try {
            AdView adView = mActivity.findViewById(R.id.recipe_book_adsView);
            if (BuildConfig.DEBUG) {
                MobileAds.initialize(mContext, AppConfig.ADMOB_APP_ID);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            } else {
                String isAdsMob = AppConfig.getInstance().getConfig().getString(FIREBASE_ADS_RECIPE_BOOK);
                String lastAdsMob = AppConfig.getInstance().getConfig().getString(FIREBASE_ADS_LAST_ADS_PROMO);
                String firstInstall = mSharedPref.getString(SHARE_PREFERENCES_KEY_APP_TIME_INSTALL, DEFUALT_TIME_INSTALL);
                DateFormat dateFormat = new SimpleDateFormat(AppConfig.DATE_SERVER_PATTERN);
                boolean isPromotion = dateFormat.parse(firstInstall).after(dateFormat.parse(lastAdsMob));
                int launchTimes = mSharedPref.getInt(SHARE_PREFERENCES_KEY_LAUNCH_TIMES, 0);
                int adsLaunchTimes = Integer.valueOf(AppConfig.getInstance().getConfig().getString(FIREBASE_ADS_LAUNCH_TIME));
                Log.d("ads_mob", " IsAdsMob: " + isAdsMob + " IsPromotion: " + isPromotion + " lauchTimes: " + launchTimes);
                if (isAdsMob.toLowerCase().equals("on") && isPromotion && launchTimes > adsLaunchTimes) {
                    Log.d(TAG, "Show Ads");
                    MobileAds.initialize(mContext, AppConfig.ADMOB_APP_ID);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                } else {
                    adView.setVisibility(View.GONE);
                    Log.d(TAG, "Hidden Ads");
                }
            }

        } catch (Exception e) {
            Log.e("Error: ", "[ShoppingListFragment] Show ads: " + e.getMessage());
        }

    }

}
