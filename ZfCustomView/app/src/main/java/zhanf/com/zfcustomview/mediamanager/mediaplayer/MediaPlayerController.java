package zhanf.com.zfcustomview.mediamanager.mediaplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;

import java.io.IOException;
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

    public MediaPlayerController(String url) {
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
                    PlayerForeground.ACTION_STATUS = PlayerForeground.ACTION_PLAYING;
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
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

    public void play(){
        mState.play();
    }

    public void pause() {
        mState.pause();
    }

    public void stop() {
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
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mState.release();
    }
}
