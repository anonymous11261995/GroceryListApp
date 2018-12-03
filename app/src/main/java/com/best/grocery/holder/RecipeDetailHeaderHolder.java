package com.best.grocery.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.best.grocery.R;


/**
 * Created by TienTruong on 8/1/2018.
 */

@SuppressWarnings("CanBeFinal")
public class RecipeDetailHeaderHolder extends RecyclerView.ViewHolder {
    public ImageView mRecipeImage;
    public TextView mRecipeName;
    //public TextView mRecipeUrl;
    public TextView mRecipeDescription;

    public RecipeDetailHeaderHolder(View itemView) {
        super(itemView);
        this.mRecipeImage = itemView.findViewById(R.id.recipe_detail_image);
        this.mRecipeName = itemView.findViewById(R.id.recipe_detail_name);
        //this.mRecipeUrl = itemView.findViewById(R.id.recipe_detail_url);
        this.mRecipeDescription = itemView.findViewById(R.id.recipe_detail_description);
    }
}
