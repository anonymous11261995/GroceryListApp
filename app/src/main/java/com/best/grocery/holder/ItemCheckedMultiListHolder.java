package com.best.grocery.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.best.grocery.R;

public class ItemCheckedMultiListHolder extends RecyclerView.ViewHolder {
    public TextView itemContent;
    public TextView itemDescription;
    public CheckBox itemCheckBox;
    public View itemColor;

    public ItemCheckedMultiListHolder(View itemView) {
        super(itemView);
        itemContent = itemView.findViewById(R.id.text_name);
        itemCheckBox = itemView.findViewById(R.id.checkbox);
        itemDescription = itemView.findViewById(R.id.text_description);
        itemColor = itemView.findViewById(R.id.view_color);
    }
}
