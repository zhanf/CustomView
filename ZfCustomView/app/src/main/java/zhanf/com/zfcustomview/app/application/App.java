package zhanf.com.zfcustomview.app.application;

import android.app.Application;

import zhanf.com.zfcustomview.app.service.InitializeService;

/**
 * Created by Administrator on 2017/8/29.
 */

public class App extends Application {

    private static App instance;

    public static synchronized App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //在子线程中完成其他初始化
        InitializeService.start(this);
    }
}
