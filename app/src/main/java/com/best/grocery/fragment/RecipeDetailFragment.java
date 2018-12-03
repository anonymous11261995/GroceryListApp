package com.best.grocery.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.activity.MainActivity;
import com.best.grocery.adapter.RecipeDetailAdapter;
import com.best.grocery.entity.Recipe;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.service.RecipeService;
import com.best.grocery.service.ShoppingListService;
import com.best.grocery.utils.DefinitionSchema;
import com.best.grocery.utils.PrefManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by TienTruong on 8/1/2018.
 */

@SuppressWarnings({"ConstantConditions", "FieldCanBeLocal"})
public class RecipeDetailFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, DefinitionSchema {
    private static final String TAG = RecipeBookFragment.class.getSimpleName();
    private ImageView mMenu;
    private ImageView mBack;
    private Spinner mSpinner;
    private Button mAddIngredient;
    private RecyclerView mRecyclerView;

    private ArrayList<Recipe> mData;
    public static RecipeService mRecipeService;
    private ShoppingListService mShoppingListService;
    private RecipeDetailAdapter mAdapter;
    private Recipe mRecipe;

    private PrefManager mPrefManager;

    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecipeService = new RecipeService(getContext());
        mShoppingListService = new ShoppingListService(getContext());
        mPrefManager = new PrefManager(getContext());
        initViews();
        setOnListener();
        initRecyclerView();
        hideSoftKeyBoard();
    }

    private void hideSoftKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initRecyclerView() {
        mData = mRecipeService.builDataList(mRecipe);
        ArrayList<String> dataChecked = getDataChecked();
        mRecyclerView = getView().findViewById(R.id.recipe_detail_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecipeDetailAdapter(mData, dataChecked, getContext());
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.recipe_detail_back:
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new RecipeBookFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.recipe_detail_menu:
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                PopupMenu popup = new PopupMenu(getContext(), mMenu);
                popup.inflate(R.menu.recipe_detail_menu);
                popup.setOnMenuItemClickListener(this);
                popup.show();
                break;
            case R.id.recipe_detail_button_add:
                final String nameShoppingList = mSpinner.getSelectedItem().toString();
                final ArrayList<String> mDataChecked = mAdapter.getDataChecked();
                Log.d(TAG, "Add ingredient to shopping list, amount : " + mDataChecked.size());
                if (mDataChecked.size() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setMessage(getResources().getString(R.string.dialog_message_no_item_chooseed));
                    builder.setNeutralButton("OK", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    AlertDialog alert;
                    builder.setMessage(getResources().getString(R.string.dialog_message_ingredient_to_shopping_list));
                    builder.setPositiveButton(getResources().getString(R.string.abc_done), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mRecipeService.addIngredientToList(mRecipe.getName(), mDataChecked, nameShoppingList);
                            Set<String> set = new HashSet<>();
                            for (String text : mDataChecked) {
                                set.add(text);
                                mPrefManager.putStringSet(SHARE_PREFERENCES_KEY_INGREDIENT_CHECKED, set);
                            }
                            activeFragment(new ShoppingListFragment());
                        }
                    });

                    builder.setNegativeButton(getResources().getString(R.string.abc_continue), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mRecipeService.addIngredientToList(mRecipe.getName(), mDataChecked, nameShoppingList);
                            Set<String> set = new HashSet<>();
                            for (String text : mDataChecked) {
                                set.add(text);
                                mPrefManager.putStringSet(SHARE_PREFERENCES_KEY_INGREDIENT_CHECKED, set);
                            }
                            activeFragment(new RecipeBookFragment());
                        }
                    });
                    alert = builder.create();
                    alert.show();
                }

                break;
        }
    }

    private void initViews() {
        mAddIngredient = getView().findViewById(R.id.recipe_detail_button_add);
        mBack = getView().findViewById(R.id.recipe_detail_back);
        mMenu = getView().findViewById(R.id.recipe_detail_menu);
        mSpinner = getView().findViewById(R.id.recipe_detail_spinner);
        setViewSpinner();
    }

    private void setViewSpinner() {
        ArrayList<ShoppingList> list = mShoppingListService.getAllShoppingList();
        ArrayList<String> arrayList = new ArrayList<>();
        for (ShoppingList sl : list) {
            if(sl.isActive()){
                arrayList.add(0,sl.getName());
            }
            else {
                arrayList.add(sl.getName());
            }
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_recipe_detail, arrayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_layout);
        mSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void setOnListener() {
        mBack.setOnClickListener(this);
        mAddIngredient.setOnClickListener(this);
        mMenu.setOnClickListener(this);
    }


    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setRecipe(Recipe recipe) {
        this.mRecipe = recipe;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        ArrayList<String> list = new ArrayList<>();
        switch (id) {
            case R.id.recipe_detail_check_all:
                list.clear();
                for (int i = 0; i < mData.size(); i++) {
                    if (mData.get(i).getType().equals(RECIPE_TYPE_INGREDIENTS)) {
                        list.add(mData.get(i).getIngredients().get(0));
                    }
                }
                mAdapter.setDataChecked(list);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.recipe_detail_uncheck_all:
                list.clear();
                mAdapter.setDataChecked(list);
                mAdapter.notifyDataSetChanged();

        }
        return false;
    }

    ArrayList<String> getDataChecked() {
        ArrayList<String> data = new ArrayList<>();
        Set<String> strings = mPrefManager.getStringSet(SHARE_PREFERENCES_KEY_INGREDIENT_CHECKED, new HashSet<String>());
        for (String text : strings) {
            data.add(text);
        }
        return data;
    }
}
