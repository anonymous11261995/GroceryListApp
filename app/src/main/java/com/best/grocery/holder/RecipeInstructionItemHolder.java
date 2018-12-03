package com.best.grocery.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.best.grocery.R;


public class RecipeInstructionItemHolder extends RecyclerView.ViewHolder{
    public TextView textContent;
    public RecipeInstructionItemHolder(View itemView) {
        super(itemView);
        textContent = itemView.findViewById(R.id.recipe_detail_instructions_item);
    }
}
