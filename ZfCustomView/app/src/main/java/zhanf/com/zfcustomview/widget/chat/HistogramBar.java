package zhanf.com.zfcustomview.widget.chat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

import zhanf.com.zfcustomview.R;
import zhanf.com.zfcustomview.utils.ToastUtil;

/**
 * 柱状图
 * Created by zhanf on 2018/5/8.
 */

public class HistogramBar extends View {

    public static final String TAG = "HistogramBar";

    private Paint mPaintText;
    private Paint mPaintRect;//矩形柱状图画笔
    private RectF rectF;//矩形柱状图路劲
    private Path mPath;//虚线刻度的路径
    private int rectWidth = ConvertUtils.dp2px(16);//矩形的宽度
    private List<String> categoryTextList = new ArrayList<String>() {{
        add("短元音");
        add("长元音");
        add("双元音");
        add("爆破音");
        add("摩擦音");
        add("鼻音");
        add("其他");
    }};
    private List<Float> scoreTextList = new ArrayList<Float>() {{
        add(100.f);
        add(82.f);
        add(78.f);
        add(38.f);
        add(40.f);
        add(43.f);
        add(61.f);
    }};
    private int colorCategory;//分类文字颜色
    private int colorScore;//分数文字颜色
    private int colorHistogram;//柱状图文字颜色
    private int textSizeCategory;//分类文字大小
    private int textSizeScore;//分数文字大小
    private int histogramRadius;//矩形柱状图圆角
    private float[] currentHistogramProgress = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};//当前进度，用于实现矩形动画
    private int scaleTextHeight;//纵坐标类型文字的高度
    private int lastRowLocationY;//最后一行刻度 Y 坐标
    private int selectHistogram = -1;//选中矩形的柱状图
    private boolean isTouchRect;//是否选中 柱状图

    public HistogramBar(Context context) {
        this(context, null);
    }

    public HistogramBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistogramBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyleAttr(attrs);
        initPaint();
    }

    private void initPaint() {
        mPath = new Path();
        rectF = new RectF();

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setPathEffect(new DashPathEffect(new float[]{ConvertUtils.dp2px(2), ConvertUtils.dp2px(2)}, 20));
        mPaintText.setStyle(Paint.Style.STROKE);

        mPaintRect = new Paint();
        mPaintRect.setAntiAlias(true);
        mPaintRect.setColor(colorHistogram);
    }

    private void initStyleAttr(AttributeSet attrs) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.style_histogram_bar);

        colorCategory = typedArray.getColor(R.styleable.style_histogram_bar_colorCategory, 0XFF666666);
        colorScore = typedArray.getColor(R.styleable.style_histogram_bar_colorScore, 0XFF999999);
        colorHistogram = typedArray.getColor(R.styleable.style_histogram_bar_colorHistogram, 0XFF338FFF);
        textSizeCategory = typedArray.getDimensionPixelSize(R.styleable.style_histogram_bar_textSizeCategory, ConvertUtils.dp2px(10));
        textSizeScore = typedArray.getDimensionPixelSize(R.styleable.style_histogram_bar_textSizeScore, ConvertUtils.dp2px(8));
        histogramRadius = typedArray.getDimensionPixelSize(R.styleable.style_histogram_bar_histogramRadius, ConvertUtils.dp2px(2));

        typedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int mWidth = getWidth();

        //1. 先画分数类型的文字
        mPaintText.setTextSize(textSizeCategory);
        //纵坐标类型文字的高度
        scaleTextHeight = getTextHeight(mPaintText) + ConvertUtils.dp2px(10) + getPaddingTop();

        mPaintText.setFakeBoldText(true);
        mPaintText.setColor(colorCategory);
        canvas.drawText("平均分数", getPaddingLeft(), getTextHeight(mPaintText) + getPaddingTop(), mPaintText);

        //2. 画刻度分割线和分割线文字
        int histogramHeight = getHeight() - getCategoryTextHeight() - scaleTextHeight - getPaddingBottom();//柱状图总高度
        Log.d(TAG, "柱状图总高度：" + histogramHeight);

        int scaleText = 100 / 5;

        mPaintText.setFakeBoldText(false);
        mPaintText.setTextSize(textSizeScore);
        mPaintText.setColor(colorScore);

        //2.1 从上到下画刻度线和文字
        for (int i = 0; i < 5; i++) {
            float startY = (float) (scaleTextHeight + (histogramHeight * i / 5));
            Log.d(TAG, "LineY - " + i + "= " + startY);
            mPath.moveTo(getPaddingLeft() + getTextWidth("100"), (startY + 0.5f));
            mPath.lineTo(mWidth - getPaddingRight(), (startY + 0.5f));
            canvas.drawPath(mPath, mPaintText);
            canvas.drawText(String.valueOf(100 - scaleText * i), getPaddingLeft() + getTextWidth("100") - getTextWidth(String.valueOf(100 - scaleText * i)), startY - (getTextAscent() / 2), mPaintText);
        }
        //2.2 画最底下一行（基准为0）的刻度线和文字
        //最后一行刻度 Y 坐标
        lastRowLocationY = scaleTextHeight + histogramHeight;
        mPath.moveTo(getPaddingLeft() + getTextWidth("100"), (lastRowLocationY + 0.5f));
        mPath.lineTo(mWidth - getPaddingRight(), (lastRowLocationY + 0.5f));
        canvas.drawPath(mPath, mPaintText);
        canvas.drawText("0", getPaddingLeft() + getTextWidth("100") - getTextWidth("0"), lastRowLocationY - (getTextAscent() / 2), mPaintText);

        try {
            //3. 画柱状图和底部分类的文字
            mPaintText.setColor(colorCategory);
            mPaintText.setTextSize(textSizeCategory);

            float categoryTextLocationX = getFirstRectCenterLineX();//确定左边第一个柱状图的中心基准线 X 坐标
            float categoryWidth = getCategoryWidth(getHistogramWidth());//柱状图每个刻度宽度
            for (int i = 0; i < categoryTextList.size(); i++) {
                String category = categoryTextList.get(i);
                int scaleTextWidth = getTextWidth(category);
                canvas.drawText(category, categoryTextLocationX + categoryWidth * i - scaleTextWidth / 2, lastRowLocationY + getTextHeight(mPaintText), mPaintText);
                float score = scoreTextList.get(i);
                if (currentHistogramProgress[i] < score - 5) {
                    currentHistogramProgress[i] += 5;
                    postInvalidateDelayed(5);
                } else {
                    currentHistogramProgress[i] = score;
                }
                Log.d(TAG, "HistogramTop - " + i + "= " + (float) (scaleTextHeight + ((100 - score) * (lastRowLocationY - scaleTextHeight)) / 100));
                if (i < categoryTextList.size() / 2) {//打印日志用于查看柱状图左右是否对称
                    Log.d(TAG, "onDrawLeft: " + (categoryTextLocationX + (categoryWidth * i) - rectWidth / 2 - getTextWidth("100")));
                } else {
                    Log.d(TAG, "onDrawRight: " + (getWidth() - (categoryTextLocationX + (categoryWidth * i) + rectWidth / 2)));
                }
                rectF.set(categoryTextLocationX + (categoryWidth * i) - rectWidth / 2, (float) (scaleTextHeight + ((100 - currentHistogramProgress[i]) * (lastRowLocationY - scaleTextHeight)) / 100),
                        categoryTextLocationX + (categoryWidth * i) + rectWidth / 2, lastRowLocationY + 1);
                canvas.drawRoundRect(rectF, histogramRadius, histogramRadius, mPaintRect);
            }
        } catch (Exception e) {
            Log.d(TAG, "绘制柱状图失败，错误信息：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 柱状图每个刻度宽度
     *
     * @param histogramWidth 柱状图宽度总宽度
     * @return
     */
    private float getCategoryWidth(float histogramWidth) {
        return (histogramWidth - rectWidth - ConvertUtils.dp2px(20) * 2) / (categoryTextList.size() - 1);
    }

    /**
     * 柱状图宽度总宽度
     *
     * @return
     */
    private int getHistogramWidth() {
        return getWidth() - getTextWidth("100") - getPaddingRight() - getPaddingLeft();
    }

    private int getFirstRectCenterLineX() {
        return getPaddingLeft() + getTextWidth("100") + ConvertUtils.dp2px(20) + rectWidth / 2;
    }

    float downX;
    float downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                isTouchRect = isTouchRect(downX, downY);
                if (!isTouchRect) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isTouchRect && -1 != selectHistogram) {
                    ToastUtil.showShort("当前选中：" + selectHistogram);
                }
                break;
        }
        return true;
    }

    /**
     * 是否选中 柱状图
     *
     * @param downX
     * @param downY
     * @return
     */
    private boolean isTouchRect(float downX, float downY) {
        float categoryTextLocationX = getFirstRectCenterLineX();//确定左边第一个柱状图的中心基准线 X 坐标
        float categoryWidth = getCategoryWidth(getHistogramWidth());//柱状图每个刻度宽度
        for (int i = 0; i < categoryTextList.size(); i++) {
            float score = scoreTextList.get(i);
            if (downX > categoryTextLocationX + (categoryWidth * i) - rectWidth / 2 &&
                    downX < categoryTextLocationX + (categoryWidth * i) + rectWidth / 2 &&
                    downY > (scaleTextHeight + ((100 - score) * (lastRowLocationY - scaleTextHeight)) / 100) &&
                    downY < lastRowLocationY) {
                selectHistogram = i;
                return true;
            }
        }
        return false;
    }

    /**
     * 获取底部的分类文字的高度
     *
     * @return
     */
    public int getCategoryTextHeight() {
        mPaintText.setTextSize(textSizeCategory);
        return getTextHeight(mPaintText) + ConvertUtils.dp2px(2);
    }

    /**
     * 获取文字高度的中心相对其基准线的偏离位置
     *
     * @param paint
     * @return
     */
    private int getTextHeight(Paint paint) {
        Paint.FontMetricsInt metrics = paint.getFontMetricsInt();
        return metrics.bottom - metrics.top;
    }

    /**
     * 获取显示的文字中心线高度坐标
     *
     * @return
     */
    private int getTextAscent() {
        Paint.FontMetricsInt metrics = mPaintText.getFontMetricsInt();
        return metrics.descent + metrics.ascent;
    }

    /**
     * 获取文字的宽度
     *
     * @param text
     * @return
     */
    private int getTextWidth(String text) {
        int textWidth = (int) mPaintText.measureText(text);
        textWidth += ConvertUtils.dp2px(2);
        return textWidth;
    }

    public void setCategoryTextList(List<String> categoryTextList) {
        if (null != categoryTextList && !categoryTextList.isEmpty()) {
            this.categoryTextList = categoryTextList;
            postInvalidate();
        }
    }

    public void setScoreTextList(List<Float> scoreTextList) {
        if (null != scoreTextList && !scoreTextList.isEmpty()) {
            this.scoreTextList = scoreTextList;
            currentHistogramProgress = new float[scoreTextList.size()];
            for (int i = 0; i < scoreTextList.size(); i++) {
                currentHistogramProgress[i] = 0.0f;
            }
            postInvalidate();
        }
    }
}
