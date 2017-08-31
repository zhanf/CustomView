package zhanf.com.zfcustomview.mediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import zhanf.com.zfcustomview.main.service.IService;

public class MediaServiceConnection implements ServiceConnection, IService {

    private String url;
    private Context context;
    private AudioPlayerService.PlayerBinder binder;

    private MediaServiceConnection(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    @Override
    public void start() {
        binder.start();
    }

    @Override
    public void pause() {
        binder.pause();
    }

    @Override
    public void stop() {
        binder.stop();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (binder == null) {
            binder = (AudioPlayerService.PlayerBinder) service;
            binder.init(url);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
