package zhanf.com.zfcustomview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import zhanf.com.zfcustomview.R;

/**
 * Created by Administrator on 2017/8/18.
 */

public class SelectorTextview extends AppCompatTextView {

    private Paint paint;
    private int height;
    private int width;

    private boolean isSelected;
    private RectF rectF;
    private int selectorColor;
    private int normalColor;
    private int radius;


    public SelectorTextview(Context context) {
        this(context, null);
    }

    public SelectorTextview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectorTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        initStyleAttr(attrs);

        paint = new Paint();

    }

    private void initStyleAttr(AttributeSet attrs) {

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.style_selector_tv);

        normalColor = a.getColor(R.styleable.style_selector_tv_normal, 0XFFFFFFFF);
        selectorColor = a.getColor(R.styleable.style_selector_tv_selector, 0XFFFFFFFF);
        radius = a.getColor(R.styleable.style_selector_tv_radius, 0);

        if (0XFFFFFFFF != selectorColor) {
            isSelected = true;
        }

        a.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (isSelected) {
            paint.setColor(selectorColor);
            canvas.drawRoundRect(rectF, 30, 30, paint);
        } else {
            paint.setColor(normalColor);
            canvas.drawRoundRect(rectF, 30, 30, paint);
        }
        super.onDraw(canvas);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        height = getHeight();
        width = getWidth();
        rectF = new RectF(0, 0, width, height);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isSelected = true;
                System.out.println("onTouchEvent:" + event.getAction());
                invalidate();

                break;

            case MotionEvent.ACTION_MOVE:
                System.out.println("onTouchEvent:" + event.getAction());
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isSelected = false;
                System.out.println("onTouchEvent:" + event.getAction());

                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

}
