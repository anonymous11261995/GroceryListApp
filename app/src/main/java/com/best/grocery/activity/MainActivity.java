package com.best.grocery.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.best.grocery.AppConfig;
import com.best.grocery.BuildConfig;
import com.best.grocery.R;
import com.best.grocery.database.DatabaseHelper;
import com.best.grocery.fragment.PantryListFragment;
import com.best.grocery.fragment.RecipeBookFragment;
import com.best.grocery.fragment.SettingsFragment;
import com.best.grocery.fragment.ShoppingListFragment;
import com.best.grocery.service.GenericService;
import com.best.grocery.utils.PrefManager;
import com.best.grocery.service.ProductService;
import com.best.grocery.utils.DefinitionSchema;
import com.best.grocery.utils.ForceUpdateAsync;
import com.best.grocery.utils.RateThisApp;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements DefinitionSchema, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static DatabaseHelper mDb;
    public static NavigationView mNavigationView;
    public static DrawerLayout mDrawerLayout;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);

        } else {
            setContentView(R.layout.activity_main);
            prefManager = new PrefManager(this);
            mDb = new DatabaseHelper(this);
            mDb.openDataBase();
            configureCrashReporting();
            initViews();
            setOnListener();
            displayFragment();
            backupData();
            forceUpdate();
            rateApp();
            if (BuildConfig.DEBUG) {
                Toast.makeText(this, "Debug app", Toast.LENGTH_LONG).show();
            } else {
                firebaseFirestore();
                Log.i(TAG, "RELEASE");
            }

        }


    }

    @Override
    protected void onResume() {
        if (!mDb.isOpening()) {
            Log.d("SQLite", "Open again database");
            mDb.openDataBase();
        }
        Log.d(TAG, "Resume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        try {
            mDb.close();
        }
        catch (Exception e){

        }
        super.onDestroy();
    }


    private void configureCrashReporting() {
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());
    }

    private void backupData() {
        try {
            int days = (int) AppConfig.getInstance().getConfig().getLong(FIREBASE_BACKUP_SCHEDULE);
            String firstInstall = prefManager.getString(SHARE_PREFERENCES_KEY_APP_TIME_INSTALL, DEFUALT_TIME_INSTALL);
            DateFormat dateFormat = new SimpleDateFormat(AppConfig.DATE_SERVER_PATTERN);
            long lastBackup = prefManager.getLong(SHARE_PREFERENCES_LAST_BACKUP, dateFormat.parse(firstInstall).getTime());
            long now = (new Date()).getTime();
            if (now - lastBackup > days * 86400000) {
                Log.d(TAG, "Backup data");
                prefManager.putLong(SHARE_PREFERENCES_LAST_BACKUP, now);
                exportDB();
            }
            // Log.d(TAG, "days: " + days + ", lastBackup: " + lastBackup + ", now: " + now + ", different: " + (now - lastBackup));
        } catch (Exception e) {
            Log.e("Error", "Backup data");
        }

    }

    private void exportDB() {
        try {
            String pathStorageApp = "data" + File.separator + this.getPackageName() + File.separator + "databases" + File.separator + AppConfig.DATABASE_NAME;
            String pathStorageDirectory = File.separator + getString(R.string.app_name) + File.separator + "backup";
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String nameFile = String.valueOf((new Date()).getTime());
                File currentDB = new File(data, pathStorageApp);
                File backupDB = new File(Environment.getExternalStorageDirectory() + pathStorageDirectory + File.separator + nameFile + ".db");

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Log.d(TAG, Environment.getExternalStorageDirectory() + pathStorageDirectory + File.separator + nameFile + ".db");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    private void displayFragment() {
        Fragment fragment;
        String fragmentActive = prefManager.getString(SHARE_PREFERENCES_FRAGMENT_ACTIVE, SHOPPING_LIST_ACTIVE);
        if (fragmentActive.equals(SHOPPING_LIST_ACTIVE)) {
            fragment = new ShoppingListFragment();
        } else if (fragmentActive.equals(RECIPE_BOOK_ACITVE)) {
            fragment = new RecipeBookFragment();
        } else {
            fragment = new PantryListFragment();
        }
        //show fragment
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    private void initViews() {
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
    }

    private void setOnListener() {
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        Intent intent;
        switch (id) {
            case R.id.nav_shopping_list:
                activeFragment(new ShoppingListFragment());
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_pantry_list:
                activeFragment(new PantryListFragment());
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_recipe_book:
                activeFragment(new RecipeBookFragment());
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_about:
                prefManager.setFirstTimeLaunch(true);
                startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                finish();
                return true;
            case R.id.nav_support:
                ConnectivityManager cm =
                        (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(AppConfig.URL_FEEDBACK));
                    this.startActivity(intent);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Toast.makeText(this, this.getResources().getString(R.string.toast_no_internet_connection), Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.nav_settings:
                activeFragment(new SettingsFragment());
                mDrawerLayout.closeDrawer(GravityCompat.START);
                mNavigationView.getMenu().getItem(0).setChecked(false);
                mNavigationView.getMenu().getItem(1).setChecked(false);
                mNavigationView.getMenu().getItem(2).setChecked(false);
                return true;
            case R.id.nav_update:
                String urlApp = AppConfig.URL_STORE + getPackageName();
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlApp));
                this.startActivity(intent);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            default:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
        }

    }

    private void forceUpdate() {
        try {
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packageInfo = null;
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String currentVersion = packageInfo.versionName;
            new ForceUpdateAsync(currentVersion, this).execute();
        } catch (Exception e) {

        }

    }

    private void rateApp() {
        try {
            RateThisApp.onCreate(this);
            int criterialInstallDay = Integer.valueOf(AppConfig.getInstance().getConfig().getString(FIREBASE_RATE_APP_INSTALL_DAY));
            int criterialLaunchTime = Integer.valueOf(AppConfig.getInstance().getConfig().getString(FIREBASE_RATE_APP_LAUNCH_TIME));
            RateThisApp.Config config = new RateThisApp.Config(criterialInstallDay, criterialLaunchTime);
            RateThisApp.init(config);
            RateThisApp.showRateDialogIfNeeded(this);
        } catch (Exception e) {
            Log.e("Error", "rate_app: " + e.getMessage());
        }

    }


    private void firebaseFirestore() {
        try {
            Log.i(TAG, "Fetch data to firestore");
            final String user_id = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setTimestampsInSnapshotsEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);
            GenericService service = new GenericService(this);
            int launchTimes = prefManager.getInt(SHARE_PREFERENCES_KEY_LAUNCH_TIMES, 1);
            Date installDate = new Date(prefManager.getLong(SHARE_PREFERENCES_KEY_INSTALL_DATE, 0));
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            Map<String, Object> data = service.fetchToFirestore();
            data.put("app_launch_time", launchTimes);
            data.put("app_date_installed", installDate);
            data.put("app_version_code", versionCode);
            data.put("app_version_name", versionName);
            data.put("app_last_active", new Date());
            data.put("device_MODEL", android.os.Build.MODEL);
            data.put("device_RELEASE", android.os.Build.VERSION.RELEASE);
            data.put("device_BRAND", android.os.Build.BRAND);
//
            db.collection("database").document(user_id)
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "Add document sucess with userid: " + user_id);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        } catch (Exception e) {
            Log.e("Error", "Firebase firestore error: " + e.getMessage());
        }

    }


    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = this.getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

}
