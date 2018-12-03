package com.best.grocery.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.best.grocery.R;

/**
 * Created by TienTruong on 7/27/2018.
 */

@SuppressWarnings("CanBeFinal")
public class HeaderListProductHolder extends RecyclerView.ViewHolder {
    public TextView headerName;

    public HeaderListProductHolder(View itemView) {
        super(itemView);
        headerName = itemView.findViewById(R.id.text_name);
    }
}
