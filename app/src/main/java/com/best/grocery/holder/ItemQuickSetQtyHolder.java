package com.best.grocery.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.best.grocery.R;

public class ItemQuickSetQtyHolder extends RecyclerView.ViewHolder {
    public TextView itemName;
    public TextView itemQty;
    public ImageView itemIncrease;
    public ImageView itemDecrease;

    public ItemQuickSetQtyHolder(View itemView) {
        super(itemView);
        itemName = itemView.findViewById(R.id.text_name);
        itemQty = itemView.findViewById(R.id.text_qty);
        itemIncrease = itemView.findViewById(R.id.image_increase);
        itemDecrease = itemView.findViewById(R.id.image_decrease);

    }
}
