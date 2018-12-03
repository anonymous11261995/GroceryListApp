package com.best.grocery.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.activity.MainActivity;

import org.json.JSONObject;
import org.jsoup.Jsoup;

public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> implements DefinitionSchema {
    private String mLatestVersion;
    private String mCurrentVersion;
    String URL_APP;

    public ForceUpdateAsync(String currentVersion, Context context) {
        this.mCurrentVersion = currentVersion;
        URL_APP = AppConfig.URL_STORE + context.getPackageName();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            mLatestVersion = Jsoup.connect(URL_APP)
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                    .first()
                    .ownText();

        } catch (Exception e) {
            Log.w("Warning", "[ForceUpdateAsync]: get last version in google stores:" + e.getMessage());
        }
        return new JSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (mLatestVersion != null) {
            if (mCurrentVersion.compareToIgnoreCase(mLatestVersion) < 0) {
                Log.d("Update_app","app_version: " + mCurrentVersion + " ,store_version: " + mLatestVersion);
                MainActivity.mNavigationView.getMenu().getItem(4).getSubMenu().getItem(1).setVisible(true);
            }
            else {
                MainActivity.mNavigationView.getMenu().getItem(4).getSubMenu().getItem(1).setVisible(true);
            }
        }
        else {
            MainActivity.mNavigationView.getMenu().getItem(4).getSubMenu().getItem(1).setVisible(true);
        }
        super.onPostExecute(jsonObject);
    }


}
