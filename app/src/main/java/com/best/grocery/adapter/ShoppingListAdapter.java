package com.best.grocery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.helper.SwipeAndDragShopping;
import com.best.grocery.dialog.CustomDialog;
import com.best.grocery.entity.Category;
import com.best.grocery.entity.Product;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.fragment.ProductDetailFragment;
import com.best.grocery.fragment.ShoppingListFragment;
import com.best.grocery.holder.ProductCheckedHeaderHolder;
import com.best.grocery.holder.ProductCheckedItemHolder;
import com.best.grocery.holder.HeaderListProductHolder;
import com.best.grocery.holder.ProductItemHolder;
import com.best.grocery.service.ProductService;
import com.best.grocery.service.ShoppingListService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.Collections;
import java.util.Date;

/**
 * Created by TienTruong on 7/27/2018.
 */

@SuppressWarnings("CanBeFinal")
public class ShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DefinitionSchema, SwipeAndDragShopping.ActionCompletionContract {
    private static final String TAG = ShoppingListAdapter.class.getSimpleName();
    private static final String CURRENCY_DEFUALT = AppConfig.getCurrencySymbol();
    private static final int ITEM_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    private static final int ITEM_CHECKED_TYPE = 3;
    private static final int HEADER_CHECKED_TYPE = 4;
    private FragmentActivity mActivity;
    private Context mContext;
    private ShoppingListService mShoppingListService;
    private ProductService mProductService;
    private ItemTouchHelper touchHelper;
    private ShoppingList mShoppingList;


