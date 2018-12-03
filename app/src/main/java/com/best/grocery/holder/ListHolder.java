package com.best.grocery.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.best.grocery.R;

public class ListHolder extends RecyclerView.ViewHolder {
    public TextView itemName;
    public ImageView itemEdit;
    public ImageView itemDelete;
    public View itemViewColor;

    public ListHolder(@NonNull View itemView) {
        super(itemView);
        itemName = itemView.findViewById(R.id.text_name);
        itemDelete = itemView.findViewById(R.id.image_delete);
        itemViewColor = itemView.findViewById(R.id.view_color);
        itemEdit = itemView.findViewById(R.id.image_edit);
    }
}
