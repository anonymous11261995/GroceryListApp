package com.best.grocery.adapter;

import android.content.Context;
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
import com.best.grocery.entity.Category;
import com.best.grocery.entity.Product;
import com.best.grocery.fragment.PantryListFragment;
import com.best.grocery.fragment.ProductPantryFragment;
import com.best.grocery.holder.PantryProductItemHolder;
import com.best.grocery.holder.HeaderListProductHolder;
import com.best.grocery.service.PantryListService;
import com.best.grocery.service.ProductService;
import com.best.grocery.utils.DefinitionSchema;
import com.best.grocery.helper.SwipeAndDrag;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class PantryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DefinitionSchema, SwipeAndDrag.ActionCompletionContract {
    private static final String TAG = ShoppingListAdapter.class.getSimpleName();
    private static final int ITEM_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    private FragmentActivity mActivity;
    private Context mContext;
    private ItemTouchHelper touchHelper;
    private ArrayList<String> mItemsPendingRemoval;
    private PantryListService mPantryListService;
    private ProductService mProductService;
    private SimpleDateFormat formatter = new SimpleDateFormat(AppConfig.DATE_SERVER_PATTERN);

    private static final int PENDING_REMOVAL_TIMEOUT = AppConfig.PENDING_REMOVAL_TIMEOUT; // 3sec
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    private HashMap<String, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

    public PantryListAdapter(FragmentActivity fragmentActivity, Context context) {
        this.mActivity = fragmentActivity;
        this.mContext = context;
        this.mProductService = new ProductService(context);
        this.mPantryListService = new PantryListService(context);
        mItemsPendingRemoval = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        //Log.d(TAG, "view: " + viewType);
        switch (viewType) {
            case ITEM_TYPE: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_pantry, parent, false);
                return new PantryProductItemHolder(view);
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
        if (holder instanceof PantryProductItemHolder) {
            PantryProductItemHolder vh = (PantryProductItemHolder) holder;
            onBindItemViewHolder(vh, position);

        } else if (holder instanceof HeaderListProductHolder) {
            HeaderListProductHolder vh = (HeaderListProductHolder) holder;
            onBindHeaderViewHolder(vh, position);

        }

    }

    private void onBindHeaderViewHolder(HeaderListProductHolder vh, int position) {
        Log.d("ABCD", " id_ctegory: " + PantryListFragment.mData.get(position).getCategory().getId());
        Category category = mProductService.getCategoryOfProduct(PantryListFragment.mData.get(position));
        vh.headerName.setText(category.getName());
    }

    private void onBindItemViewHolder(final PantryProductItemHolder holder, int pos) {
        final int position = pos;
        final Product product = PantryListFragment.mData.get(position);
        if (mItemsPendingRemoval.contains(product.getId())) {
            Log.d(TAG, "Pending removal this item: " + product.getName());
            holder.itemLayout.setVisibility(View.GONE);
            holder.swipeLayout.setVisibility(View.VISIBLE);
            holder.undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    undoOpt(product);
                }
            });
        } else {
            holder.itemLayout.setVisibility(View.VISIBLE);
            holder.swipeLayout.setVisibility(View.GONE);
            holder.itemContent.setText(product.getContent());
            if (product.getExpired().after(new Date(0))) {
                holder.itemDescription.setVisibility(View.VISIBLE);
                String textExpired = mContext.getString(R.string.abc_text_expired) + " " + formatter.format(product.getExpired());
                holder.itemDescription.setText(textExpired);
            } else {
                holder.itemDescription.setVisibility(View.GONE);
            }
            if (PantryListFragment.mDataChecked.contains(product.getId())) {
                holder.itemCheckBox.setChecked(true);
            } else {
                holder.itemCheckBox.setChecked(false);
            }
            String state = product.getState();
            if (state == null) state = "";
            if (state.equals(PRODUCT_LOW)) {
                holder.btnLow.setTextColor(mActivity.getResources().getColor(R.color.text_pantry_low_full_active));
                holder.btnLow.setTextSize(18);
                holder.btnFull.setTextColor(mActivity.getResources().getColor(R.color.text_pantry_low_full_no_active));
                holder.btnFull.setTextSize(15);
            } else {
                holder.btnLow.setTextColor(mActivity.getResources().getColor(R.color.text_pantry_low_full_no_active));
                holder.btnLow.setTextSize(15);
                holder.btnFull.setTextColor(mActivity.getResources().getColor(R.color.text_pantry_low_full_active));
                holder.btnFull.setTextSize(18);
            }

            holder.itemMove.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    touchHelper.startDrag(holder);
                    return false;
                }
            });

            holder.itemCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_effect));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (PantryListFragment.mDataChecked.contains(product.getId())) {
                                PantryListFragment.mDataChecked.remove(product.getId());
                                buildViewList();
                            } else {
                                PantryListFragment.mDataChecked.add(product.getId());
                                buildViewList();
                            }
                        }
                    }, AppConfig.DELAY_EFFECT);

                }
            });

            holder.btnLow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_effect));
                    holder.btnLow.setTextColor(mActivity.getResources().getColor(R.color.text_pantry_low_full_active));
                    holder.btnLow.setTextSize(18);
                    holder.btnFull.setTextColor(mActivity.getResources().getColor(R.color.text_pantry_low_full_no_active));
                    holder.btnFull.setTextSize(15);
                    product.setState(PRODUCT_LOW);
                    mProductService.updateProduct(product);
                }
            });
            holder.btnFull.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_effect));
                    holder.btnLow.setTextColor(mActivity.getResources().getColor(R.color.text_pantry_low_full_no_active));
                    holder.btnLow.setTextSize(15);
                    holder.btnFull.setTextColor(mActivity.getResources().getColor(R.color.text_pantry_low_full_active));
                    holder.btnFull.setTextSize(18);
                    product.setState(PRODUCT_FULL);
                    mProductService.updateProduct(product);
                }
            });
            holder.group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "You clicked " + product.getName() + " on row number " + position + " to change name");

                    ProductPantryFragment fragment = new ProductPantryFragment();
                    fragment.setProduct(product);
                    activeFragment(fragment);

                }
            });

        }
    }

    private void buildViewList() {
        PantryListFragment.mData = mPantryListService.productPantry(PantryListFragment.mPantryList);
        if (PantryListFragment.mData.size() == 0) {
            PantryListFragment.mGuide.setVisibility(View.VISIBLE);
        } else {
            PantryListFragment.mGuide.setVisibility(View.GONE);
        }
        if (PantryListFragment.mDataChecked.size() == 0) {
            PantryListFragment.mLayoutBottom.setVisibility(View.GONE);
        } else {
            PantryListFragment.mLayoutBottom.setVisibility(View.VISIBLE);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (PantryListFragment.mData.size() == 0)
            return super.getItemViewType(position);

        Product product = PantryListFragment.mData.get(position);
        if (TextUtils.isEmpty(product.getName())) {
            return HEADER_TYPE;
        }
        return ITEM_TYPE;
    }


    @Override
    public int getItemCount() {
        return PantryListFragment.mData.size();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        Log.v(TAG, "Log move position: " + oldPosition + " to " + newPosition);
        int limit;
        int productCheckedIndex = -1;
        for (int i = 0; i < PantryListFragment.mData.size(); i++) {
            Product product = PantryListFragment.mData.get(i);
            if (product.getCategory().getId() == null) {
                productCheckedIndex = i;
                break;
            }
        }
        if (productCheckedIndex != -1) limit = productCheckedIndex;
        else limit = PantryListFragment.mData.size();
        if (oldPosition < limit && newPosition < limit && newPosition > 0) {
            Product product = PantryListFragment.mData.get(oldPosition);
            product.setModified(new Date());
            mProductService.updateProduct(product);
            if (oldPosition < newPosition) {
                for (int i = oldPosition; i < newPosition; i++) {
                    Collections.swap(PantryListFragment.mData, i, i + 1);
                }
            } else {
                for (int i = oldPosition; i > newPosition; i--) {
                    Collections.swap(PantryListFragment.mData, i, i - 1);
                }
            }
            notifyItemMoved(oldPosition, newPosition);
            saveDataBeforeMove();
        }

    }

    private void saveDataBeforeMove() {
        int k = 0;
        Category categoryUpdate = null;
        for (Product p : PantryListFragment.mData) {
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

    @Override
    public void onViewSwiped(int position) {
        for (String key : mItemsPendingRemoval) {
            Product product1 = mProductService.findProductById(key);
            remove(product1);

        }
        final Product product = PantryListFragment.mData.get(position);
        if (!mItemsPendingRemoval.contains(product.getId())) {
            Log.d(TAG, "Add to list pending removal");
            mItemsPendingRemoval.add(product.getId());
            // this will redraw row in "undo" state
            buildViewList();
            // let's create, store and post a runnable to remove the data
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(product);
                    if (PantryListFragment.mDataChecked.contains(product.getId())) {
                        PantryListFragment.mDataChecked.remove(product.getId());
                    }
                    buildViewList();

                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(product.getId(), pendingRemovalRunnable);
        }

    }

    private void undoOpt(Product product) {
        Runnable pendingRemovalRunnable = pendingRunnables.get(product.getId());
        pendingRunnables.remove(product.getId());
        if (pendingRemovalRunnable != null)
            handler.removeCallbacks(pendingRemovalRunnable);
        mItemsPendingRemoval.remove(product.getId());
        buildViewList();
    }

    private void remove(Product product) {
        if (mItemsPendingRemoval.contains(product.getId())) {
            mItemsPendingRemoval.remove(product.getId());
        }
        mPantryListService.removeProduct(product);
        pendingRunnables.remove(product.getId());
    }

    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = mActivity.getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }
}
