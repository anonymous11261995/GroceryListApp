package com.best.grocery.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.activity.MainActivity;
import com.best.grocery.activity.WelcomeActivity;
import com.best.grocery.utils.DefinitionSchema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class SettingsFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    ImageView mOpenDrawer;
    LinearLayout mSettingLanguage;
    SharedPreferences mSharedPref;
    ConstraintLayout mSettingsBackup;
    ConstraintLayout mSettingsRestore;
    ConstraintLayout mSettingAutoBackup;
    CheckBox mCheckboxAutoBackup;
    TextView twBackup;
    Boolean mAutobackup;

    String pathStorageApp;
    String pathStorageDirectory;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSharedPref = getContext().getSharedPreferences(SHARE_PREFERENCES_PREF_NAME, Context.MODE_PRIVATE);
        pathStorageApp = "data" + File.separator + getContext().getPackageName() + File.separator + "databases" + File.separator + AppConfig.DATABASE_NAME;
        pathStorageDirectory = File.separator + getString(R.string.app_name) + File.separator + "backup";
        mAutobackup = mSharedPref.getBoolean(SHARE_PREFERENCES_AUTO_BACKUP, true);
        initViews();
        setOnListener();
        hideSoftKeyBoard();
    }

    private void initViews() {
        mOpenDrawer = getView().findViewById(R.id.open_drawer_settings);
        mSettingLanguage = getView().findViewById(R.id.setting_language_layout);
        mSettingsBackup = getView().findViewById(R.id.layout_backup);
        mSettingsRestore = getView().findViewById(R.id.settings_restore_layout);
        twBackup = getView().findViewById(R.id.settings_backup_last_backup);
        setTextLastBackup();
        mSettingAutoBackup = getView().findViewById(R.id.settings_autobackup_layout);
        mCheckboxAutoBackup = getView().findViewById(R.id.settings_autobackup_checkbox);
        if (mAutobackup) {
            mCheckboxAutoBackup.setChecked(true);
        } else {
            mCheckboxAutoBackup.setChecked(false);
        }
    }

    private void setTextLastBackup() {
        try {
            String firstInstall = mSharedPref.getString(SHARE_PREFERENCES_KEY_APP_TIME_INSTALL, DEFUALT_TIME_INSTALL);
            DateFormat dateFormat = new SimpleDateFormat(AppConfig.DATE_SERVER_PATTERN);
            long lastBackup = mSharedPref.getLong(SHARE_PREFERENCES_LAST_BACKUP, dateFormat.parse(firstInstall).getTime());
            String text = convertMilliSecondsToFormattedDate(String.valueOf(lastBackup));
            twBackup.setText(text);
        } catch (Exception e) {

        }

    }


    private void setOnListener() {
        mOpenDrawer.setOnClickListener(this);
        mSettingLanguage.setOnClickListener(this);
        mSettingsBackup.setOnClickListener(this);
        mSettingsRestore.setOnClickListener(this);
        mSettingAutoBackup.setOnClickListener(this);
        mCheckboxAutoBackup.setOnClickListener(this);

    }

    private void hideSoftKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        final File direct = new File(Environment.getExternalStorageDirectory() + pathStorageDirectory);
        final File subFolder = new File(Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alert;
        switch (id) {
            case R.id.open_drawer_settings:
                MainActivity.mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.setting_language_layout:
                Log.d(TAG, "Setting language");
                builder.setTitle(R.string.dialog_title_select_language)
                        .setItems(R.array.app_languages, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "which: " + which);
                                if (which == 0) {
                                    setLocale("de");
                                } else if (which == 1) {
                                    setLocale("en");
                                } else if (which == 2) {
                                    setLocale("es");
                                } else if (which == 3) {
                                    setLocale("fr");
                                } else if (which == 4) {
                                    setLocale("pt");
                                } else if (which == 5) {
                                    setLocale("ru");
                                } else if (which == 6) {
                                    setLocale("ko");
                                } else if (which == 7) {
                                    setLocale("ja");
                                }
                            }
                        });
                alert = builder.create();
                alert.show();
                break;
            case R.id.layout_backup:
                Log.d(TAG, "Backup data");
                builder.setMessage(getString(R.string.dialog_message_backup_data));
                builder.setPositiveButton(getString(R.string.abc_continue), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (isStoragePermissionGranted()) {
                            if (!subFolder.exists()) {
                                subFolder.mkdir();
                                if (!direct.exists()) {
                                    direct.mkdir();
                                }
                            }
                            exportDB();
                            Date now = new Date();
                            SharedPreferences.Editor editor = mSharedPref.edit();
                            editor.putLong(SHARE_PREFERENCES_LAST_BACKUP, now.getTime());
                            editor.commit();
                            setTextLastBackup();
                            Toast.makeText(getContext(), getString(R.string.toast_backup_data_sucess), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Access file permission is revoked", Toast.LENGTH_LONG).show();
                        }

                    }
                });

                builder.setNegativeButton(getResources().getString(R.string.abc_cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert = builder.create();
                alert.show();
                Button btnNegative = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                btnNegative.setTextColor(getResources().getColor(R.color.btn_negative));
                Button btnPositive = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                btnPositive.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
            case R.id.settings_restore_layout:
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isStoragePermissionGranted()) {
                            if (!subFolder.exists()) {
                                subFolder.mkdir();
                                if (!direct.exists()) {
                                    direct.mkdir();
                                }
                            }
                            activeFragment(new RestoreDataFragment());
                        } else {
                            Toast.makeText(getActivity(), "Access file permission is revoked", Toast.LENGTH_LONG).show();
                        }
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.settings_autobackup_layout:
                changeAutoBackup();
                break;
            case R.id.settings_autobackup_checkbox:
                changeAutoBackup();
                break;
            default:
                break;
        }

    }

    private void changeAutoBackup() {
        SharedPreferences.Editor editor = mSharedPref.edit();
        if (mAutobackup) {
            mAutobackup = false;
            mCheckboxAutoBackup.setChecked(false);
            editor.putBoolean(SHARE_PREFERENCES_AUTO_BACKUP, false);
            editor.commit();
        } else {
            mAutobackup = true;
            mCheckboxAutoBackup.setChecked(true);
            editor.putBoolean(SHARE_PREFERENCES_AUTO_BACKUP, true);
            editor.commit();
        }
    }

    //exporting database
    private void exportDB() {
        try {
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
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }

    private void setLocale(String codeLanguage) {
        Locale myLocale = new Locale(codeLanguage);
        Resources resources = getActivity().getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(myLocale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        Intent refresh = new Intent(getActivity(), WelcomeActivity.class);
        startActivity(refresh);
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(SHARE_PREFERENCES_LANGUAGE_CODE, codeLanguage);
        editor.commit();

    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public String convertMilliSecondsToFormattedDate(String milliSeconds) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConfig.DATE_SERVER_PATTERN_FULL);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(milliSeconds));
            return simpleDateFormat.format(calendar.getTime());
        } catch (Exception e) {
            Log.e("Error", "Read file backup: " + e.getMessage());
            return milliSeconds;
        }

    }
}
