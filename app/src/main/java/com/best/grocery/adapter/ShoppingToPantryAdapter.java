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
import com.best.grocery.holder.ItemShoppingToPantryHolder;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;

public class ShoppingToPantryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DefinitionSchema {
    private static final String TAG = ShoppingToPantryAdapter.class.getSimpleName();
    private static final int ITEM_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    private ArrayList<Product> mData = new ArrayList<>();
    private ArrayList<Product> mDataChecked = new ArrayList<>();
    private Context mContext;

    public ShoppingToPantryAdapter(Context context, ArrayList<Product> data) {
        this.mContext = context;
        mData.addAll(data);
        for(Product product: data){
            if(!TextUtils.isEmpty(product.getName()) && product.isChecked()){
                mDataChecked.add(product);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
//        Log.d(TAG, "view: " + viewType);
        switch (viewType) {
            case ITEM_TYPE: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_shopping_to_pantry, parent, false);
                return new ItemShoppingToPantryHolder(view);
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
            if (holder instanceof ItemShoppingToPantryHolder) {
                ItemShoppingToPantryHolder vh = (ItemShoppingToPantryHolder) holder;
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
        Log.d(TAG, " " + product.getCategory().getName());
        vh.headerName.setText(product.getCategory().getName());
    }

    private void onBindItemViewHolder(final ItemShoppingToPantryHolder holder, final int position) {
        final Product product = mData.get(position);
        holder.itemContent.setText(product.getName());
        if (mDataChecked.contains(product)) {
            holder.itemCheckBox.setChecked(true);
        }
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataChecked.contains(product)) {
                    int index = mDataChecked.indexOf(product);
                    mDataChecked.remove(index);
                    holder.itemCheckBox.setChecked(false);
                } else {
                    mDataChecked.add(product);
                    holder.itemCheckBox.setChecked(true);
                }
            }
        });

        holder.itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDataChecked.contains(product)) {
                    int index = mDataChecked.indexOf(product);
                    mDataChecked.remove(index);
                    holder.itemCheckBox.setChecked(false);;
                } else {
                    mDataChecked.add(product);
                    holder.itemCheckBox.setChecked(true);
                }
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

    public ArrayList<Product> getDataChecked() {
        return mDataChecked;
    }

}
