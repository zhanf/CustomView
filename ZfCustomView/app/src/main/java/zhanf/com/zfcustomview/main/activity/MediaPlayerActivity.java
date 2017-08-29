package zhanf.com.zfcustomview.main.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zhanf.com.zfcustomview.R;
import zhanf.com.zfcustomview.main.service.MediaPlayerService;
import zhanf.com.zfcustomview.widget.SelectorTextview;

public class MediaPlayerActivity extends AppCompatActivity {

    @BindView(R.id.tv_media_player)
    TextureView tvMediaPlayer;
    @BindView(R.id.stv_start)
    SelectorTextview stvStart;
    @BindView(R.id.stv_pause)
    SelectorTextview stvPause;
    @BindView(R.id.stv_stop)
    SelectorTextview stvStop;
    private MediaServiceConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);
        initTextureSize();
        initTextureListener();
    }

    private SurfaceTexture surfaceTexture;

    private void initTextureListener() {
        tvMediaPlayer.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                surfaceTexture = surface;
                conn = new MediaServiceConnection(MediaPlayerActivity.this);
                bindService(new Intent(MediaPlayerActivity.this, MediaPlayerService.class), conn, Context.BIND_AUTO_CREATE);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
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

    @OnClick(R.id.stv_start)
    public void onStvStartClicked() {
        conn.start();
    }

    @OnClick(R.id.stv_pause)
    public void onStvPauseClicked() {
        conn.pause();
    }

    public  class MediaServiceConnection implements ServiceConnection {

        private WeakReference<MediaPlayerActivity> reference;
        private MediaPlayerService.PlayerBinder binder;

        private MediaServiceConnection(MediaPlayerActivity activity) {

            reference = new WeakReference<>(activity);

        }
        private void start() {
            binder.start();
        }

        private void pause(){
            binder.pause();
        }
        private void stop(){
            binder.stop();
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerActivity activity = reference.get();
            if (activity != null && !activity.isFinishing()) {
                if (binder == null) {
                    binder = (MediaPlayerService.PlayerBinder) service;
                }
                binder.init("", activity.surfaceTexture);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }
/*
    @Override
    protected void onResume() {
        super.onResume();
        if (conn != null) {
            conn.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        conn.pause();
    }*/
}
