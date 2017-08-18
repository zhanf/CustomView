package zhanf.com.zfcustomview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/8/18.
 */

public class SelectorTextview extends AppCompatTextView {

    private Paint paint;
    private int height;
    private int width;

    private boolean isSelected;


    public SelectorTextview(Context context) {
        this(context, null);
    }

    public SelectorTextview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectorTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF rectF = new RectF(0, 0, width, height);
        if (isSelected) {
            paint.setColor(Color.BLUE);
            canvas.drawRoundRect(rectF, 30, 30, paint);
        } else {
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, 30, 30, paint);
        }
        super.onDraw(canvas);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        height = getHeight();
        width = getWidth();
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
