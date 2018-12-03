package com.best.grocery.holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.best.grocery.R;

@SuppressWarnings("CanBeFinal")
public class ProductCheckedItemHolder extends RecyclerView.ViewHolder{
    public TextView itemContent;
    public TextView itemDescription;
    public CheckBox itemCheckBox;
    public ConstraintLayout group;
    public CardView itemLayout;

    public ProductCheckedItemHolder(View itemView) {
        super(itemView);
        itemContent = itemView.findViewById(R.id.product_checked_content);
        itemCheckBox = itemView.findViewById(R.id.product_checked_checkbox);
        itemDescription = itemView.findViewById(R.id.product_checked_description);
        group = itemView.findViewById(R.id.product_checked_constraint);
        itemLayout = itemView.findViewById(R.id.product_checked_layout);
    }
}
