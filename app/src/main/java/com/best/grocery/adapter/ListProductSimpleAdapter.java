package com.best.grocery.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.best.grocery.R;
import com.best.grocery.entity.Product;
import com.best.grocery.holder.HeaderListProductHolder;
import com.best.grocery.holder.ProductSimpleItemHolder;
import com.best.grocery.service.ProductService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;

public class ListProductSimpleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DefinitionSchema {
    private static final String TAG = ListProductSimpleAdapter.class.getSimpleName();
    private static final int ITEM_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    private ArrayList<Product> mData = new ArrayList<>();
    private ArrayList<Product> mDataChecked = new ArrayList<>();
    private Context mContext;
    private ProductService mProductService;

    public ListProductSimpleAdapter(FragmentActivity fragmentActivity, Context context, ArrayList<Product> data) {
        this.mContext = context;
        this.mProductService = new ProductService(context);
        mData.addAll(data);
        for(Product product: data){
            if(!TextUtils.isEmpty(product.getName())){
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
                        .inflate(R.layout.item_clipboard, parent, false);
                return new ProductSimpleItemHolder(view);
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
            if (holder instanceof ProductSimpleItemHolder) {
                ProductSimpleItemHolder vh = (ProductSimpleItemHolder) holder;
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

    private void onBindItemViewHolder(final ProductSimpleItemHolder holder, int pos) {
        final int position = pos;
        final Product product = mData.get(position);
        holder.itemContent.setText(product.getContent());
        Log.d(TAG, product.getName());
        if (product.isChecked()) {
            holder.itemContent.setPaintFlags(holder.itemContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemContent.setTextColor(mContext.getResources().getColor(R.color.color_text_no_active));
        }
        String description = mProductService.getDescription(product);
        //Log.d(TAG,"description: " + description);
        if (!description.equals("")) {
            holder.itemDescription.setVisibility(View.VISIBLE);
            holder.itemDescription.setText(description);
        } else {
            holder.itemDescription.setVisibility(View.GONE);
        }

        if (mDataChecked.contains(product)) {
            holder.itemCheckBox.setChecked(true);
        } else {
            holder.itemCheckBox.setChecked(false);
        }
        holder.group.setOnClickListener(new View.OnClickListener() {
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

    public void selectAll(){
        mDataChecked.clear();
        for(Product product: mData){
            if(!TextUtils.isEmpty(product.getName())){
                mDataChecked.add(product);
            }
        }
        notifyDataSetChanged();
    }

    public void unSelectAll(){
        mDataChecked.clear();
        notifyDataSetChanged();
    }

}
