package com.best.grocery.fragment;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.best.grocery.AdsManager;
import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.activity.MainActivity;
import com.best.grocery.adapter.RecipeBookAdapter;
import com.best.grocery.entity.Recipe;
import com.best.grocery.network.NetworkUtil;
import com.best.grocery.service.RecipeService;
import com.best.grocery.utils.DefinitionSchema;
import com.best.grocery.utils.PrefManager;

import java.util.ArrayList;


/**
 * Created by TienTruong on 7/19/2018.
 */

@SuppressWarnings("ConstantConditions")
public class RecipeBookFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private static final String TAG = RecipeBookFragment.class.getSimpleName();
    private ImageView mOpenDrawer;
    private ImageView mImageAdd;
    private RecipeService mRecipeService;
    public static TextView mStatusInternet;
    public static RecyclerView mRecyclerView;
    public static TextView mTextEmptyList;
    private PrefManager mPrefManager;
    public static NetworkChangeReceiver mNetworkChangeReceiver;
    IntentFilter mFilter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_book, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecipeService = new RecipeService(getContext());
        mPrefManager = new PrefManager(getContext());
        MainActivity.mNavigationView.getMenu().getItem(2).setChecked(true);
        mPrefManager.putString(SHARE_PREFERENCES_FRAGMENT_ACTIVE, RECIPE_BOOK_ACITVE);
        initViews();
        setOnListener();
        initRecyclerView();
        hideSoftKeyBoard();
        onBackPressed();
        adsManager();
    }

    private void adsManager() {
        AdsManager adsManager = new AdsManager(getContext(), getActivity());
        adsManager.adsRecipeBook();

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mNetworkChangeReceiver = new RecipeBookFragment.NetworkChangeReceiver();
            getContext().registerReceiver(mNetworkChangeReceiver, mFilter);
        } catch (Exception e) {
            Log.w("Warn", " " + e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mNetworkChangeReceiver = new RecipeBookFragment.NetworkChangeReceiver();
            getContext().registerReceiver(mNetworkChangeReceiver, mFilter);
        } catch (Exception e) {
            Log.w("Warn", " " + e.getMessage());
        }
    }

    private void hideSoftKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void onBackPressed() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (MainActivity.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                            MainActivity.mDrawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                            getContext().unregisterReceiver(mNetworkChangeReceiver);
                            getActivity().finish();
                        }
//                        Toast.makeText(getActivity(), "Back Pressed", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
    }


    private void initRecyclerView() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        ArrayList<Recipe> myDataset = mRecipeService.myRecipe();
        if (isConnected) {
            mStatusInternet.setVisibility(View.GONE);
        } else {
            mStatusInternet.setVisibility(View.VISIBLE);
        }
        if (myDataset.size() == 0) {
            mTextEmptyList.setVisibility(View.VISIBLE);
        } else {
            mTextEmptyList.setVisibility(View.GONE);
        }
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        RecipeBookAdapter mAdapter = new RecipeBookAdapter(getActivity(), getContext(), myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onClick(final View view) {
        int id = view.getId();
        view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
        switch (id) {
            case R.id.open_drawer_recipe:
                MainActivity.mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.recipe_book_add:
                chooseSiteAndRedirectUrl();
                break;


        }

    }

    private void chooseSiteAndRedirectUrl() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alert;
        builder.setTitle(getResources().getString(R.string.dialog_title_choose_site_recipe))
                .setItems(R.array.recipe_site_crawl, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] stringArray = getContext().getResources().getStringArray(R.array.recipe_site_crawl);
                        String site = stringArray[which];
                        Log.d(TAG, "You choose site: " + site);
                        WebViewRecipeFragment viewFragment = new WebViewRecipeFragment();
                        if (which == 0) {
                            viewFragment.setmUrl(AppConfig.SITE_ALLRECIPES);
                            activeFragment(viewFragment);
                        } else if (which == 1) {
                            viewFragment.setmUrl(AppConfig.SITE_GENIUSKITCHEN);
                            activeFragment(viewFragment);
                        } else if (which == 2) {
                            viewFragment.setmUrl(AppConfig.SITE_COOKPAD);
                            activeFragment(viewFragment);
                        }
                    }
                });
        alert = builder.create();
        alert.show();
    }


    private void initViews() {
        mRecyclerView = getView().findViewById(R.id.recycler_view_recipe_book);
        mOpenDrawer = getView().findViewById(R.id.open_drawer_recipe);
        mImageAdd = getView().findViewById(R.id.recipe_book_add);
        mStatusInternet = getView().findViewById(R.id.no_internet_banner);
        mTextEmptyList = getView().findViewById(R.id.recipe_book_text_empty_list);
    }

    private void setOnListener() {
        mOpenDrawer.setOnClickListener(this);
        mImageAdd.setOnClickListener(this);
    }

    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = NetworkUtil.getConnectivityStatusString(context);
            Log.d(TAG, "Status internet change:" + status);
            initRecyclerView();

        }
    }

}
