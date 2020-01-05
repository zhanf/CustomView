package zhanf.com.zfcustomview.mediamanager.audioplayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.ref.WeakReference;

import zhanf.com.zfcustomview.R;
import zhanf.com.zfcustomview.app.application.App;

/**
 * Created by Administrator on 2017/8/28.
 */

public class AudioPlayerService extends Service implements IService, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnErrorListener {

    private static final String MEDIA_PLAYER_URL = "media_player_url";

    private PlayerBinder playerBinder;
    private MediaPlayer mediaPlayer;

    private AssetFileDescriptor descriptor = App.getInstance().getResources().openRawResourceFd(R.raw.dream_it_possible);
    private AudioManager audioManager;
    private String url;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        url = intent.getStringExtra(MEDIA_PLAYER_URL);
        playerBinder = new PlayerBinder(this, url);
        return playerBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = new MediaPlayer();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        audioManager.abandonAudioFocus(this);
        playerBinder.stop();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        playerBinder.stop();
        mediaPlayer = null;
        playerBinder = null;
        super.onDestroy();
    }

    public void init(String url) {
        try {
            //mediaPlayer.setDataSource(MediaPlayerActivity.this, Uri.parse("android.resource://".concat(getPackageName()).concat("/") + R.raw.dream_it_possible));
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());//参数里的注释是直接播放sd卡上的视频
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//            int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_RING, AudioManager.AUDIOFOCUS_GAIN);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void stop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://Pause playback
                pause();
                break;
            case AudioManager.AUDIOFOCUS_GAIN://Resume playback
                start();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://

                break;
            case AudioManager.AUDIOFOCUS_LOSS://Stop playback
                audioManager.abandonAudioFocus(this);
                break;
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        System.out.println("MediaPlayer:" + what);
        mediaPlayer.reset();
        return true;
    }


    public static class PlayerBinder extends Binder implements IService {

        private WeakReference<AudioPlayerService> reference;

        private AudioPlayerService service;

        private PlayerBinder(AudioPlayerService service, String url) {

            reference = new WeakReference<>(service);

        }

        public void init(String url) {
            service = reference.get();
            if (null != service) {
                service.init(url);
            }
        }

        @Override
        public void start() {
            if (null != service) {
                service.start();
            }
        }

        @Override
        public void pause() {
            if (null != service) {
                service.pause();
            }
        }

        @Override
        public void stop() {
            if (null != service) {
                service.stop();
                service = null;
                reference = null;
            }
        }
    }

}
