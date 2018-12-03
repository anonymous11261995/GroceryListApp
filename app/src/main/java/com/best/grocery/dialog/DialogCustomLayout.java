package com.best.grocery.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.best.grocery.R;
import com.best.grocery.fragment.ShoppingListFragment;

public class DialogCustomLayout {
    private Context mContext;
    OnClickListener listener;

    public DialogCustomLayout(Context context) {
        this.mContext = context;
    }

    public void onCreate(String message, String valueEditText){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View alertView = inflater.inflate(R.layout.view_dialog_create, null);
        TextView titleView = alertView.findViewById(R.id.dialog_create_title);
        titleView.setText(message);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertView);
        final EditText editView = alertView.findViewById(R.id.dialog_create_content);
        editView.setText(valueEditText);
        builder.setPositiveButton(mContext.getString(R.string.abc_create), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String name = editView.getText().toString().trim();
                listener.onClickPositiveButton(dialog,name);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(mContext.getString(R.string.abc_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
        void onClickPositiveButton(DialogInterface dialog, String text);
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }
}
