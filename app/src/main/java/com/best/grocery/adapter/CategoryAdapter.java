package com.best.grocery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.best.grocery.R;
import com.best.grocery.dialog.CustomDialog;
import com.best.grocery.dialog.DialogCustomLayout;
import com.best.grocery.entity.Category;
import com.best.grocery.holder.CategoryItemHolder;
import com.best.grocery.service.CategoryService;
import com.best.grocery.utils.DefinitionSchema;
import com.best.grocery.helper.SwipeAndDrag;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by TienTruong on 7/24/2018.
 */

@SuppressWarnings("CanBeFinal")
public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DefinitionSchema, SwipeAndDrag.ActionCompletionContract {
    private static final String TAG = CategoryAdapter.class.getSimpleName();
    private ArrayList<Category> mData;
    private CategoryService mCategoryService;
    private ItemTouchHelper touchHelper;
    private Context mContext;

    public CategoryAdapter(Context context, ArrayList<Category> data) {
        this.mData = data;
        this.mCategoryService = new CategoryService(context);

        this.mContext = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryItemHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int pos) {
        final CategoryItemHolder holder = (CategoryItemHolder) viewHolder;
        final Category category = mData.get(pos);
        holder.itemLayout.setVisibility(View.VISIBLE);
        String categoryName = category.getName();
        holder.myTextView.setText(categoryName);
        //event
        holder.mMoveCategory.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    touchHelper.startDrag(holder);
                }
                return false;
            }
        });

        holder.mDeleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(mContext);
                customDialog.onCreate(mContext.getString(R.string.dialog_message_confirm_delete_category)
                        , mContext.getString(R.string.abc_delete), mContext.getString(R.string.abc_cancel));
                customDialog.setListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        int position = holder.getAdapterPosition();
                        mCategoryService.delete(category);
                        mData.remove(position);
                        notifyItemRemoved(position);
                    }
                });
            }
        });
        holder.myTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCustomLayout dialogCustomLayout = new DialogCustomLayout(mContext);
                dialogCustomLayout.onCreate(mContext.getString(R.string.dialog_title_category_update), category.getName());
                dialogCustomLayout.setListener(new DialogCustomLayout.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, String text) {
                        if(mCategoryService.checkBeforeUpdate(text)){
                            category.setName(text);
                            mCategoryService.update(category);
                            dialog.dismiss();
                            notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(mContext, mContext.getString(R.string.toast_duplicate_name), Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public void onViewMoved(int fromPosition, int toPosition) {
        Log.v("", "Log move position: " + fromPosition + " to " + toPosition);
        if (fromPosition < mData.size() && toPosition < mData.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mData, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mData, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            for (int i = 0; i < mData.size() - 1; i++) {
                Category category = mData.get(i);
                category.setOrderView(i);
                mCategoryService.update(category);
            }
        }

    }

    @Override
    public void onViewSwiped(int position) {

    }


    public void setTouchHelper(ItemTouchHelper mItemTouchHelper) {
        this.touchHelper = mItemTouchHelper;
    }
}
