package com.best.grocery.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.best.grocery.R;
import com.best.grocery.holder.HeaderListProductHolder;
import com.best.grocery.holder.ProductCheckedHeaderHolder;
import com.best.grocery.holder.ProductCheckedItemHolder;

public class SwipeAndDragShopping extends ItemTouchHelper.SimpleCallback {
    private static final String TAG = SwipeAndDragShopping.class.getSimpleName();
    private ActionCompletionContract contract;
    private int dragDirs;
    private int swipeDirs;
    private float previousDx = 0;

    private Drawable background;
    private Drawable deleteIcon;
    private Drawable hideIcon;

    private int xMarkMargin;

    private boolean initiated;
    private Context context;

    public SwipeAndDragShopping(int dragDirs, int swipeDirs, ActionCompletionContract actionCompletionContract, Context context) {
        super(dragDirs, swipeDirs);
        this.contract = actionCompletionContract;
        this.dragDirs = dragDirs;
        this.swipeDirs = swipeDirs;
        this.context = context;
    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof HeaderListProductHolder || viewHolder instanceof ProductCheckedHeaderHolder || viewHolder instanceof ProductCheckedItemHolder) {
            return 0;
        }
        return makeMovementFlags(this.dragDirs, this.swipeDirs);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //đưa xuống contract để xử lý
        contract.onViewMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //đưa xuống contract để xử lý
        contract.onViewSwiped(viewHolder.getAdapterPosition(), direction);
        previousDx = 0;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public void onChildDraw(Canvas c,
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX,
                            float dY,
                            int actionState,
                            boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        if (!initiated) {
            init();
        }
        if (previousDx <= 0 && dX > 0) {
            Log.d(TAG, "swiping from left to right");

            int itemHeight = itemView.getBottom() - itemView.getTop();
            int colorCode = ContextCompat.getColor(context, R.color.colorSwipeLeft);

            //Setting Swipe Background
            ((ColorDrawable) background).setColor(colorCode);
            background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
            background.draw(c);

            int intrinsicWidth = deleteIcon.getIntrinsicWidth();
            int intrinsicHeight = deleteIcon.getIntrinsicWidth();

            int xMarkLeft = itemView.getLeft() + xMarkMargin;
            int xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
            int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int xMarkBottom = xMarkTop + intrinsicHeight;

            //Setting Swipe Icon
            deleteIcon.setBounds(xMarkLeft, xMarkTop + 16, xMarkRight, xMarkBottom);
            deleteIcon.draw(c);
        } else if (previousDx >= 0 && dX < 0) {
            Log.d(TAG, "swiping from right to left");
            int itemHeight = itemView.getBottom() - itemView.getTop();

            //Setting Swipe Background
            ((ColorDrawable) background).setColor(context.getResources().getColor(R.color.colorSwipeRight));
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

            int intrinsicWidth = deleteIcon.getIntrinsicWidth();
            int intrinsicHeight = deleteIcon.getIntrinsicWidth();

            int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
            int xMarkRight = itemView.getRight() - xMarkMargin;
            int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int xMarkBottom = xMarkTop + intrinsicHeight;

            //Setting Swipe Icon
            hideIcon.setBounds(xMarkLeft, xMarkTop + 16, xMarkRight, xMarkBottom);
            hideIcon.draw(c);

//            //Setting Swipe Text
//            Paint paint = new Paint();
//            paint.setColor(Color.WHITE);
//            paint.setTextSize(R.dimen.swipe_text_size);
//            paint.setTextAlign(Paint.Align.CENTER);
//            String textHide = context.getString(R.string.abc_hide);
//            c.drawText(textHide, xMarkLeft + 40, xMarkTop + 10, paint);
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void init() {
        background = new ColorDrawable();
        xMarkMargin = (int) context.getResources().getDimension(R.dimen.swipe_ic_margin);
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_trash_white);
        hideIcon = context.getResources().getDrawable(R.drawable.ic_snooze_white);
        initiated = true;
    }

    public interface ActionCompletionContract {
        void onViewMoved(int oldPosition, int newPosition);

        void onViewSwiped(int position, int direction);
    }
}
