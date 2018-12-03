package com.best.grocery.holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.best.grocery.R;

/**
 * Created by TienTruong on 8/1/2018.
 */

@SuppressWarnings("CanBeFinal")
public class RecipeIngredientItemHolder extends RecyclerView.ViewHolder {
    public CheckBox mCheckbox;
    public TextView mIngredient;

    public RecipeIngredientItemHolder(View itemView) {
        super(itemView);
        this.mCheckbox = itemView.findViewById(R.id.recipe_detail_ingredient_checkbox);
        this.mIngredient = itemView.findViewById(R.id.recipe_detail_ingredient);
    }
}
