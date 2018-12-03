package com.best.grocery.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.best.grocery.AppConfig;
import com.best.grocery.BuildConfig;
import com.best.grocery.R;
import com.best.grocery.adapter.ViewPagerAdapter;
import com.best.grocery.database.DatabaseHelper;
import com.best.grocery.database.InternalStorage;
import com.best.grocery.service.CategoryService;
import com.best.grocery.utils.PrefManager;
import com.best.grocery.utils.DefinitionSchema;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

/**
 * Created by TienTruong on 8/7/2018.
 */

@SuppressWarnings({"ResultOfMethodCallIgnored", "CanBeFinal"})
public class WelcomeActivity extends AppCompatActivity implements DefinitionSchema, View.OnClickListener {
    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private PrefManager prefManager;
    private DatabaseHelper mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
        setDefaultLanguage();
        configureCrashReporting();
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
        } else {
            saveUserVersionApp();
            if (Build.VERSION.SDK_INT >= 21) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }

            this.setContentView(R.layout.welcome_layout);
            mDb = new DatabaseHelper(this);
            initDatabase();
            initViews();
            setOnListener();
            //permissionGranted
            isStoragePermissionGranted();

        }
        // Making notification bar transparent


    }

    private void configureCrashReporting() {
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());
    }


    private void setOnListener() {
        ViewPagerAdapter myViewPagerAdapter = new ViewPagerAdapter(layouts, this);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission is granted");
                return true;
            } else {

                Log.d(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    private void firebaseRemoteConnect() {
        try {
            FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
            AppConfig.getInstance().setConfig(config);
            //Setting chế độ debug
            FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG).build();
            config.setConfigSettings(settings);
            //Sử dụng các giá trị defaule trong file R.xml.default_config nếu không lấy được giá trị
            config.setDefaults(R.xml.default_config);
            //fetch và active ngay lập tức sau khi thay đổi trên console
            long expireTime = config.getInfo().getConfigSettings().isDeveloperModeEnabled() ? 0 : AppConfig.CONFIG_EXPIRE_SECOND;
            //Mỗi lần khởi chạy app sẽ fetch config về và nếu thành công thì sẽ active config vừa lấy về
            config.fetch(expireTime)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Fetch remote config from Firebase Success");
                                AppConfig.getInstance().getConfig().activateFetched();
                            } else {
                                Log.d(TAG, "Fetch data from Firebase Failed");
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Firebase connect error: " + e.getMessage());
        }
    }


    private void saveUserVersionApp() {
        try {
            String firstInstall = prefManager.getString(SHARE_PREFERENCES_KEY_APP_TIME_INSTALL, DEFUALT_TIME_INSTALL);
            if (firstInstall.equals(DEFUALT_TIME_INSTALL)) {
                DateFormat dateFormat = new SimpleDateFormat(AppConfig.DATE_SERVER_PATTERN);
                prefManager.putString(SHARE_PREFERENCES_KEY_APP_TIME_INSTALL, dateFormat.format(new Date()));
            }
            Log.d("App info", " First install: " + firstInstall);
        } catch (Exception e) {
            Log.d("Error", "save user version_app: " + e.getMessage());
        }
    }


    private void setDefaultLanguage() {
        String defaultLanguage = Locale.getDefault().getLanguage();
        String code = prefManager.getString(SHARE_PREFERENCES_LANGUAGE_CODE, defaultLanguage);
        Locale myLocale = new Locale(code);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    private void initCategory() {
        mDb.openDataBase();
        CategoryService service = new CategoryService(this);
        String[] categories = getResources().getStringArray(R.array.categories);
        for (int i = 0; i < categories.length; i++) {
            String name = categories[i];
            service.createCategory(name);

        }

    }

    private void initDatabase() {
        File sqlFile = getApplicationContext().getDatabasePath(AppConfig.DATABASE_NAME);
        if (!sqlFile.exists()) {
            SQLiteDatabase db_Read;
            db_Read = mDb.getWritableDatabase();
            db_Read.close();
            Log.d(TAG, "File data.sql not exitst ");
            if (mDb.copyDatabase(this)) {
                Log.d(TAG, "Copy database to your phone");
                initCategory();
            } else return;
        } else {
            Log.d(TAG, "File data.sql exitst");
        }
        InternalStorage storage = new InternalStorage(this);
        File jsonFile = new File(getFilesDir(), AppConfig.DATEBASE_FILE_NAME);
        if (!jsonFile.exists()) {
            Log.d(TAG, "Copy file recipe_book json to dir cache");
            String data = storage.readFromAssets(AppConfig.DATEBASE_FILE_NAME);
            storage.writeToFile(data, AppConfig.DATEBASE_FILE_NAME);
        } else {
            Log.d(TAG, "File json exits");
        }
        mDb.close();
    }

    private void initViews() {
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4,
                R.layout.welcome_slide5
        };

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        firebaseRemoteConnect();
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    //	viewpager change listener
    private ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.abc_got_it));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.abc_next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_skip:
                launchHomeScreen();
                break;
            case R.id.btn_next:
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
                break;
            default:
                break;
        }
    }

}
