package zhanf.com.zfcustomview.mediamanager.mediaplayer;

import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.Surface;

import java.io.IOException;

/**
 * Created by Administrator on 2017/9/1.
 */

public class PlayerForeground implements IPlayerState {

    public static final String ACTION_INIT = "init";
    public static final String ACTION_RESET = "reset";
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_PLAYING = "playing";
    public static  String ACTION_STATUS = ACTION_INIT;

    private MediaPlayer mediaPlayer;

    public PlayerForeground(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void reset() {
        mediaPlayer.reset();
        ACTION_STATUS = ACTION_RESET;
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        mediaPlayer.prepareAsync();
    }

    /**
     * TextureView 可用时调用，自己识别之前是播放/暂停状态
     * @param surface surface
     */
    @Override
    public void play(Surface surface) {
        setSurface(surface);
        if (TextUtils.equals(ACTION_STATUS,ACTION_INIT)) {
            start();
        } else {
            if (TextUtils.equals(ACTION_STATUS,ACTION_PLAYING)) {
                mediaPlayer.start();
            }
        }
    }

    /**
     * 手动切换播放、暂停状态时调用，并可保存当前播放状态
     */
    @Override
    public void play() {
        if (mediaPlayer.isPlaying()) {
            ACTION_STATUS = ACTION_PAUSE;
            mediaPlayer.pause();
        } else {
            ACTION_STATUS = ACTION_PLAYING;
            mediaPlayer.start();
        }
    }

    /**
     * 重新播放
     */
    @Override
    public void reStart() {
        stop();
        mediaPlayer.prepareAsync();
    }

    /**
     * 播放下一个
     * @param urlNext url
     */
    @Override
    public void next(String urlNext) {
        try {
            reset();
            mediaPlayer.setDataSource(urlNext);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放上一个
     * @param urlPre url
     */
    @Override
    public void pre(String urlPre) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(urlPre);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 销毁MediaPlayer
     */
    @Override
    public void destroy() {
        ACTION_STATUS = ACTION_INIT;
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                stop();
            }
            release();
            mediaPlayer = null;
        }
    }

    /**
     * TextureView 可用时调用
     * @param surface surface
     */
    @Override
    public void setSurface(Surface surface) {
        mediaPlayer.setSurface(surface);
    }

    /**
     * looping 为 false,播放完成时释放资源，不可再调用isPlaying()/stop()等方法
     */
    @Override
    public void release() {
        mediaPlayer.release();
    }

    /**
     * 暂停，一般不掉用此方法
     */
    @Override
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
    }
}
