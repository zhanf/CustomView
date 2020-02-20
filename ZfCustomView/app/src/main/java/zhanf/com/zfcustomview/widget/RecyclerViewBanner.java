package zhanf.com.zfcustomview.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import zhanf.com.zfcustomview.R;

/**
 * @author zhanfeng
 * @date 2019-11-07
 * @desc 轮播图
 */
public class RecyclerViewBanner extends FrameLayout {

    private static final int DEFAULT_SELECTED_COLOR = 0xffffffff;
    private static final int DEFAULT_UNSELECTED_COLOR = 0x50ffffff;
    private int mInterval;
    private boolean isShowIndicator;
    private Drawable mSelectedDrawable;
    private Drawable mUnselectedDrawable;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private int mSpace;

    private RecyclerView mRecyclerView;
    private LinearLayout mLinearLayout;

    private RecyclerAdapter adapter;
    private List<Object> mData = new ArrayList<>();
    private Handler handler = new Handler();
    private boolean isAutoPlaying = true;

    private Runnable playTask = new Runnable() {

        @Override
        public void run() {
            handler.removeCallbacksAndMessages(null);
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            if (null != layoutManager) {
                int firstVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                mRecyclerView.smoothScrollToPosition(firstVisibleItemPosition + 1);
                handler.postDelayed(this, mInterval);
            }
        }
    };

    public RecyclerViewBanner(Context context) {
        this(context, null);
    }

