package zhanf.com.zfcustomview.main.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import zhanf.com.zfcustomview.R;
import zhanf.com.zfcustomview.widget.ObservableScrollView;

public class TransparentActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    @BindView(R.id.sw_switch)
    Switch swSwitch;
    @BindView(R.id.cb_check_box)
    CheckBox cbCheckBox;
    @BindView(R.id.tv_scroll)
    TextView tvScroll;
    @BindView(R.id.gs_scroll)
    ObservableScrollView gsScroll;
    @BindView(R.id.tv_bac)
    TextView tvBac;
    private int tv_height;
    private int tv_top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);
        ButterKnife.bind(this);

        initScrollListen();
        swSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                } else {

                }
            }
        });

        cbCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    private void initScrollListen() {
        tvScroll.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        gsScroll.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        tv_height = tvScroll.getHeight();
        tv_top = (int) tvScroll.getX();
//        09-27 23:25:21.587 5504-5504/zhanf.com.zfcustomview I/System.out: ===tv_top:0===y:4===oldY:0
//        09-27 23:25:21.605 5504-5504/zhanf.com.zfcustomview I/System.out: ===tv_top:0===y:29===oldY:4
//        09-27 23:25:21.622 5504-5504/zhanf.com.zfcustomview I/System.out: ===tv_top:0===y:32===oldY:29
        gsScroll.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldX, int oldY) {
                System.out.println("===tv_top:" + tv_top + "===y:" + y + "===oldY:" + oldY);
                if (y > tv_height) {
                    tvBac.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                } else {
                    tvBac.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }
        });

    }
}
