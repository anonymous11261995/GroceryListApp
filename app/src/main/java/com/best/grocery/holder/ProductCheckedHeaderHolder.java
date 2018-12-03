package com.best.grocery.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;


import com.best.grocery.R;
import com.best.grocery.utils.DefinitionSchema;


@SuppressWarnings("CanBeFinal")
public class ProductCheckedHeaderHolder extends RecyclerView.ViewHolder implements DefinitionSchema {
    @SuppressWarnings("FieldCanBeLocal")
    public Button mButtonUnCheckAll;
    public Button mButtonDeleteAll;

    public ProductCheckedHeaderHolder(View itemView) {
        super(itemView);
        mButtonDeleteAll = itemView.findViewById(R.id.action_delete_all);
        mButtonUnCheckAll = itemView.findViewById(R.id.action_uncheck_all);
    }
}
