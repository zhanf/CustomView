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
import android.view.Surface;
import android.view.TextureView;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zhanf.com.zfcustomview.R;
import zhanf.com.zfcustomview.main.service.IService;
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

    private Surface surfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);
        initTextureSize();
        initTextureListener();
    }


    private void initTextureListener() {

        tvMediaPlayer.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                surfaceView = new Surface(surface);
                if (null == conn) {
                    conn = new MediaServiceConnection(MediaPlayerActivity.this);
                    Intent intent = new Intent(MediaPlayerActivity.this, MediaPlayerService.class);
                    bindService(intent, conn, Context.BIND_AUTO_CREATE);
                } else {
                    conn.setSurface(surfaceView);
                    conn.start();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                System.out.println("onSurfaceTextureSizeChanged");
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

                conn.pause();
                System.out.println("onSurfaceTextureDestroyed");
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                System.out.println("onSurfaceTextureUpdated");
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

    public class MediaServiceConnection implements ServiceConnection, IService {

        private WeakReference<MediaPlayerActivity> reference;
        private MediaPlayerService.PlayerBinder binder;

        private MediaServiceConnection(MediaPlayerActivity activity) {

            reference = new WeakReference<>(activity);

        }

        @Override
        public void start() {
            binder.start();
        }

        @Override
        public void pause() {
            binder.pause();
        }

        @Override
        public void stop() {
            binder.stop();
        }

        @Override
        public void setSurface(Surface surface) {
            binder.setSurface(surface);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerActivity activity = reference.get();
            if (activity != null && !activity.isFinishing()) {
                if (binder == null) {
                    binder = (MediaPlayerService.PlayerBinder) service;
                    binder.init("", activity.surfaceView);
                }
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
