package com.best.grocery.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;


import com.best.grocery.R;
import com.best.grocery.fragment.RecipeDetailFragment;
import com.best.grocery.entity.Recipe;
import com.best.grocery.holder.RecipeDetailHeaderHolder;
import com.best.grocery.holder.RecipeIngredientHeaderHolder;
import com.best.grocery.holder.RecipeIngredientItemHolder;
import com.best.grocery.holder.RecipeInstructionHeaderHolder;
import com.best.grocery.holder.RecipeInstructionItemHolder;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;

/**
 * Created by TienTruong on 8/1/2018.
 */

@SuppressWarnings("CanBeFinal")
public class RecipeDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DefinitionSchema {
    private static final String TAG = RecipeDetailAdapter.class.getSimpleName();
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_HEADER_INGREDIENT = 2;
    private static final int TYPE_INGREDIENT = 3;
    private static final int TYPE_HEADER_INSTRUCTION = 4;
    private static final int TYPE_INSTRUCTION = 5;
    public ArrayList<String> mDataChecked;
    private ArrayList<Recipe> mData;
    private Context mContext;

    public RecipeDetailAdapter(ArrayList<Recipe> data, ArrayList<String> dataChecked, Context context) {
        this.mData = data;
        this.mDataChecked = dataChecked;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Log.d(TAG, "view: " + viewType);
        switch (viewType) {
            case TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_recipe_detail, parent, false);
                return new RecipeDetailHeaderHolder(view);
            case TYPE_HEADER_INGREDIENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_ingredient_recipe_detail, parent, false);
                return new RecipeIngredientHeaderHolder(view);
            case TYPE_INGREDIENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_ingredient_recipe_detail, parent, false);
                return new RecipeIngredientItemHolder(view);
            case TYPE_HEADER_INSTRUCTION:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_instruction_recipe_detail, parent, false);
                return new RecipeInstructionHeaderHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_instruction_recipe_detail, parent, false);
                return new RecipeInstructionItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof RecipeDetailHeaderHolder) {
                RecipeDetailHeaderHolder vh = (RecipeDetailHeaderHolder) holder;
                onBindHeader(vh, position);

            } else if (holder instanceof RecipeIngredientHeaderHolder) {
                RecipeIngredientHeaderHolder vh = (RecipeIngredientHeaderHolder) holder;
                onBindIngredientHeader(vh, position);

            } else if (holder instanceof RecipeIngredientItemHolder) {
                RecipeIngredientItemHolder vh = (RecipeIngredientItemHolder) holder;
                onBindIngredient(vh, position);
            } else if (holder instanceof RecipeInstructionHeaderHolder) {
                RecipeInstructionHeaderHolder vh = (RecipeInstructionHeaderHolder) holder;
                onBindInstructionHeader(vh, position);
            } else {
                RecipeInstructionItemHolder vh = (RecipeInstructionItemHolder) holder;
                onBindInstruction(vh, position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemViewType(int position) {
        if (mData.size() == 0)
            return super.getItemViewType(position);
        Recipe recipe = mData.get(position);
        switch (recipe.getType()) {
            case RECIPE_TYPE_HEADER:
                return TYPE_HEADER;
            case RECIPE_TYPE_INGREDIENTS_HEADER:
                return TYPE_HEADER_INGREDIENT;
            case RECIPE_TYPE_INGREDIENTS:
                return TYPE_INGREDIENT;
            case RECIPE_TYPE_INSTRUCTIONS_HEADER:
                return TYPE_HEADER_INSTRUCTION;
            case RECIPE_TYPE_INSTRUCTION:
                return TYPE_INSTRUCTION;
            default:
                return TYPE_HEADER_INSTRUCTION;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void onBindInstruction(RecipeInstructionItemHolder holder, int position) {
        Recipe recipe = mData.get(position);
        String text = recipe.getInstrutions().get(0);
        holder.textContent.setText(text);


    }

    private void onBindInstructionHeader(RecipeInstructionHeaderHolder holder, int position) {
    }

    private void onBindIngredientHeader(RecipeIngredientHeaderHolder holder, int position) {
    }


    private void onBindIngredient(final RecipeIngredientItemHolder holder, int position) {
        final Recipe recipe = mData.get(position);
        final String text = recipe.getIngredients().get(0);
        holder.mIngredient.setText(text);
        if (mDataChecked.contains(text)) {
            holder.mCheckbox.setChecked(true);
        } else {
            holder.mCheckbox.setChecked(false);
        }
        //event
        holder.mCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_effect));
                if (mDataChecked.contains(text)) {
                    int index = mDataChecked.indexOf(text);
                    mDataChecked.remove(index);
                } else {
                    mDataChecked.add(text);
                }
            }
        });

        holder.mIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mCheckbox.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_effect));
                Boolean status = holder.mCheckbox.isChecked();
                Log.d(TAG, "Ingredient: " + text + " check/uncheck");
                if (status) {
                    int index = mDataChecked.indexOf(text);
                    if (index > -1 && index < mDataChecked.size()) mDataChecked.remove(index);
                    holder.mCheckbox.setChecked(false);
                } else {
                    mDataChecked.add(text);
                    holder.mCheckbox.setChecked(true);
                }
            }
        });
    }

    private void onBindHeader(RecipeDetailHeaderHolder holder, int position) {
        Recipe recipe = mData.get(position);
        holder.mRecipeName.setText(recipe.getName());
        holder.mRecipeDescription.setText(recipe.getDescription());
        Bitmap b = RecipeDetailFragment.mRecipeService.loadRecipeImage(mContext, recipe.getCode());
        if (b != null) {
            holder.mRecipeImage.setImageBitmap(b);
        }
    }

    public ArrayList<String> getDataChecked() {
        return mDataChecked;
    }

    public void setDataChecked(ArrayList<String> dataChecked) {
        this.mDataChecked = dataChecked;
    }
}
