package zhanf.com.zfcustomview.main.activity;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zhanf.com.zfcustomview.R;
import zhanf.com.zfcustomview.widget.SelectorTextview;

public class MediaPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    @BindView(R.id.sfv_media_player)
    SurfaceView sfvMediaPlayer;
    @BindView(R.id.stv_start)
    SelectorTextview stvStart;
    @BindView(R.id.stv_pause)
    SelectorTextview stvPause;
    @BindView(R.id.stv_stop)
    SelectorTextview stvStop;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private AssetFileDescriptor descriptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);

        initSurfaceView();
    }

    private void initSurfaceView() {
        surfaceHolder = sfvMediaPlayer.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        descriptor = getResources().openRawResourceFd(R.raw.dream_it_possible);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(MediaPlayerActivity.this, Uri.parse("android.resource://".concat(getPackageName()).concat("/") + R.raw.dream_it_possible));
//            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());//参数里的注释是直接播放sd卡上的视频
//            mediaPlayer.setDataSource(descriptor.getFileDescriptor());//Fixme: 直接调用此方法会无法播放, why?
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @OnClick(R.id.stv_start)
    public void onStvStartClicked() {
        mediaPlayer.start();
    }

    @OnClick(R.id.stv_pause)
    public void onStvPauseClicked() {
        mediaPlayer.pause();
    }

    @OnClick(R.id.stv_stop)
    public void onStvStopClicked() {
        mediaPlayer.stop();
    }
}
