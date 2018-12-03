package com.best.grocery.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.best.grocery.R;


public class RecipeInstructionHeaderHolder extends RecyclerView.ViewHolder{
    public TextView textHeader;

    public RecipeInstructionHeaderHolder(View itemView) {
        super(itemView);
        textHeader = itemView.findViewById(R.id.recipe_detail_instructions_header);
    }
}
