package com.best.grocery.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.best.grocery.R;
import com.best.grocery.entity.Product;
import com.best.grocery.holder.ItemShoppingSnoozeHolder;
import com.best.grocery.service.ProductService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;

public class ShoppingSnoozeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DefinitionSchema {
    private ArrayList<Product> mData;
    private ProductService mProductService;
    private ArrayList<Product> mDataChecked = new ArrayList<>();

    public ShoppingSnoozeAdapter(Context contex, ArrayList<Product> data) {
        this.mData = data;
        mProductService = new ProductService(contex);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_shopping_snooze, viewGroup, false);
        return new ItemShoppingSnoozeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final ItemShoppingSnoozeHolder holder = (ItemShoppingSnoozeHolder) viewHolder;
        final Product product = mData.get(i);
        holder.itemContent.setText(product.getContent());
        String description = mProductService.getDescription(product);
        if (!description.equals("")) {
            holder.itemDescription.setVisibility(View.VISIBLE);
            holder.itemDescription.setText(description);
        } else {
            holder.itemDescription.setVisibility(View.GONE);
        }

        holder.itemCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDataChecked.contains(product)){
                    holder.itemCheckbox.setChecked(false);
                    int index = mDataChecked.indexOf(product);
                    mDataChecked.remove(index);
                }
                else {
                    holder.itemCheckbox.setChecked(true);
                    mDataChecked.add(product);
                }
            }
        });
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDataChecked.contains(product)){
                    holder.itemCheckbox.setChecked(false);
                    int index = mDataChecked.indexOf(product);
                    mDataChecked.remove(index);
                }
                else {
                    holder.itemCheckbox.setChecked(true);
                    mDataChecked.add(product);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<Product> getDataChecked() {
        return mDataChecked;
    }
}
