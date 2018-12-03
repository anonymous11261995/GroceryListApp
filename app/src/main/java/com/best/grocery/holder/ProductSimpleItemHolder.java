package com.best.grocery.holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.best.grocery.R;


public class ProductSimpleItemHolder extends RecyclerView.ViewHolder {
    public TextView itemContent;
    public TextView itemDescription;
    public CheckBox itemCheckBox;
    public ConstraintLayout group;
    public CardView itemLayout;

    public ProductSimpleItemHolder(View itemView) {
        super(itemView);
        itemContent = itemView.findViewById(R.id.product_clipboard_content);
        itemCheckBox = itemView.findViewById(R.id.product_clipboard_checkbox);
        itemDescription = itemView.findViewById(R.id.product_clipboard_description);
        group = itemView.findViewById(R.id.product_clipboard_constraint);
        itemLayout = itemView.findViewById(R.id.product_clipboard_layout);
    }
}
