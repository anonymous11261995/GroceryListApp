package com.best.grocery.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.best.grocery.R;
import com.best.grocery.dialog.CustomDialog;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.fragment.ManageShoppingDetailFragment;
import com.best.grocery.fragment.ShoppingListFragment;
import com.best.grocery.holder.ListHolder;
import com.best.grocery.service.ShoppingListService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;


public class ManageListShoppingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DefinitionSchema {
    private ArrayList<ShoppingList> mData;
    private FragmentActivity mActivity;
    private Context mContext;
    private ShoppingListService mService;

    public ManageListShoppingAdapter(FragmentActivity activity, Context context, ArrayList<ShoppingList> data) {
        this.mService = new ShoppingListService(context);
        this.mData = new ArrayList<>(data);
        mActivity = activity;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manage_list, parent, false);
        return new ListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final ListHolder holder = (ListHolder) viewHolder;
        final ShoppingList shoppingList = mData.get(i);
        holder.itemName.setText(shoppingList.getName());
        if (shoppingList.getColor() != 0) {
            holder.itemViewColor.setBackgroundColor(shoppingList.getColor());
        }
        holder.itemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(mContext);
                customDialog.onCreate(mContext.getString(R.string.dialog_message_confirm_delete_list)
                        , mContext.getString(R.string.abc_delete), mContext.getString(R.string.abc_cancel));
                customDialog.setListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        int pos = holder.getAdapterPosition();
                        mService.deleteShopping(shoppingList);
                        mData.remove(pos);
                        notifyItemRemoved(pos);
                    }
                });

            }
        });
        holder.itemEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageShoppingDetailFragment fragment = new ManageShoppingDetailFragment();
                fragment.setShoppingList(shoppingList);
                activeFragment(fragment);
            }
        });
        holder.itemName.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                mService.activeShopping(shoppingList.getName());
                activeFragment(new ShoppingListFragment());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(ArrayList<ShoppingList> data) {
        mData.clear();
        mData.addAll(data);
    }

    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = mActivity.getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
