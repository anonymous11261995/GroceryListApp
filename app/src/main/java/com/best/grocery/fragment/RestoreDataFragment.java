package com.best.grocery.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.adapter.FileAdapter;
import com.best.grocery.entity.FileObject;
import com.best.grocery.utils.DefinitionSchema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;

public class RestoreDataFragment extends Fragment implements DefinitionSchema, View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private static final String TAG = RestoreDataFragment.class.getSimpleName();

    private TextView mTextEmptyData;
    private ImageView mMenu;
    private ImageView mBack;
    private FileAdapter mAdapter;
    ArrayList<FileObject> mData;

    String pathStorageApp;
    String pathStorageDirectory;

    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restore_data, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pathStorageApp = "data" + File.separator + getContext().getPackageName() + File.separator + "databases" + File.separator + AppConfig.DATABASE_NAME;
        pathStorageDirectory = File.separator + getString(R.string.app_name) + File.separator + "backup";
        initViews();
        setOnListener();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mData = new ArrayList<>(getAllFileBackup());
        if (mData.size() == 0) {
            mTextEmptyData.setVisibility(View.VISIBLE);
        } else {
            mTextEmptyData.setVisibility(View.GONE);
        }
        RecyclerView recyclerView = getView().findViewById(R.id.restore_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new FileAdapter(getContext(), mData);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new FileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final FileObject object, final int position) {
                Log.d(TAG, "Item click: " + position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String delete = getString(R.string.abc_delete);
                String restore = getString(R.string.abc_restore);
                String[] arr = {restore, delete};
                builder.setTitle(object.getCreated())
                        .setItems(arr, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "which: " + which);
                                if (which == 0) {
                                    Log.d(TAG, "Restore data");
                                    restoreData(object);
                                } else {
                                    Log.d(TAG, "Delete file");
                                    File file = new File(object.getPath());
                                    file.delete();
                                    mData.remove(position);
                                    mAdapter.notifyItemRemoved(position);

                                }
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    private void restoreData(final FileObject object) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.dialog_message_restore_data));
        builder.setPositiveButton(getString(R.string.abc_continue), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                importDB(object.getName());
                activeFragment(new ShoppingListFragment());
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.abc_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private ArrayList<FileObject> getAllFileBackup() {
        ArrayList<FileObject> data = new ArrayList<>();
        File file = new File(Environment.getExternalStorageDirectory() + pathStorageDirectory);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File item : files) {
                Log.d(TAG, item.getAbsolutePath() + ", name: " + item.getName());
                if (item.getName().indexOf(".db") != -1) {
                    FileObject object = new FileObject(item.getPath(), item.getName());
                    data.add(object);
                }

            }
        }
        Collections.reverse(data);
        return data;
    }

    private void initViews() {
        mTextEmptyData = getView().findViewById(R.id.restore_textview_recycler_view_no_item);
        mBack = getView().findViewById(R.id.restore_button_back);
        mMenu = getView().findViewById(R.id.restore_image_menu);

    }

    private void setOnListener() {
        mBack.setOnClickListener(this);
        mMenu.setOnClickListener(this);

    }

    private void importDB(String nameFile) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                File backupDB = new File(data, pathStorageApp);
                File currentDB = new File(Environment.getExternalStorageDirectory() + pathStorageDirectory + File.separator + nameFile);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        } catch (Exception e) {
            Log.e("Error", "Restore: " + e.getMessage());

        }
    }

    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.restore_button_back:
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new SettingsFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.restore_image_menu:
                PopupMenu popup = new PopupMenu(getContext(), mMenu);
                popup.inflate(R.menu.restore_menu);
                popup.setOnMenuItemClickListener(this);
                popup.show();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alert;
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.action_delete_all:
                builder.setMessage(getString(R.string.dialog_message_backup_delete_all));
                builder.setPositiveButton(getString(R.string.abc_continue), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        for (FileObject object : mData) {
                            File file = new File(object.getPath());
                            file.delete();
                        }
                        mData.clear();
                        mAdapter.notifyDataSetChanged();
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
        }
        return false;
    }

}
