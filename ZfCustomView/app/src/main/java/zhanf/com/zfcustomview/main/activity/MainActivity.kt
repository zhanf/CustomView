package zhanf.com.zfcustomview.main.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import zhanf.com.zfcustomview.R
import zhanf.com.zfcustomview.main.adapter.MainAdapter
import java.util.*

class MainActivity : AbstractActivity() {

    private val adapter by lazy { MainAdapter(this) }
    private val activityList = ArrayList<Class<out AppCompatActivity>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        activityList.add(SelectorTvActivity::class.java)
        activityList.add(MediaPlayerActivity::class.java)
        activityList.add(TransparentActivity::class.java)
        activityList.add(HistogramActivity::class.java)

        adapter.setActivityList(activityList)
        rvMain.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMain.setHasFixedSize(true)
//        adapter.setHasStableIds(true)
        rvMain.adapter = adapter

    }
}
