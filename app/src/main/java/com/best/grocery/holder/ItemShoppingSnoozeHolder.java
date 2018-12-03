package com.best.grocery.holder;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.best.grocery.R;

public class ItemShoppingSnoozeHolder extends RecyclerView.ViewHolder {
    public TextView itemContent;
    public TextView itemDescription;
    public CheckBox itemCheckbox;
    public ConstraintLayout itemLayout;

    public ItemShoppingSnoozeHolder(@NonNull View view) {
        super(view);
        itemContent = view.findViewById(R.id.text_content);
        itemDescription = view.findViewById(R.id.text_description);
        itemCheckbox = view.findViewById(R.id.checkbox);
        itemLayout = view.findViewById(R.id.layout_item);
    }
}
