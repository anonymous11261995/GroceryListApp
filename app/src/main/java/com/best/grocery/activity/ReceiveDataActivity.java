package com.best.grocery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.best.grocery.BuildConfig;
import com.best.grocery.R;
import com.best.grocery.database.DatabaseHelper;
import com.best.grocery.fragment.CrawlDataFragment;
import com.best.grocery.fragment.ReceiveDataShareFragment;
import com.best.grocery.utils.DefinitionSchema;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;

public class ReceiveDataActivity extends AppCompatActivity implements DefinitionSchema {
    private static final String TAG = ReceiveDataActivity.class.getSimpleName();
    DatabaseHelper mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_data);
        configureCrashReporting();
        openDatabase();
        showFragment();
    }


    private void openDatabase() {
        mDb = new DatabaseHelper(this);
        mDb.openDataBase();

    }

    private void configureCrashReporting() {
        Fabric.with(this, new Crashlytics());
        CrashlyticsCore crashlyticsCore = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();
        Fabric.with(this, new Crashlytics.Builder().core(crashlyticsCore).build());
    }

    private void showFragment() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                Fragment fragment;
                if (text.startsWith("https://") || text.startsWith("http://")) {
                    fragment = new CrawlDataFragment();
                    ((CrawlDataFragment) fragment).setTextRecive(text);
                } else {
                    fragment = new ReceiveDataShareFragment();
                    ((ReceiveDataShareFragment) fragment).setTextRecive(text);
                }
                activeFragment(fragment);
            }
        }
    }

    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = this.getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Destroy app");
        //mDb.close();
        super.onDestroy();
    }
}
