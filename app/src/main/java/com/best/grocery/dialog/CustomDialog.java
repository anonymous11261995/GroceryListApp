package com.best.grocery.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

import com.best.grocery.R;

public class CustomDialog {
    private Context mContext;
    OnClickListener listener;

    public CustomDialog(Context context) {
        this.mContext = context;
    }

    public void onCreate(String message, String textPositiveButton, String textNegativeButton) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message)
                .setPositiveButton(textPositiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onClickPositiveButton(dialog, id);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(textNegativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        Button btnNegative = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        btnNegative.setTextColor(mContext.getResources().getColor(R.color.btn_negative));
        Button btnPositive = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        btnPositive.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
    }

    public interface OnClickListener {
        void onClickPositiveButton(DialogInterface dialog, int id);
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }
}
