package zhanf.com.zfcustomview.main.service;

import android.view.Surface;

/**
 * Created by Administrator on 2017/8/29.
 */

public interface IService {
    void start();

    void pause();

    void stop();

    void setSurface(Surface surface);

}
