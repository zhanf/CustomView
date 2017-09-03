package zhanf.com.zfcustomview.main.activity;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zhanf.com.zfcustomview.R;
import zhanf.com.zfcustomview.mediamanager.mediaplayer.MediaPlayerController;
import zhanf.com.zfcustomview.widget.SelectorTextView;

public class MediaPlayerActivity extends AppCompatActivity {

    @BindView(R.id.tv_media_player)
    TextureView tvMediaPlayer;
    @BindView(R.id.stv_start)
    SelectorTextView stvStart;
    @BindView(R.id.stv_next)
    SelectorTextView stvPause;
    @BindView(R.id.stv_stop)
    SelectorTextView stvStop;
    @BindView(R.id.sb_progress)
    SeekBar sbProgress;

    private Surface surfaceView;
    private MediaPlayerController mediaPlayerController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);
        initTextureSize();
        initListener();
    }

    private void initTextureSize() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) tvMediaPlayer.getLayoutParams();
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        layoutParams.width = width;
        layoutParams.height = width * 9 / 16;
        tvMediaPlayer.setLayoutParams(layoutParams);
    }

    private void initListener() {

        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //手指触摸seekBar时停止自己更新seekBar进度条
                mediaPlayerController.removeCallbacksAndMessages();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //将视频跳转到进度条进度处
                mediaPlayerController.seekTo(seekBar.getProgress());
                //手指停止触摸seekBar时停止更新seekBar进度条
                mediaPlayerController.sendPlayMessage();
            }
        });

        tvMediaPlayer.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                surfaceView = new Surface(surface);
                if (null == mediaPlayerController) {
                    mediaPlayerController = new MediaPlayerController("");
                    //设置MediaPlayer Prepared成功的回调，得到视频时长设置给SeekBar设置最大进度
                    mediaPlayerController.setOnPreparedListener(new MediaPlayerController.OnPreparedListen() {
                        @Override
                        public void onPreparedListener(int duration) {
                            sbProgress.setMax(duration);
                        }
                    });
                    //得到MediaPlayer当前播放进度回调并用于更新SeekBar进度
                    mediaPlayerController.setCurrentPositionCallback(new MediaPlayerController.CurrentPositionListen() {
                        @Override
                        public void getCurrentPosition(int currentPosition) {
                            System.out.println("currentPosition_MediaPlayerActivity:" + currentPosition);
                            sbProgress.setProgress(currentPosition);
                        }
                    });
                }
                //前台状态，开始播放
                mediaPlayerController.playerForeground();
                mediaPlayerController.play(surfaceView);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                //后台状态，暂停播放
                mediaPlayerController.playerBackground();
                mediaPlayerController.pause();
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
        //手动暂停/播放状态调用此方法
        mediaPlayerController.play();
    }

    @OnClick(R.id.stv_next)
    public void onStvPauseClicked() {
        mediaPlayerController.next("");
    }


    @Override
    protected void onDestroy() {
        //销毁MediaPlayer释放资源
        mediaPlayerController.destroy();
        mediaPlayerController = null;
        super.onDestroy();
    }
}
