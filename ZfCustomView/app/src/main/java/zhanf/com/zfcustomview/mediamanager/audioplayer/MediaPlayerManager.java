package zhanf.com.zfcustomview.mediamanager.audioplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.Surface;

import java.io.IOException;

import zhanf.com.zfcustomview.R;
import zhanf.com.zfcustomview.app.application.App;

/**
 * Created by Administrator on 2017/8/31.
 */

public class MediaPlayerManager implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    public static final String ACTION_REPLAY = "rePlay";
    public static final String ACTION_STOP = "stop";
    public static final String ACTION_NEXT = "next";
    public static final String ACTION_PREV = "prev";
    public static final String ACTION_TO = "to";
    public static final String ACTION_ISPLAYING = "playing";
    public static final String ACTION_START = "start";
    public static final String ACTION_PAUSE = "pause";

    public static final String ACTION_SET_PLAY_MODE = "setPlayMode";

    public static final String ACTION_ADD = "add";
    public static final String ACTION_CLEAR = "clear";
    public static final String ACTION_POSITION = "position";
    public static final String ACTION_LIST = "list";

    public static final String ACTION_SEEK = "seek";
    public static  String ACTION_STATUS = "status";

    private MediaPlayer mediaPlayer;
    private AssetFileDescriptor descriptor = App.getInstance().getResources().openRawResourceFd(R.raw.dream_it_possible);
    private AudioManager audioManager;
    private Context context;

    public MediaPlayerManager(String url, Surface surface) {
        this.mediaPlayer = new MediaPlayer();
        context = App.getInstance();
        init(url, surface);
    }

    private void init(String url, Surface surface) {
        try {
//            mediaPlayer.setDataSource(MediaPlayerActivity.this, Uri.parse("android.resource://".concat(getPackageName()).concat("/") + R.raw.dream_it_possible));
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());//参数里的注释是直接播放sd卡上的视频
//            mediaPlayer.setDataSource(url);
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_RING, AudioManager.AUDIOFOCUS_GAIN);
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    public void onDestroy() {
        audioManager.abandonAudioFocus(this);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void setSurface(Surface surface) {
        mediaPlayer.setSurface(surface);
    }

    /**
     * 切换播放
     *
     * @param path
     */
    public void changePlay(String path) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放or暂停视频
     */
    public void play() {
        if (mediaPlayer.isPlaying()) {
            ACTION_STATUS = ACTION_PAUSE;
            mediaPlayer.pause();
        } else {
            ACTION_STATUS = ACTION_ISPLAYING;
            mediaPlayer.start();
        }
    }

    public void start() {
        ACTION_STATUS = ACTION_ISPLAYING;
        mediaPlayer.start();
    }

    public void reStart(){
        if (TextUtils.equals(ACTION_STATUS,ACTION_ISPLAYING)) {
            mediaPlayer.start();
        }
    }

    /**
     * 勿更改状态值，此方法用于自动切换后TextureView不可用情况
     */
    public void pauseAuto() {
        mediaPlayer.pause();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://Pause playback
                mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_GAIN://Resume playback
                mediaPlayer.start();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://

                break;
            case AudioManager.AUDIOFOCUS_LOSS://Stop playback
                mediaPlayer.pause();
                break;
        }
    }

}
