package com.best.grocery.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.best.grocery.R;


/**
 * Created by TienTruong on 7/26/2018.
 */

@SuppressWarnings("CanBeFinal")
public class MasterListItemHolder extends RecyclerView.ViewHolder {
    public TextView myTextView;
    public ImageView mInfo;
    public ImageView mDelete;
    public CheckBox mCheckBox;

    public MasterListItemHolder(View itemView) {
        super(itemView);
        myTextView = itemView.findViewById(R.id.text_name);
        mInfo = itemView.findViewById(R.id.image_info);
        mDelete = itemView.findViewById(R.id.image_delete);
        mCheckBox = itemView.findViewById(R.id.checkbox);
        //event
    }

}