    public ShoppingListAdapter(FragmentActivity fragmentActivity, Context context, ShoppingList shoppingList) {
        this.mActivity = fragmentActivity;
        this.mContext = context;
        this.mShoppingList = shoppingList;
        this.mShoppingListService = new ShoppingListService(context);
        this.mProductService = new ProductService(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        //Log.d(TAG, "view: " + viewType);
        switch (viewType) {
            case ITEM_TYPE: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_shopping_normal, parent, false);
                return new ProductItemHolder(view);
            }
            case HEADER_TYPE: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_list_product, parent, false);
                return new HeaderListProductHolder(view);
            }
            case HEADER_CHECKED_TYPE: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_product_checked, parent, false);
                return new ProductCheckedHeaderHolder(view);
            }
            default: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_shopping_checked, parent, false);
                return new ProductCheckedItemHolder(view);
            }
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof ProductItemHolder) {
                ProductItemHolder vh = (ProductItemHolder) holder;
                onBindItemViewHolder(vh, position);

            } else if (holder instanceof HeaderListProductHolder) {
                HeaderListProductHolder vh = (HeaderListProductHolder) holder;
                onBindHeaderViewHolder(vh, position);

            } else if (holder instanceof ProductCheckedItemHolder) {
                ProductCheckedItemHolder vh = (ProductCheckedItemHolder) holder;
                onBindItemCheckedViewHolder(vh, position);

            } else if (holder instanceof ProductCheckedHeaderHolder) {
                ProductCheckedHeaderHolder vh = (ProductCheckedHeaderHolder) holder;
                onBindHeaderCheckedViewHolder(vh, position);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("EmptyMethod")
    private void onBindHeaderCheckedViewHolder(ProductCheckedHeaderHolder holder, int position) {
        int width = mContext.getResources().getDisplayMetrics().widthPixels / 2;
        Log.d("phone", "width: " + width);
        holder.mButtonUnCheckAll.setWidth(width - 30);
        holder.mButtonDeleteAll.setWidth(width - 30);
        //event
        holder.mButtonDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(mContext);
                customDialog.onCreate(mContext.getString(R.string.dialog_message_confirm_delete_all_item)
                        , mContext.getString(R.string.abc_delete), mContext.getString(R.string.abc_cancel));
                customDialog.setListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        mShoppingListService.clearAllProductChecked(mShoppingList);
                        buildViewShoppingList();
                    }

                });

            }
        });

        holder.mButtonUnCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(mContext);
                customDialog.onCreate(mContext.getString(R.string.dialog_message_confirm_uncheck_all_item),
                        mContext.getString(R.string.abc_confirm), mContext.getString(R.string.abc_cancel));
                customDialog.setListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        mShoppingListService.unCheckAll(mShoppingList);
                        buildViewShoppingList();
                    }

                });

            }
        });


    }

    private void onBindHeaderViewHolder(HeaderListProductHolder holder, int position) {
        Category category = mProductService.getCategoryOfProduct(ShoppingListFragment.mData.get(position));
        holder.headerName.setText(category.getName());
    }

    private void onBindItemCheckedViewHolder(ProductCheckedItemHolder holder, final int position) {
        final Product product = ShoppingListFragment.mData.get(position);
        holder.itemLayout.setVisibility(View.VISIBLE);
        holder.itemContent.setText(product.getContent());
        holder.itemContent.setPaintFlags(holder.itemContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        String description = mProductService.getDescription(product);
        if (!description.equals("")) {
            holder.itemDescription.setVisibility(View.VISIBLE);
            holder.itemDescription.setText(description);
        } else {
            holder.itemDescription.setVisibility(View.GONE);
        }
        holder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "You clicked " + product.getName() + " on row number " + position + " to change name");
                ProductDetailFragment productDetailFragment = new ProductDetailFragment();
                productDetailFragment.setProduct(product);
                activeFragment(productDetailFragment);

            }
        });
        holder.itemCheckBox.setChecked(true);
        holder.itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_effect));
                if (product.isChecked()) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            product.setChecked(false);
                            product.setLastChecked(new Date());
                            mProductService.updateProduct(product);
                            buildViewShoppingList();
                        }
                    }, AppConfig.DELAY_EFFECT);
                }
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void onBindItemViewHolder(ProductItemHolder viewHolder, int pos) {
        final ProductItemHolder holder = viewHolder;
        final int position = pos;
        final Product product = ShoppingListFragment.mData.get(position);
        //Log.d(TAG, "Position: " + position + " content: " + product.getName());
        holder.itemLayout.setVisibility(View.VISIBLE);
        holder.swipeLayout.setVisibility(View.GONE);
        holder.itemContent.setText(product.getContent());
        holder.itemCheckBox.setChecked(false);
        String description = mProductService.getDescription(product);
        if (!description.equals("")) {
            holder.itemDescription.setVisibility(View.VISIBLE);
            holder.itemDescription.setText(description);
        } else {
            holder.itemDescription.setVisibility(View.GONE);
        }

        holder.itemMove.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                touchHelper.startDrag(holder);
                return false;
            }
        });
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("abcd", "kdjfkd");
            }
        });

        holder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "You clicked " + product.getName() + " on row number " + position + " to change name");
                ProductDetailFragment productDetailFragment = new ProductDetailFragment();
                productDetailFragment.setProduct(product);
                activeFragment(productDetailFragment);

            }
        });
        holder.itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_effect));
                if (!product.isChecked()) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            product.setChecked(true);
                            product.setLastChecked(new Date());
                            mProductService.updateProduct(product);
                            buildViewShoppingList();
                        }
                    }, AppConfig.DELAY_EFFECT);
                }
            }
        });
        //showcaseGuide(viewHolder, pos);
    }


    private void buildViewShoppingList() {
        ShoppingListFragment.mData = mShoppingListService.productShoppingSrceen(mShoppingList);
        if (ShoppingListFragment.mData.size() == 0) {
            ShoppingListFragment.mGuide.setVisibility(View.VISIBLE);
        } else {
            ShoppingListFragment.mGuide.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
        buidInfoShoppingList();
    }

    @Override
    public int getItemViewType(int position) {
        //Log.d("abc", "size: " + mData.size());

        if (ShoppingListFragment.mData.size() == 0)
            return super.getItemViewType(position);

        Product product = ShoppingListFragment.mData.get(position);

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

    @Override
    public int getItemCount() {
        return ShoppingListFragment.mData.size();
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        Log.v(TAG, "Log move position: " + oldPosition + " to " + newPosition);
        int limit;
        int productCheckedIndex = -1;
        for (int i = 0; i < ShoppingListFragment.mData.size(); i++) {
            Product product = ShoppingListFragment.mData.get(i);
            if (product.isChecked()) {
                productCheckedIndex = i;
                break;
            }
        }
        if (productCheckedIndex != -1) limit = productCheckedIndex;
        else limit = ShoppingListFragment.mData.size();
        if (oldPosition < limit && newPosition < limit && newPosition > 0) {
            Product product = ShoppingListFragment.mData.get(oldPosition);
            product.setModified(new Date());
            mProductService.updateProduct(product);
            Log.d(TAG, "name: " + product.getName() + " modfield: " + product.getModified());
            if (oldPosition < newPosition) {
                for (int i = oldPosition; i < newPosition; i++) {
                    Collections.swap(ShoppingListFragment.mData, i, i + 1);
                }
            } else {
                for (int i = oldPosition; i > newPosition; i--) {
                    Collections.swap(ShoppingListFragment.mData, i, i - 1);
                }
            }
            notifyItemMoved(oldPosition, newPosition);
            convertListToDatabase();
        }
    }

    @Override
    public void onViewSwiped(int position, int direction) {
        Product product = ShoppingListFragment.mData.get(position);
        Log.d("AAAA", "direction: " + direction);
        if (direction == ItemTouchHelper.RIGHT) {
            mProductService.deleteProductFromList(product);
            buildViewShoppingList();

        } else {
            product.setHide(true);
            mProductService.updateProduct(product);
            buildViewShoppingList();
            Log.d(TAG, "Snooze item");
        }
    }


    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = mActivity.getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void convertListToDatabase() {
        int k = 0;
        Category categoryUpdate = null;
        for (Product p : ShoppingListFragment.mData) {
            if (p.getCategory().getId() == null) {
                break;
            }
            if (p.getName() == null) {
                k = 0;
                categoryUpdate = p.getCategory();
            } else {
                p.setOrderInGroup(k);
                p.setCategory(categoryUpdate);
                mProductService.updateProduct(p);
                k++;
            }

        }
    }

    private void buidInfoShoppingList() {
        Double priceCart = 0.0, priceList = 0.0;
        int itemCart = 0, itemList = 0;
        for (Product product : ShoppingListFragment.mData) {
            if (!TextUtils.isEmpty(product.getName())) {
                Double price = (product.getQuantity() * product.getUnitPrice());
                if (product.isChecked()) {
                    itemList++;
                    priceList += price;
                    itemCart++;
                    priceCart += price;
                } else {
                    itemList++;
                    priceList += price;
                }
            }
        }
        String priceCartText = CURRENCY_DEFUALT + mProductService.parserPrice(priceCart);
        String priceListText = CURRENCY_DEFUALT + mProductService.parserPrice(priceList);
        if (priceCart == 0.0) {
            priceCartText = CURRENCY_DEFUALT + "0.0";
        }
        if (priceList == 0.0) {
            priceListText = CURRENCY_DEFUALT + "0.0";
        }
        String countCartText = String.valueOf(itemCart);
        String countListText = String.valueOf(itemList);
        if (itemList == 0.0) {
            Log.d(TAG, "hide info list");
            ShoppingListFragment.mLayoutInfo.setVisibility(View.GONE);

        } else {
            ShoppingListFragment.mLayoutInfo.setVisibility(View.VISIBLE);
            ShoppingListFragment.mCartPriceInfo.setText(priceCartText);
            ShoppingListFragment.mCartCountInfo.setText(countCartText);
            ShoppingListFragment.mListCountInfo.setText(countListText);
            ShoppingListFragment.mListPriceInfo.setText(priceListText);

        }
    }

}
