package zhanf.com.zfcustomview.main.activity;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.TextureView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zhanf.com.zfcustomview.R;
import zhanf.com.zfcustomview.mediaplayer.MediaPlayerManager;
import zhanf.com.zfcustomview.widget.SelectorTextview;

public class MediaPlayerActivity extends AppCompatActivity {

    @BindView(R.id.tv_media_player)
    TextureView tvMediaPlayer;
    @BindView(R.id.stv_start)
    SelectorTextview stvStart;
    @BindView(R.id.stv_next)
    SelectorTextview stvPause;
    @BindView(R.id.stv_stop)
    SelectorTextview stvStop;

    private Surface surfaceView;

    private MediaPlayerManager mediaPlayerManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);
        initTextureSize();
        initTextureListener();
    }

    private void initTextureSize() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvMediaPlayer.getLayoutParams();
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        layoutParams.width = width;
        layoutParams.height = width * 9 / 16;
        tvMediaPlayer.setLayoutParams(layoutParams);
    }

    private void initTextureListener() {

        tvMediaPlayer.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                surfaceView = new Surface(surface);
                if (null == mediaPlayerManager) {
                    mediaPlayerManager = new MediaPlayerManager("", surfaceView);
                } else {
                    mediaPlayerManager.setSurface(surfaceView);
                    mediaPlayerManager.reStart();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

                mediaPlayerManager.pauseAuto();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
    }

    @OnClick(R.id.stv_start)
    public void onStvStartClicked() {
        stvStart.setText(TextUtils.equals("暂停", stvStart.getText().toString().trim()) ? "播放" : "暂停");
        mediaPlayerManager.play();
    }

    @OnClick(R.id.stv_next)
    public void onStvPauseClicked() {
        mediaPlayerManager.changePlay("");
    }


    @Override
    protected void onDestroy() {
        mediaPlayerManager.onDestroy();
        super.onDestroy();
    }
}
