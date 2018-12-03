package com.best.grocery.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.entity.Product;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.holder.ProductCheckedHeaderHolder;
import com.best.grocery.holder.HeaderListProductHolder;
import com.best.grocery.holder.ItemCheckedMultiListHolder;
import com.best.grocery.holder.ItemMultiListHolder;
import com.best.grocery.service.ProductService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;

public class MultiListShoppingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DefinitionSchema {
    private static final String TAG = MultiListShoppingAdapter.class.getSimpleName();
    private static final int ITEM_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    private static final int ITEM_CHECKED_TYPE = 3;
    private static final int HEADER_CHECKED_TYPE = 4;
    private ArrayList<Product> mData;
    private Context mContext;
    private ProductService mProductService;
    private OnItemClickListener listener;


    public MultiListShoppingAdapter(FragmentActivity fragmentActivity, Context context, ArrayList<Product> data) {
        this.mContext = context;
        this.mProductService = new ProductService(context);
        mData = new ArrayList<>(data);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ITEM_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_multi_list, parent, false);
                return new ItemMultiListHolder(view);
            case ITEM_CHECKED_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_shopping_view_checked, parent, false);
                return new ItemCheckedMultiListHolder(view);
            case HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_list_product, parent, false);
                return new HeaderListProductHolder(view);
            default: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_product_checked, parent, false);
                return new ProductCheckedHeaderHolder(view);
            }
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemMultiListHolder) {
            ItemMultiListHolder vh = (ItemMultiListHolder) holder;
            onBindItem(vh, position);

        } else if (holder instanceof ItemCheckedMultiListHolder) {
            ItemCheckedMultiListHolder vh = (ItemCheckedMultiListHolder) holder;
            onBindItemChecked(vh, position);

        } else if (holder instanceof HeaderListProductHolder) {
            HeaderListProductHolder vh = (HeaderListProductHolder) holder;
            onBindHeader(vh, position);

        } else {
            ProductCheckedHeaderHolder vh = (ProductCheckedHeaderHolder) holder;
            onBindHeaderChecked(vh, position);
        }
    }


    private void onBindHeader(HeaderListProductHolder vh, int position) {
        Product product = mData.get(position);
        vh.headerName.setText(product.getCategory().getName());
    }

    private void onBindItem(ItemMultiListHolder holder, int position) {
        final Product product = mData.get(position);

        holder.itemContent.setText(product.getContent());
        String description = mProductService.getDescription(product);
        //Log.d(TAG,"description: " + description);
        if (!description.equals("")) {
            holder.itemDescription.setVisibility(View.VISIBLE);
            holder.itemDescription.setText(description);
        } else {
            holder.itemDescription.setVisibility(View.GONE);
        }
        holder.itemCheckBox.setChecked(false);
        ShoppingList shoppingList = product.getShoppingList();
        holder.itemColor.setBackgroundColor(shoppingList.getColor());

        //event
        holder.itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickListener(product);
            }
        });

    }

    private void onBindItemChecked(ItemCheckedMultiListHolder holder, int position) {
        final Product product = mData.get(position);
        holder.itemContent.setText(product.getContent());
        holder.itemContent.setPaintFlags(holder.itemContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        String description = mProductService.getDescription(product);
        if (!description.equals("")) {
            holder.itemDescription.setVisibility(View.VISIBLE);
            holder.itemDescription.setText(description);
        } else {
            holder.itemDescription.setVisibility(View.GONE);
        }
        holder.itemCheckBox.setChecked(true);

        ShoppingList shoppingList = product.getShoppingList();
        holder.itemColor.setBackgroundColor(shoppingList.getColor());

        //event
        holder.itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickListener(product);
            }
        });
    }

    private void onBindHeaderChecked(ProductCheckedHeaderHolder holder, int position) {
        int width = mContext.getResources().getDisplayMetrics().widthPixels / 2;
        holder.mButtonUnCheckAll.setWidth(width - 30);
        holder.mButtonDeleteAll.setWidth(width - 30);
        holder.mButtonDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                listener.onHeaderClickListener(id);
            }
        });

        holder.mButtonUnCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                listener.onHeaderClickListener(id);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.size() == 0) {
            return super.getItemViewType(position);
        }
        Product product = mData.get(position);
        if (product.isChecked() && TextUtils.isEmpty(product.getName())) {
            return HEADER_CHECKED_TYPE;
        }
        if (product.isChecked() && !TextUtils.isEmpty(product.getName())) {
            return ITEM_CHECKED_TYPE;
        }
        //Log.d("Build list", "Position: " + position + " name: " + mData.get(position).getName());
        if (TextUtils.isEmpty(product.getName())) {
            return HEADER_TYPE;
        }
        return ITEM_TYPE;
    }

    public void loadAgianList(ArrayList<Product> data) {
        mData.clear();
        mData.addAll(data);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, AppConfig.DELAY_EFFECT);
    }

    public interface OnItemClickListener{
        void onItemClickListener(Product product);
        void onHeaderClickListener(int id);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
