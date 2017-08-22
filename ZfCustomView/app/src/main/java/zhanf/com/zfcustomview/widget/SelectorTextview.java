package zhanf.com.zfcustomview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import zhanf.com.zfcustomview.R;

/**
 * 按压可改变背景色的TextView
 * 支持：solid stroke
 * Created by ZhanFeng on 2017/8/18.
 * 使用：1，注册onClickListener事件 或 xml 中设置 android:clickable="true"
 * 2，设置 app:isChangeStatus="true"
 * 3，设置相应属性，描述如下
 */

public class SelectorTextview extends AppCompatTextView {

    private boolean isSelected;//是否触摸状态
    private int colorSelector;//pressed 下的 背景色
    private int colorNormal;
    private int cornersRadius;//矩形圆角半径
    private boolean isChangeStatus;//是否改变状态下的颜色
    private int rectLeftTop;//圆角 左上
    private int rectRightTop;//圆角 右上
    private int rectLeftBottom;//圆角 左下
    private int rectRightBottom;//圆角 右下
    private int strokeColorSelector;//pressed下的描边颜色
    private int strokeColorNormal;
    private int strokeDashGap;
    private int strokeDashWidth;
    private int strokeWidth;//描边宽度
    private GradientDrawable gradientDrawable;


    public SelectorTextview(Context context) {
        this(context, null);
    }

    public SelectorTextview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectorTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initStyleAttr(attrs);

        initGradientDrawable();

    }

    private void initStyleAttr(AttributeSet attrs) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.style_selector_tv);

        isChangeStatus = typedArray.getBoolean(R.styleable.style_selector_tv_isChangeStatus, false);

        colorNormal = typedArray.getColor(R.styleable.style_selector_tv_colorNormal, 0XFFFFAAAA);
        colorSelector = typedArray.getColor(R.styleable.style_selector_tv_colorSelector, 0XFF00FFFF);

        strokeColorSelector = typedArray.getColor(R.styleable.style_selector_tv_strokeColorSelector, 0XFFFF44AA);
        strokeColorNormal = typedArray.getColor(R.styleable.style_selector_tv_strokeColorNormal, 0XFFFF0000);
        strokeDashGap = typedArray.getDimensionPixelSize(R.styleable.style_selector_tv_strokeDashGap, 0);
        strokeDashWidth = typedArray.getDimensionPixelSize(R.styleable.style_selector_tv_strokeDashWidth, 0);
        strokeWidth = typedArray.getDimensionPixelSize(R.styleable.style_selector_tv_strokeWidth, 0);

        cornersRadius = typedArray.getDimensionPixelSize(R.styleable.style_selector_tv_cornersRadius, 0);
        rectLeftTop = typedArray.getDimensionPixelSize(R.styleable.style_selector_tv_rectLeftTop, 0);
        rectRightTop = typedArray.getDimensionPixelSize(R.styleable.style_selector_tv_rectRightTop, 0);
        rectLeftBottom = typedArray.getDimensionPixelSize(R.styleable.style_selector_tv_rectLeftBottom, 0);
        rectRightBottom = typedArray.getDimensionPixelSize(R.styleable.style_selector_tv_rectRightBottom, 0);

        typedArray.recycle();
    }


    private void initGradientDrawable() {
        gradientDrawable = new GradientDrawable();        //创建drawable
        setRadius();
//        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);//设置渐变类型

    }

    /**
     * 只有类型是矩形的时候设置圆角半径才有效
     */
    private void setRadius() {
        if (0 != cornersRadius) {
            gradientDrawable.setCornerRadius(cornersRadius);//设置圆角的半径
        } else {
            //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
            gradientDrawable.setCornerRadii(new float[]{
                            rectLeftTop, rectLeftTop,
                            rectRightTop, rectRightTop,
                            rectLeftBottom, rectLeftBottom,
                            rectRightBottom, rectRightBottom
                    }
            );
        }

    }

    /**
     * 设置边框  宽度  颜色  虚线  间隙
     *
     * @param strokeWidth     宽度
     * @param strokeColor     颜色
     * @param strokeDashWidth 虚线
     * @param strokeDashGap   间隙
     */
    private void setBorder(int strokeWidth, int strokeColor, int strokeDashWidth, int strokeDashGap) {
        if (0 != strokeWidth) {
            gradientDrawable.setStroke(strokeWidth, strokeColor, strokeDashWidth, strokeDashGap);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (isClickable()) {

            if (isChangeStatus && isSelected) {
                gradientDrawable.setColor(colorSelector);
                setBorder(strokeWidth, strokeColorSelector, strokeDashWidth, strokeDashGap);
                setBackgroundDrawable(gradientDrawable);

            }

            if (isChangeStatus && !isSelected) {
                gradientDrawable.setColor(colorNormal);
                setBorder(strokeWidth, strokeColorNormal, strokeDashWidth, strokeDashGap);
                setBackgroundDrawable(gradientDrawable);
            }

        }

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isSelected = true;
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isSelected = false;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

}
