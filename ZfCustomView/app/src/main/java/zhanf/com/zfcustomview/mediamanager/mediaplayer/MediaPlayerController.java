package zhanf.com.zfcustomview.mediamanager.mediaplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Surface;
import android.widget.TextClock;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zhanf.com.zfcustomview.R;
import zhanf.com.zfcustomview.app.application.App;
import zhanf.com.zfcustomview.mediamanager.AudioFocusHelper;

/**
 * Created by Administrator on 2017/9/1.
 */

public class MediaPlayerController implements IPlayerStateController, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private IPlayerState mState;
    private MediaPlayer mediaPlayer;
    private Context context;
    private List<String> MediaList = new ArrayList<>();
    private PlayerForeground playerForeground;
    private PlayerBackground playerBackground;
    private AssetFileDescriptor descriptor = App.getInstance().getResources().openRawResourceFd(R.raw.dream_it_possible);
    private AudioFocusHelper audioFocusHelper;
    private MediaHandler mediaHandler;
    private  final int current_position_msg = 100;
    private int duration;
    private OnPreparedListen onPreparedListen;


    public MediaPlayerController(String url) {
        mediaHandler = new MediaHandler(this);
        mediaPlayer = new MediaPlayer();
        context = App.getInstance();
        audioFocusHelper = AudioFocusHelper.getInstance(context);
        init(url);
    }

    private void init(String url) {
        try {
//            mediaPlayer.setDataSource(url);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());//参数里的注释是直接播放sd卡上的视频
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            audioFocusHelper.setAudioFocusChangeListener(this);
            audioFocusHelper.startFocus();

            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    playerForeground.ACTION_STATUS = PlayerForeground.ACTION_PLAYING;
                    if (null != onPreparedListen && 0 == duration) {
                            duration = mediaPlayer.getDuration();
                            onPreparedListen.onPreparedListener(duration);
                    }
                    mediaPlayer.start();
                    System.out.println("currentPosition_onPrepared");
                    mediaHandler.sendEmptyMessageDelayed(current_position_msg,1000);
                    mediaPlayer.seekTo(0);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerState(IPlayerState playerForeground) {
        this.mState = playerForeground;
    }

    public void reset() {
        mState.reset();
    }

    public void start() {
        mState.start();
    }

    public void play(Surface surface) {
        mState.play(surface);
    }

    public void play() {
        mState.play();
    }

    public void pause() {
        mState.pause();
    }

    public void stop() {
        duration = 0;
        mState.stop();
    }

    public void reStart() {
        mState.reStart();
    }

    public void next(String urlNext) {
        mState.next(urlNext);
    }

    public void pre(String urlPre) {
        mState.pre(urlPre);
    }

    public int getDuration() {
        return mState.getDuration();
    }

    public int getCurrentPosition() {
        return mState.getCurrentPosition();
    }

    public void destroy() {
        audioFocusHelper.release();
        if (null != playerForeground) {
            playerForeground.destroy();
        }
        if (null != playerBackground) {
            playerBackground.destroy();
        }
        playerForeground = null;
        playerBackground = null;
//        mState.destroy();
    }

    public void setSurface(Surface surface) {
        mState.setSurface(surface);
    }

    @Override
    public void playerBackground() {
        if (null == playerBackground) {
            playerBackground = new PlayerBackground(mediaPlayer);
        }
        setPlayerState(playerBackground);
    }

    @Override
    public void playerForeground() {
        if (null == playerForeground) {
            playerForeground = new PlayerForeground(mediaPlayer);
        }
        setPlayerState(playerForeground);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://Pause playback，短时间失去焦点
                mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_GAIN://Resume playback，重新获得焦点
                mediaPlayer.start();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://

                break;
            case AudioManager.AUDIOFOCUS_LOSS://Stop playback,长时间失去焦点
                audioFocusHelper.stopFocus();
                break;
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mState.reset();
        duration = 0;
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playerForeground.ACTION_STATUS = PlayerForeground.ACTION_RELEASE;
//        mState.release();
    }

    private static class MediaHandler extends Handler {

        private WeakReference<MediaPlayerController> reference;

        private MediaHandler(MediaPlayerController mediaController) {
            reference = new WeakReference<>(mediaController);
        }

        @Override
        public void handleMessage(Message msg) {
            MediaPlayerController mediaPlayerController = reference.get();
//            System.out.println("currentPosition_handleMessage");
            if (null != mediaPlayerController) {
//                System.out.println("currentPosition_mediaPlayerController");
                int currentPosition = mediaPlayerController.getCurrentPosition();
//                System.out.println("currentPosition_mediaPlayerController"+currentPosition);
                if (currentPosition > 0 && null != mediaPlayerController.currentPositionListen) {
                    System.out.println("currentPosition_handleMessage:"+currentPosition);
                    mediaPlayerController.currentPositionListen.getCurrentPosition(currentPosition);
                    mediaPlayerController.mediaHandler.sendEmptyMessageDelayed(mediaPlayerController.current_position_msg,1000);
                }
            }
        }
    }

    public interface CurrentPositionListen {
        void getCurrentPosition(int currentPosition);
    }

    private CurrentPositionListen currentPositionListen;

    public void setCurrentPositionCallback(CurrentPositionListen currentPositionListen) {
        this.currentPositionListen = currentPositionListen;
    }

    public interface OnPreparedListen{
        void onPreparedListener(int duration);
    }

    public void setOnPreparedListener(OnPreparedListen onPreparedListen) {
        this.onPreparedListen = onPreparedListen;
    }
}
