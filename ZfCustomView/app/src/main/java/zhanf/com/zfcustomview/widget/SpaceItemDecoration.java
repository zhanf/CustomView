package zhanf.com.zfcustomview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView 最后一个item 底部 添加分割线
 * Created by zhanf on 2017/9/30.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };
    public final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private Drawable mDivider;

    public SpaceItemDecoration(Context context, int orientation, Drawable dividerDrawable) {
        if (orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = dividerDrawable;
        a.recycle();
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if (isLastRaw(parent, itemPosition, parent.getLayoutManager().getItemCount())) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, 0, 0);
        }
    }

    private boolean isLastRaw(RecyclerView parent, int itemPosition, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            if (itemPosition == childCount - 1)
                return true;
        }
        return false;
    }

}
