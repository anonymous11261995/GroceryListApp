package com.best.grocery.holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.best.grocery.R;

/**
 * Created by TienTruong on 7/26/2018.
 */

@SuppressWarnings("CanBeFinal")
public class CategoryItemHolder extends RecyclerView.ViewHolder {
    public TextView myTextView;
    public ImageView mDeleteCategory;
    public ImageView mMoveCategory;
    public CardView itemLayout;
    public CategoryItemHolder(View itemView) {
        super(itemView);
        myTextView = itemView.findViewById(R.id.manage_category_name);
        mDeleteCategory = itemView.findViewById(R.id.manage_category_delete);
        mMoveCategory = itemView.findViewById(R.id.manage_category_move_item);
        itemLayout = itemView.findViewById(R.id.category_item_layout);
    }
}
