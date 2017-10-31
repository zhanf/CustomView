package zhanf.com.zfcustomview.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 仿ios 弹性滚动的NestedScrollView
 * Created by zhanf on 2017/10/30.
 */

public class FlexibleScrollView extends NestedScrollView {

    private ObjectAnimator translationY;//属性动画
    private int oldTopY;//顶部位置下拉，记录下拉的坐标
    private int oldBottomY;//底部位置上拉，记录上拉之前的坐标

    public FlexibleScrollView(Context context) {
        this(context, null);
    }

    public FlexibleScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlexibleScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (getScrollY() <= 0) {//到达最顶部,下拉
                    if (oldTopY == 0) {//记录移动之前位置
                        oldTopY = (int) ev.getY();
                    }
                    if (ev.getY() > oldTopY) {//两次位置进行对比，上拉状态
                        setTranslationY((ev.getY() - oldTopY) / 2);//设置移动距离
                        return true;
                    } else {
                        if (null != translationY && translationY.isRunning()) {//两次位置进行对比，下拉状态
                            translationY.cancel();
                        }
                    }

                }

                if (getChildAt(0).getMeasuredHeight() <= getScrollY() + getHeight()) {//到达最底部，上拉
                    if (oldBottomY == 0) {//记录移动之前位置
                        oldBottomY = (int) ev.getY();
                    }
                    if (ev.getY() - oldBottomY < 0) {//两次位置进行对比，上拉状态
                        setTranslationY((ev.getY() - oldBottomY) / 2);
                        return true;
                    } else {
                        if (null != translationY && translationY.isRunning()) {//两次位置进行对比，下拉状态
                            translationY.cancel();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (getTranslationY() != 0) {
                    translationY = ObjectAnimator.ofFloat(this, "translationY", 0);
                    translationY.setDuration(300);
                    translationY.start();
                }
                oldBottomY = 0;
                oldTopY = 0;
                break;
        }
        return super.onTouchEvent(ev);
    }

}
