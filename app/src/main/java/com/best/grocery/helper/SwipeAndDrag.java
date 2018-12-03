package com.best.grocery.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.best.grocery.R;
import com.best.grocery.holder.CategoryItemHolder;
import com.best.grocery.holder.ProductCheckedHeaderHolder;
import com.best.grocery.holder.HeaderListProductHolder;

@SuppressWarnings("CanBeFinal")
public class SwipeAndDrag extends ItemTouchHelper.SimpleCallback {
    private ActionCompletionContract contract;
    private int dragDirs;
    private int swipeDirs;

    private Drawable background;
    private Drawable deleteIcon;

    private int xMarkMargin;

    private boolean initiated;
    private Context context;

    public SwipeAndDrag(int dragDirs, int swipeDirs, ActionCompletionContract actionCompletionContract, Context context) {
        super(dragDirs, swipeDirs);
        this.contract = actionCompletionContract;
        this.dragDirs = dragDirs;
        this.swipeDirs = swipeDirs;
        this.context = context;
    }

    public SwipeAndDrag(int dragDirs, ActionCompletionContract contract, Context context) {
        super(dragDirs, -1);
        this.dragDirs = dragDirs;
        this.contract = contract;
        this.context = context;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof HeaderListProductHolder || viewHolder instanceof ProductCheckedHeaderHolder) {
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
        if (!(viewHolder instanceof CategoryItemHolder))
            contract.onViewSwiped(viewHolder.getAdapterPosition());
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
        Log.d("AAAA","Dx: " + dX + ", dY: " + dY + ", action_state: " + actionState);

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

        //Setting Swipe Text
//        Paint paint = new Paint();
//        paint.setColor(Color.WHITE);
//        paint.setTextSize(48);
//        paint.setTextAlign(Paint.Align.CENTER);
//        c.drawText(swipeLable, xMarkLeft + 40, xMarkTop + 10, paint);
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void init() {
        background = new ColorDrawable();
        xMarkMargin = (int) context.getResources().getDimension(R.dimen.welcome_ic_clear_margin);
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_trash);
        deleteIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        initiated = true;
    }

    public interface ActionCompletionContract {
        void onViewMoved(int oldPosition, int newPosition);

        void onViewSwiped(int position);
    }
}