    public RecyclerViewBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.style_recycler_banner);
        mInterval = a.getInt(R.styleable.style_recycler_banner_rvb_interval, 3000);
        isShowIndicator = a.getBoolean(R.styleable.style_recycler_banner_rvb_showIndicator, true);
        isAutoPlaying = a.getBoolean(R.styleable.style_recycler_banner_rvb_autoPlaying, true);
        Drawable sd = a.getDrawable(R.styleable.style_recycler_banner_rvb_indicatorSelectedSrc);
        Drawable usd = a.getDrawable(R.styleable.style_recycler_banner_rvb_indicatorUnselectedSrc);
        if (sd == null) {
            mSelectedDrawable = generateDefaultDrawable(DEFAULT_SELECTED_COLOR);
        } else {
            if (sd instanceof ColorDrawable) {
                mSelectedDrawable = generateDefaultDrawable(((ColorDrawable) sd).getColor());
            } else {
                mSelectedDrawable = sd;
            }
        }
        if (usd == null) {
            mUnselectedDrawable = generateDefaultDrawable(DEFAULT_UNSELECTED_COLOR);
        } else {
            if (usd instanceof ColorDrawable) {
                mUnselectedDrawable = generateDefaultDrawable(((ColorDrawable) usd).getColor());
            } else {
                mUnselectedDrawable = usd;
            }
        }
        mIndicatorWidth = a.getDimensionPixelSize(R.styleable.style_recycler_banner_rvb_indicatorWidth, 0);
        mIndicatorHeight = a.getDimensionPixelSize(R.styleable.style_recycler_banner_rvb_indicatorHeight, 0);
        mSpace = a.getDimensionPixelSize(R.styleable.style_recycler_banner_rvb_indicatorSpace, dp2px(4));
        int margin = a.getDimensionPixelSize(R.styleable.style_recycler_banner_rvb_indicatorMargin, dp2px(8));
        int g = a.getInt(R.styleable.style_recycler_banner_rvb_indicatorGravity, 1);
        int gravity;
        if (g == 0) {
            gravity = GravityCompat.START;
        } else if (g == 2) {
            gravity = GravityCompat.END;
        } else {
            gravity = Gravity.CENTER;
        }
        a.recycle();

        initRecyclerView();
        initIndicator(margin, gravity);
    }

    private void initIndicator(int margin, int gravity) {
        mLinearLayout = new LinearLayout(getContext());
        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mLinearLayout.setGravity(Gravity.CENTER);

        LayoutParams linearLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.gravity = Gravity.BOTTOM | gravity;
        linearLayoutParams.setMargins(margin, margin, margin, margin);
        addView(mLinearLayout, linearLayoutParams);

        // 便于在xml中编辑时观察，运行时不执行
        if (isInEditMode()) {
            for (int i = 0; i < 3; i++) {
                mData.add("");
            }
            createIndicators();
        }
    }

    /**
     * 默认指示器是一系列直径为6dp的小圆点
     */
    private GradientDrawable generateDefaultDrawable(int color) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setSize(dp2px(6), dp2px(6));
        gradientDrawable.setCornerRadius(dp2px(6));
        gradientDrawable.setColor(color);
        return gradientDrawable;
    }

    /**
     * 设置是否显示指示器导航点
     *
     * @param show 显示
     */
    public void isShowIndicator(boolean show) {
        this.isShowIndicator = show;
    }

    /**
     * 设置轮播间隔时间
     *
     * @param millisecond 时间毫秒
     */
    public void setIndicatorInterval(int millisecond) {
        this.mInterval = millisecond;
    }

    /**
     * 设置是否自动播放（上锁）
     *
     * @param playing 开始播放
     */
    private synchronized void setPlaying(boolean playing) {
        if (isAutoPlaying) {
            handler.removeCallbacksAndMessages(null);
            if (playing && adapter != null && adapter.getItemCount() > 1) {
                handler.postDelayed(playTask, mInterval);
            }
        }
    }

    /**
     * 设置是否禁止滚动播放
     *
     * @param isAutoPlaying true  是自动滚动播放,false 是禁止自动滚动
     */
    public void setRvAutoPlaying(boolean isAutoPlaying) {
        this.isAutoPlaying = isAutoPlaying;
    }

    /**
     * 设置轮播数据集
     *
     * @param data Banner对象列表
     */
    public void setRvBannerData(List data) {
        setPlaying(false);
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        if (mData.size() > 1) {
            adapter.notifyDataSetChanged();
            int currentIndex = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % mData.size();
            // 将起始点设为最靠近的 MAX_VALUE/2 的，且为mData.size()整数倍的位置
            mRecyclerView.scrollToPosition(currentIndex);
            setPlaying(true);
        } else {
            adapter.notifyDataSetChanged();
        }
        if (isShowIndicator) {
            createIndicators();
        }
    }

    /**
     * 指示器整体由数据列表容量数量的AppCompatImageView均匀分布在一个横向的LinearLayout中构成
     * 使用AppCompatImageView的好处是在Fragment中也使用Compat相关属性
     */
    private void createIndicators() {
        if (mData.size() <= 0) {
            mLinearLayout.setVisibility(GONE);
            return;
        }
        mLinearLayout.removeAllViews();
        int width = Math.max(mIndicatorWidth, dp2px(4));
        int height = Math.max(mIndicatorHeight, dp2px(4));
        for (int i = 0; i < mData.size(); i++) {
            CardView cvIndicator = new CardView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = mSpace / 2;
            lp.rightMargin = mSpace / 2;
            // 设置了indicatorSize属性
            if (i == 0) {
                lp.width = width;
                lp.height = height;
            } else {
                lp.width = lp.height = height;
            }
            cvIndicator.setCardElevation(0);
            cvIndicator.setRadius(height);
            cvIndicator.setBackground(i == 0 ? mSelectedDrawable : mUnselectedDrawable);
            mLinearLayout.addView(cvIndicator, lp);
        }
    }

    /**
     * 改变导航的指示点
     */
    private void switchIndicator(int currentIndex) {
        if (mLinearLayout != null && mLinearLayout.getChildCount() > 0) {
            for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
                CardView indicatorView = (CardView) mLinearLayout.getChildAt(i);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) indicatorView.getLayoutParams();
                if (i == currentIndex % mData.size()) {
                    layoutParams.width = Math.max(mIndicatorWidth, dp2px(4));
                    indicatorView.setBackground(mSelectedDrawable);
                } else {
                    layoutParams.width = layoutParams.height = Math.max(mIndicatorHeight, dp2px(4));
                    indicatorView.setBackground(mUnselectedDrawable);
                }
                indicatorView.setLayoutParams(layoutParams);
            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        setPlaying(false);
        if (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP) {
            setPlaying(true);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setPlaying(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setPlaying(false);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == GONE || visibility == INVISIBLE) {
            // 停止轮播
            setPlaying(false);
        } else if (visibility == VISIBLE) {
            // 开始轮播
            setPlaying(true);
        }
        super.onWindowVisibilityChanged(visibility);
    }

    /**
     * RecyclerView适配器
     */
    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            if (null != innerAdapter) {
                return innerAdapter.onCreateViewHolder(parent, viewType);
            } else {
                throw new IllegalStateException("innerAdapter must not be null in RecyclerViewBanner");
            }
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
            int finalPos = position % mData.size();
            if (null != innerAdapter) {
                innerAdapter.onBindViewHolder(holder, finalPos);
            }
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size() < 2 ? mData.size() : Integer.MAX_VALUE;
        }
    }

    private class PagerSnapHelper extends LinearSnapHelper {

        @Override
        public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
            int targetPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
            final View currentView = findSnapView(layoutManager);
            if (targetPos != RecyclerView.NO_POSITION && currentView != null) {
                int currentPos = layoutManager.getPosition(currentView);
                int first = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                int last = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                currentPos = targetPos < currentPos ? last : (targetPos > currentPos ? first : currentPos);
                targetPos = targetPos < currentPos ? currentPos - 1 : (targetPos > currentPos ? currentPos + 1 : currentPos);
            }
            return targetPos;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 获取RecyclerView实例，便于满足自定义{@link RecyclerView.ItemAnimator}或者{@link RecyclerView.Adapter}的需求
     *
     * @return RecyclerView实例
     */
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private InnerAdapter innerAdapter;

    public void setAdapter(InnerAdapter innerAdapter) {
        this.innerAdapter = innerAdapter;
    }

    public void initRecyclerView() {
        if (mRecyclerView != null && this.equals(mRecyclerView.getParent())) {
            this.removeView(mRecyclerView);
            mRecyclerView = null;
        }

        mRecyclerView = new RecyclerView(getContext());
        new PagerSnapHelper().attachToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new RecyclerAdapter();
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (null != layoutManager && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int currentIndex = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
                    if (isShowIndicator && currentIndex != RecyclerView.NO_POSITION) {
                        setPlaying(true);
                        switchIndicator(currentIndex);
                    }
                }
            }
        });
        LayoutParams vpLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mRecyclerView, vpLayoutParams);
    }

    public void release() {
        handler.removeCallbacksAndMessages(null);
        mData.clear();
    }

    public @Nullable
    RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public interface InnerAdapter<VH extends RecyclerView.ViewHolder> {

        VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

        void onBindViewHolder(@NotNull VH holder, int position);

    }

}
