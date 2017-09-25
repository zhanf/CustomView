package zhanf.com.zfcustomview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * VerticalProgress
 */

public class VerticalProgressBar extends View {

    private Paint paint;
    private int progress = 30;
    private int constantProgress = 80;
    private int width;
    private int height;
    private Path path;

    public VerticalProgressBar(Context context) {
        this(context, null);
    }

    public VerticalProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        path = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
        width = getMeasuredWidth() + getPaddingLeft() + getPaddingRight() + params.leftMargin + params.rightMargin;
        height = getMeasuredHeight() + getPaddingTop() + getPaddingBottom() + params.bottomMargin + params.topMargin;
        //精确模式设置测量得到的尺寸，否则去设置子ViewGroup的宽高测量总和
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width, heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.rgb(55, 200, 255));// 设置画笔颜色
        canvas.drawRect(0, height - progress / 100f * height, width, height, paint);// 画矩形

        paint.setColor(Color.RED);
        path.moveTo(0,height-constantProgress / 100f * height);
        path.lineTo(width,height-constantProgress / 100f * height);
        path.lineTo(width,height-(constantProgress-1) / 100f * height);
        path.lineTo(0,height-(constantProgress-1) / 100f * height);
        path.close();
        canvas.drawPath(path,paint);
        super.onDraw(canvas);
    }

    /**
     * 拿到文字宽度
     * @param str 传进来的字符串
     * return 宽度
     */
    private int getTextWidth(String str) {
        // 计算文字所在矩形，可以得到宽高
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.width();
    }


    /** 设置progressbar进度 */
    public void setProgress(int progress) {
        this.progress = progress;
        postInvalidate();
    }
}
