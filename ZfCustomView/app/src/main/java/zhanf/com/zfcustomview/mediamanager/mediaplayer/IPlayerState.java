package zhanf.com.zfcustomview.mediamanager.mediaplayer;

import android.view.Surface;

/**
 * Created by Administrator on 2017/9/1.
 */

public interface IPlayerState {

    void reset();

    void start();

    void play(Surface surface);

    void play();

    void pause();

    void stop();

    void reStart();

    void next(String urlNext);

    void pre(String urlPre);

    void destroy();

    void setSurface(Surface surface);

    void release();

}
