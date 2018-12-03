package com.best.grocery.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.best.grocery.R;

public class RecipeIngredientHeaderHolder extends RecyclerView.ViewHolder{
    TextView textHeader;
    public RecipeIngredientHeaderHolder(View itemView) {
        super(itemView);
        textHeader = itemView.findViewById(R.id.recipe_detail_ingredient_header_text);
    }
}
