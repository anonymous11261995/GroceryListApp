package com.best.grocery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.best.grocery.R;
import com.best.grocery.entity.Product;
import com.best.grocery.holder.HeaderListProductHolder;
import com.best.grocery.holder.ItemQuickSetQtyHolder;

import java.util.ArrayList;

public class QuickSetQtyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = MoveOrCopyProductAdapter.class.getSimpleName();
    private static final int ITEM_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    private ArrayList<Product> mData = new ArrayList<>();
    private Context mContext;

    public QuickSetQtyAdapter(Context context, ArrayList<Product> data) {
        this.mContext = context;
        mData.addAll(data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ITEM_TYPE: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_quick_set_qty, parent, false);
                return new ItemQuickSetQtyHolder(view);
            }
            default: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_list_product, parent, false);
                return new HeaderListProductHolder(view);
            }
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof ItemQuickSetQtyHolder) {
                ItemQuickSetQtyHolder vh = (ItemQuickSetQtyHolder) holder;
                onBindItemViewHolder(vh, position);

            } else if (holder instanceof HeaderListProductHolder) {
                HeaderListProductHolder vh = (HeaderListProductHolder) holder;
                onBindHeaderViewHolder(vh, position);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onBindHeaderViewHolder(HeaderListProductHolder vh, int position) {
        Product product = mData.get(position);
        vh.headerName.setText(product.getCategory().getName());
    }

    private void onBindItemViewHolder(final ItemQuickSetQtyHolder holder, final int position) {
        final Product product = mData.get(position);
        holder.itemName.setText(product.getName());
        Log.d(TAG,"quantity: " + product.getQuantity());
        holder.itemQty.setText(String.valueOf(product.getQuantity()));

        holder.itemDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.valueOf(holder.itemQty.getText().toString());
                if (quantity != 1) {
                    quantity--;
                }
                holder.itemQty.setText(String.valueOf(quantity));
                product.setQuantity(quantity);
                mData.set(position,product);
            }
        });

        holder.itemIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.valueOf(holder.itemQty.getText().toString());
                quantity++;
                holder.itemQty.setText(String.valueOf(quantity));
                product.setQuantity(quantity);
                mData.set(position,product);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.size() == 0)
            return super.getItemViewType(position);
        Product product = mData.get(position);
        if (TextUtils.isEmpty(product.getName())) {
            return HEADER_TYPE;
        }
        return ITEM_TYPE;
    }

    public ArrayList<Product> getData() {
        return mData;
    }
}
