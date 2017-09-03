package zhanf.com.zfcustomview.mediamanager.mediaplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zhanf.com.zfcustomview.R;
import zhanf.com.zfcustomview.app.application.App;
import zhanf.com.zfcustomview.mediamanager.AudioFocusHelper;

import static zhanf.com.zfcustomview.mediamanager.mediaplayer.PlayerForeground.ACTION_PLAYING;

/**
 * Created by Administrator on 2017/9/1.
 */

public class MediaPlayerController implements IPlayerStateController, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private static final String TAG = "MediaPlayerManager";
    private IPlayerState mState;
    private MediaPlayer mediaPlayer;
    private Context context;
    private List<String> MediaList = new ArrayList<>();
    private PlayerForeground playerForeground;
    private PlayerBackground playerBackground;
    private AssetFileDescriptor descriptor = App.getInstance().getResources().openRawResourceFd(R.raw.dream_it_possible);
    private AudioFocusHelper audioFocusHelper;
    private MediaHandler mediaHandler;
    private final int current_position_msg = 100;
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
            Log.d(TAG, "init");
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
                    Log.d(TAG, "onPrepared");
                    playerForeground.actionStatus = ACTION_PLAYING;
                    if (null != onPreparedListen && 0 == duration) {
                        duration = mediaPlayer.getDuration();
                        onPreparedListen.onPreparedListener(duration);
                    }
                    mediaPlayer.start();
                    mediaHandler.sendEmptyMessageDelayed(current_position_msg, 1000);
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
        Log.d(TAG, "reset");
        mState.reset();
    }

    public void start() {
        Log.d(TAG, "start");
        mState.start();
    }

    public void play(Surface surface) {
        Log.d(TAG, "play()");
        mState.play(surface);
        if (TextUtils.equals(playerForeground.actionStatus, ACTION_PLAYING)) {
            mediaHandler.sendEmptyMessage(current_position_msg);
        }
    }

    public void play() {
        Log.d(TAG, "play");
        mState.play();
        if (TextUtils.equals(playerForeground.actionStatus, ACTION_PLAYING)) {
            mediaHandler.sendEmptyMessage(current_position_msg);
        }
    }

    public void pause() {
        Log.d(TAG, "pause");
        mediaHandler.removeCallbacksAndMessages(null);
        mState.pause();
    }

    public void stop() {
        Log.d(TAG, "stop");
        duration = 0;
        mState.stop();
    }

    public void reStart() {
        Log.d(TAG, "reStart");
        mState.reStart();
    }

    public void next(String urlNext) {
        Log.d(TAG, "next");
        mState.next(urlNext);
    }

    public void pre(String urlPre) {
        Log.d(TAG, "pre");
        mState.pre(urlPre);
    }

    public int getDuration() {
        Log.d(TAG, "getDuration");
        return mState.getDuration();
    }

    public int getCurrentPosition() {
        Log.d(TAG, "getCurrentPosition");
        return mState.getCurrentPosition();
    }

    public void destroy() {
        Log.d(TAG, "destroy");
        audioFocusHelper.release();
        mediaHandler.removeCallbacksAndMessages(null);
        if (null != playerForeground) {
            playerForeground.destroy();
        }
        if (null != playerBackground) {
            playerBackground.destroy();
        }
        mediaHandler = null;
        playerForeground = null;
        playerBackground = null;
//        mState.destroy();
    }

    public void setSurface(Surface surface) {
        Log.d(TAG, "setSurface");
        mState.setSurface(surface);
    }

    public void seekTo(int position) {
        Log.d(TAG, "seekTo" + position);
        mState.seekTo(position);
    }

    @Override
    public void playerBackground() {
        Log.d(TAG, "playerBackground");
        if (null == playerBackground) {
            playerBackground = new PlayerBackground(mediaPlayer);
        }
        setPlayerState(playerBackground);
    }

    @Override
    public void playerForeground() {
        Log.d(TAG, "playerForeground");
        if (null == playerForeground) {
            playerForeground = new PlayerForeground(mediaPlayer);
        }
        setPlayerState(playerForeground);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://Pause playback，短时间失去焦点
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_GAIN://Resume playback，重新获得焦点
                Log.d(TAG, "AUDIOFOCUS_GAIN");
                mediaPlayer.start();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                break;
            case AudioManager.AUDIOFOCUS_LOSS://Stop playback,长时间失去焦点
                Log.d(TAG, "AUDIOFOCUS_LOSS");
                audioFocusHelper.stopFocus();
                break;
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError");
        mState.reset();
        duration = 0;
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion");
        playerForeground.actionStatus = PlayerForeground.ACTION_RELEASE;
//        mState.release();
    }

    public void sendPlayMessage() {
        mediaHandler.sendEmptyMessage(current_position_msg);
    }

    public void removeCallbacksAndMessages() {
        mediaHandler.removeCallbacksAndMessages(null);
    }

    private static class MediaHandler extends Handler {

        private WeakReference<MediaPlayerController> reference;

        private MediaHandler(MediaPlayerController mediaController) {
            reference = new WeakReference<>(mediaController);
        }

        @Override
        public void handleMessage(Message msg) {
            MediaPlayerController mediaPlayerController = reference.get();
            Log.d(TAG, "currentPosition_handleMessage");
            if (null != mediaPlayerController) {
                int currentPosition = mediaPlayerController.getCurrentPosition();
                Log.d(TAG, "currentPosition_handleMessage" + currentPosition);
                if (currentPosition > 0 && null != mediaPlayerController.currentPositionListen) {
                    mediaPlayerController.currentPositionListen.getCurrentPosition(currentPosition);
                    mediaPlayerController.mediaHandler.sendEmptyMessageDelayed(mediaPlayerController.current_position_msg, 1000);
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

    public interface OnPreparedListen {
        void onPreparedListener(int duration);
    }

    public void setOnPreparedListener(OnPreparedListen onPreparedListen) {
        this.onPreparedListen = onPreparedListen;
    }
}
