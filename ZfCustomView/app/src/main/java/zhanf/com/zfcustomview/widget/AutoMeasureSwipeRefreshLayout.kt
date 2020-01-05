package zhanf.com.zfcustomview.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * @author zhanfeng
 * @date 2019-12-20
 * @desc 自动测量的 SwipeRefreshLayout,解决 UNSPECIFIED 下高度充满屏幕的问题
 */
class AutoMeasureSwipeRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : SwipeRefreshLayout(context, attrs) {

    private var target: View? = null

    @SuppressLint("WrongConstant")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        for (i in 0 until this.childCount) {
            val child = this.getChildAt(i)
            if (!ImageView::class.java.isAssignableFrom(child.javaClass)) {
                target = child
                break
            }
        }
        var height = 0
        target!!.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(((1 shl 30) - 1), MeasureSpec.AT_MOST))
//      target!!.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))//这种也行
        val targetHeight = target!!.measuredHeight
        if (targetHeight > height) {
            height = targetHeight
        }
        val heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightSpec)
    }
}
