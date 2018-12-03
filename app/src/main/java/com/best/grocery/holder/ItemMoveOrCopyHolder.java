package com.best.grocery.holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.best.grocery.R;

public class ItemMoveOrCopyHolder extends RecyclerView.ViewHolder {
        public TextView itemContent;
        public TextView itemDescription;
        public CheckBox itemCheckBox;
        public CardView itemLayout;

        public ItemMoveOrCopyHolder(View itemView) {
            super(itemView);
            itemContent = itemView.findViewById(R.id.text_content);
            itemDescription = itemView.findViewById(R.id.text_description);
            itemCheckBox = itemView.findViewById(R.id.checkbox);
            itemLayout = itemView.findViewById(R.id.layout_item);
        }
}
