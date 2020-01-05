package zhanf.com.zfcustomview.main.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast

import zhanf.com.zfcustomview.R

/**
 * @author sincerity
 */
class SelectorTvActivity : AbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)

        findViewById<View>(R.id.stv).setOnClickListener { Toast.makeText(this@SelectorTvActivity, "onClick", Toast.LENGTH_SHORT).show() }
    }
}
