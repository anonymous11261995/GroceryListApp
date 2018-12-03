package com.best.grocery.holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.best.grocery.R;


/**
 * Created by TienTruong on 7/27/2018.
 */

@SuppressWarnings("CanBeFinal")
public class ProductItemHolder extends RecyclerView.ViewHolder {
    public TextView itemContent;
    public TextView itemDescription;
    public ConstraintLayout itemMove;
    public CheckBox itemCheckBox;
    public ConstraintLayout group;
    public CardView itemLayout;
    public ConstraintLayout swipeLayout;
    public TextView undo;

    public ProductItemHolder(View itemView) {
        super(itemView);
        itemContent = itemView.findViewById(R.id.product_content);
        itemMove = itemView.findViewById(R.id.product_move);
        itemCheckBox = itemView.findViewById(R.id.product_checkbox);
        itemDescription = itemView.findViewById(R.id.product_description);
        group = itemView.findViewById(R.id.product_constraint);
        undo = itemView.findViewById(R.id.text_undo);
        swipeLayout = itemView.findViewById(R.id.layout_swipe);
        itemLayout = itemView.findViewById(R.id.product_layout);
    }
}
