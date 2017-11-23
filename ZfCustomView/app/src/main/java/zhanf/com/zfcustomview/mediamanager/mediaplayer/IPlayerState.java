package zhanf.com.zfcustomview.mediamanager.mediaplayer;

import android.view.Surface;

/**
 * Created by Administrator on 2017/9/1.
 */

public interface IPlayerState {

    void reset();

    void prepareAsync();

    void autoPlay();

    void play();

    void pause();

    void stop();

    void reStart();

    void start();

    void next(String urlNext);

    void pre(String urlPre);

    void destroy();

    void setSurface(Surface surface);

    void release();

    int getDuration();

    int getCurrentPosition();

    void seekTo(int position);


}
