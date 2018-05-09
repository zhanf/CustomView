package zhanf.com.zfcustomview.main.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import zhanf.com.zfcustomview.R;
import zhanf.com.zfcustomview.main.adapter.MainAdapter;

public class MainActivity extends SimpleActivity {

    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    private MainAdapter adapter;
    private ArrayList<Class<? extends AppCompatActivity>> activityList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        activityList.add(new SelectorTvActivity().getClass());
        activityList.add(new MediaPlayerActivity().getClass());
        activityList.add(new TransparentActivity().getClass());
        activityList.add(new HistogramActivity().getClass());

        adapter = new MainAdapter(this);
        adapter.setActivityList(activityList);
        rvMain.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvMain.setHasFixedSize(true);
        adapter.setHasStableIds(true);
        rvMain.setAdapter(adapter);

    }
}
