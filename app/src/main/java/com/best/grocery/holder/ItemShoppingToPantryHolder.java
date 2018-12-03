package com.best.grocery.holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.best.grocery.R;

public class ItemShoppingToPantryHolder extends RecyclerView.ViewHolder {
    public TextView itemContent;
    public CheckBox itemCheckBox;
    public CardView itemLayout;

    public ItemShoppingToPantryHolder(View itemView) {
        super(itemView);
        itemContent = itemView.findViewById(R.id.item_content);
        itemCheckBox = itemView.findViewById(R.id.item_checkbox);
        itemLayout = itemView.findViewById(R.id.item_layout);
    }
}
