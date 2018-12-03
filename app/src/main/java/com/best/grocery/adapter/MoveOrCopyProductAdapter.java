package com.best.grocery.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.best.grocery.R;
import com.best.grocery.entity.Product;
import com.best.grocery.fragment.MoveOrCopyProductFragment;
import com.best.grocery.holder.HeaderListProductHolder;
import com.best.grocery.holder.ItemMoveOrCopyHolder;
import com.best.grocery.service.ProductService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;


public class MoveOrCopyProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DefinitionSchema {
    private static final String TAG = MoveOrCopyProductAdapter.class.getSimpleName();
    private static final int ITEM_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    private ArrayList<Product> mData = new ArrayList<>();
    private ArrayList<Product> mDataChecked = new ArrayList<>();
    private Context mContext;
    private ProductService mProductService;

    public MoveOrCopyProductAdapter(Context context, ArrayList<Product> data) {
        this.mContext = context;
        mData.addAll(data);
        mProductService = new ProductService(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
//        Log.d(TAG, "view: " + viewType);
        switch (viewType) {
            case ITEM_TYPE: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_copy_or_move, parent, false);
                return new ItemMoveOrCopyHolder(view);
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
            if (holder instanceof ItemMoveOrCopyHolder) {
                ItemMoveOrCopyHolder vh = (ItemMoveOrCopyHolder) holder;
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

    private void onBindItemViewHolder(final ItemMoveOrCopyHolder holder, final int position) {
        final Product product = mData.get(position);
        holder.itemContent.setText(product.getContent());
        String description = mProductService.getDescription(product);
        if (!description.equals("")) {
            holder.itemDescription.setVisibility(View.VISIBLE);
            holder.itemDescription.setText(description);
        } else {
            holder.itemDescription.setVisibility(View.GONE);
        }
        if(product.isChecked()){
            holder.itemContent.setPaintFlags(holder.itemContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
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
                updateViewBottom();
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
                updateViewBottom();
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

    private void updateViewBottom(){
        if(mDataChecked.size() ==0){
            MoveOrCopyProductFragment.mLayoutBottom.setVisibility(View.GONE);
        }
        else {
            MoveOrCopyProductFragment.mLayoutBottom.setVisibility(View.VISIBLE);
        }
    }

}
