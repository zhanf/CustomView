package zhanf.com.zfcustomview.mediamanager.mediaplayer;

import android.media.MediaPlayer;
import android.view.Surface;

/**
 * Created by Administrator on 2017/9/1.
 */

public class PlayerBackground implements IPlayerState {

    private MediaPlayer mediaPlayer;

    public PlayerBackground(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }


    @Override
    public void reset() {
        mediaPlayer.reset();
    }

    @Override
    public void start() {

    }

    @Override
    public void play(Surface surface) {

    }

    @Override
    public void play() {

    }

    @Override
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void reStart() {

    }

    @Override
    public void next(String urlNext) {

    }

    @Override
    public void pre(String urlPre) {

    }

    /**
     * 销毁 MediaPlayer
     * 注：因在 playerForeground 状态已将 mediaPlayer 置为release状态，
     * 此时不需更不可调用isPlaying()/stop()方法(如调用后崩溃)
     */
    @Override
    public void destroy() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public void setSurface(Surface surface) {

    }

    @Override
    public void release() {
        mediaPlayer.release();
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }
}
