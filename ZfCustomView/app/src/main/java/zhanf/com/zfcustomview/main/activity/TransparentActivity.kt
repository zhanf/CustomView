package zhanf.com.zfcustomview.main.activity

import android.os.Bundle
import android.view.ViewTreeObserver
import kotlinx.android.synthetic.main.activity_transparent.*
import zhanf.com.zfcustomview.R

class TransparentActivity : AbstractActivity(), ViewTreeObserver.OnGlobalLayoutListener {

    private var tvHeight: Int = 0
    private var tvTop: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transparent)

        initScrollListen()
        sw_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {

            } else {

            }
        }

        cb_check_box!!.setOnCheckedChangeListener { buttonView, isChecked -> }
    }

    private fun initScrollListen() {
        tv_scroll!!.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        gs_scroll!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
        tvHeight = tv_scroll!!.height
        tvTop = tv_scroll!!.x.toInt()
        //        09-27 23:25:21.587 5504-5504/zhanf.com.zfcustomview I/System.out: ===tv_top:0===y:4===oldY:0
        //        09-27 23:25:21.605 5504-5504/zhanf.com.zfcustomview I/System.out: ===tv_top:0===y:29===oldY:4
        //        09-27 23:25:21.622 5504-5504/zhanf.com.zfcustomview I/System.out: ===tv_top:0===y:32===oldY:29
        gs_scroll!!.setScrollViewListener { scrollView, x, y, oldX, oldY ->
            println("===tv_top:$tvTop===y:$y===oldY:$oldY")
            if (y > tvHeight) {
                tv_bac!!.setBackgroundColor(resources.getColor(R.color.colorAccent))
            } else {
                tv_bac!!.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark))
            }
        }

    }
}
