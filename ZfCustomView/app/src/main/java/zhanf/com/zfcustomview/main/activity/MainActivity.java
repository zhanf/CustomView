package zhanf.com.zfcustomview.main.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import zhanf.com.zfcustomview.R;
import zhanf.com.zfcustomview.main.adapter.MainAdapter;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    private MainAdapter adapter;
    private ArrayList<String> activityList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        activityList.add("zhanf.com.zfcustomview.main.activity.SelectorTvActivity");
        activityList.add("zhanf.com.zfcustomview.main.activity.MediaPlayerActivity");

        adapter = new MainAdapter(this);
        adapter.setActivityList(activityList);
        rvMain.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvMain.setHasFixedSize(true);
        adapter.setHasStableIds(true);
        rvMain.setAdapter(adapter);

    }
}
