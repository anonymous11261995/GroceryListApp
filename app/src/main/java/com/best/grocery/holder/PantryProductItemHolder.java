package com.best.grocery.holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.best.grocery.R;


public class PantryProductItemHolder extends RecyclerView.ViewHolder {
    public TextView itemContent;
    public TextView itemDescription;
    public ConstraintLayout itemMove;
    public CheckBox itemCheckBox;
    public ConstraintLayout group;
    public CardView itemLayout;
    public ConstraintLayout swipeLayout;
    public TextView undo;
    public TextView btnLow;
    public TextView btnFull;

    public PantryProductItemHolder(View itemView) {
        super(itemView);
        itemContent = itemView.findViewById(R.id.pantry_product_content);
        itemCheckBox = itemView.findViewById(R.id.pantry_product_checkbox);
        itemDescription = itemView.findViewById(R.id.pantry_product_description);
        group = itemView.findViewById(R.id.pantry_product_constraint);
        undo = itemView.findViewById(R.id.text_undo);
        swipeLayout = itemView.findViewById(R.id.layout_swipe);
        itemLayout = itemView.findViewById(R.id.pantry_product_layout);
        btnFull = itemView.findViewById(R.id.pantry_list_btn_full);
        btnLow = itemView.findViewById(R.id.pantry_list_btn_low);
        itemMove = itemView.findViewById(R.id.pantry_item_move);
    }
}
