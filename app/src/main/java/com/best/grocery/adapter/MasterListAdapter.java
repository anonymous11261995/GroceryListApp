package com.best.grocery.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.best.grocery.R;
import com.best.grocery.fragment.MasterListDetailItemFragment;
import com.best.grocery.fragment.MasterListFragment;
import com.best.grocery.holder.MasterListItemHolder;
import com.best.grocery.service.ProductService;

import java.util.ArrayList;

/**
 * Created by TienTruong on 7/25/2018.
 */

@SuppressWarnings("CanBeFinal")
public class MasterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = MasterListAdapter.class.getSimpleName();
    private ArrayList<String> mData;
    private ArrayList<String> mDataChoosed;
    private ProductService mProductService;
    private Context mContext;
    private FragmentActivity mActivity;


    public MasterListAdapter(ArrayList<String> data, Context context, FragmentActivity activity) {
        this.mData = data;
        mContext = context;
        mActivity = activity;
        this.mDataChoosed = new ArrayList<>();
        this.mProductService = new ProductService(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_master_list, parent, false);
        return new MasterListItemHolder(view);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int pos) {
        final MasterListItemHolder holder = (MasterListItemHolder) viewHolder;
        final String text = mData.get(pos);
        holder.myTextView.setText(text);
        if (mDataChoosed.contains(text)) {
            holder.mCheckBox.setChecked(true);
        } else {
            holder.mCheckBox.setChecked(false);
        }
        //event
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDataChoosed.contains(text)) mDataChoosed.add(text);
                else {
                    mDataChoosed.remove(text);
                }
                setTextButton();
            }
        });
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(mContext.getString(R.string.dialog_message_confirm_delete_list));
                builder.setPositiveButton(mContext.getString(R.string.abc_delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mProductService.deleteItemMasterList(text);
                        mData.remove(text);
                        if (mDataChoosed.contains(text)) {
                            mDataChoosed.remove(text);
                        }
                        notifyDataSetChanged();
                        setTextButton();
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

            }
        });
        holder.myTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mCheckBox.isChecked()) {
                    holder.mCheckBox.setChecked(false);
                    if (mDataChoosed.contains(text)) mDataChoosed.remove(text);
                } else {
                    holder.mCheckBox.setChecked(true);
                    if (!mDataChoosed.contains(text)) mDataChoosed.add(text);
                }
                setTextButton();
            }
        });

        holder.mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MasterListDetailItemFragment fragment = new MasterListDetailItemFragment();
                fragment.setmText(text);
                activeFragment(fragment);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<String> getProductChoosed() {
        return mDataChoosed;
    }

    private void setTextButton() {
        String textButton = mContext.getString(R.string.abc_add);
        int size = mDataChoosed.size();
        if (size == 0) {
            MasterListFragment.mButton.setText(textButton);
        } else {
            MasterListFragment.mButton.setText(textButton + "(" + size + ")");
        }
    }

    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = mActivity.getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }
}
