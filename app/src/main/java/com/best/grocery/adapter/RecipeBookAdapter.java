package com.best.grocery.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import android.widget.PopupMenu;


import com.best.grocery.R;
import com.best.grocery.fragment.RecipeBookFragment;
import com.best.grocery.fragment.RecipeDetailFragment;
import com.best.grocery.entity.Recipe;
import com.best.grocery.holder.RecipeBookItemHolder;
import com.best.grocery.service.RecipeService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by TienTruong on 7/30/2018.
 */

@SuppressWarnings("CanBeFinal")
public class RecipeBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DefinitionSchema {
    private static final String TAG = RecipeBookAdapter.class.getSimpleName();
    private ArrayList<Recipe> mData;
    private RecipeService mRecipeService;
    private FragmentActivity mFragmentActivity;
    private Context mContext;

    public RecipeBookAdapter(FragmentActivity fragmentActivity, Context context, ArrayList<Recipe> mData) {
        this.mData = mData;
        this.mRecipeService = new RecipeService(context);
        this.mFragmentActivity = fragmentActivity;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_book, parent, false);
        return new RecipeBookItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RecipeBookItemHolder holder = (RecipeBookItemHolder) viewHolder;
        final Recipe recipe = mData.get(position);
        holder.recipeName.setText(recipe.getName());
        holder.recipeDescription.setText(recipe.getDescription());
        Bitmap b = mRecipeService.loadRecipeImage(mContext, recipe.getCode());
        if (b != null) {
            holder.recipeImage.setImageBitmap(b);
        }
        //Picasso.get().load(recipe.getImage()).into(holder.recipeImage);

        holder.groupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_effect));
                Log.d(TAG, "Click recipe: " + recipe.getName());
                RecipeDetailFragment fragment = new RecipeDetailFragment();
                fragment.setRecipe(recipe);
                activeFragment(fragment);
            }
        });

        holder.recipeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_effect));
                PopupMenu popup = new PopupMenu(view.getContext(), holder.recipeMenu);
                popup.inflate(R.menu.recipe_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.recipe_menu_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(mFragmentActivity);
                                builder.setMessage(mContext.getResources().getString(R.string.dialog_message_confirm_delete_recipe_book));
                                builder.setPositiveButton(mContext.getResources().getString(R.string.abc_delete), new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d(TAG, "Delete recipe book: " + recipe.getName());
                                        mRecipeService.deleteRecipeBook(recipe, mContext);
                                        int index = mData.indexOf(recipe);
                                        mData.remove(index);
                                        initRecyclerView();

                                    }
                                });

                                builder.setNegativeButton(mContext.getResources().getString(R.string.abc_cancel), new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();

                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

    }

    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = mFragmentActivity.getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void initRecyclerView() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            RecipeBookFragment.mStatusInternet.setVisibility(View.GONE);
        } else {
            RecipeBookFragment.mStatusInternet.setVisibility(View.VISIBLE);
        }
        if (mData.size() == 0) {
            RecipeBookFragment.mTextEmptyList.setVisibility(View.VISIBLE);
        } else {
            RecipeBookFragment.mTextEmptyList.setVisibility(View.GONE);
        }
        RecipeBookFragment.mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mFragmentActivity);
        RecipeBookFragment.mRecyclerView.setLayoutManager(layoutManager);
        RecipeBookFragment.mRecyclerView.setAdapter(this);
    }


}
