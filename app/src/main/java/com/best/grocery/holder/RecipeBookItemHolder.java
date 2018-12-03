package com.best.grocery.holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.best.grocery.R;

/**
 * Created by TienTruong on 7/30/2018.
 */

 @SuppressWarnings("CanBeFinal")
 public class RecipeBookItemHolder extends RecyclerView.ViewHolder{
     public ImageView recipeImage;
     public TextView recipeName;
     public TextView recipeDescription;
     public ImageView recipeMenu;
     public ConstraintLayout groupView;

    public RecipeBookItemHolder(View itemView) {
        super(itemView);
        recipeImage = itemView.findViewById(R.id.recipe_image);
        recipeName = itemView.findViewById(R.id.recipe_name);
        recipeDescription = itemView.findViewById(R.id.recipe_description);
        recipeMenu = itemView.findViewById(R.id.recipe_menu);
        groupView = itemView.findViewById(R.id.recipe_book_constraint);
    }
}
