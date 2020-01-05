package zhanf.com.zfcustomview.app.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.Utils;

import zhanf.com.zfcustomview.app.application.App;
import zhanf.com.zfcustomview.app.application.CrashHandler;

/**
 * Created by Administrator on 2017/8/29.
 */

public class InitializeService extends IntentService {

    private static final String ACTION_INIT = "initApplication";

    public InitializeService(){
        super("InitializeService");
    }


    public static void start(Context context) {
        Intent intent = new Intent(context, InitializeService.class);
        intent.setAction(ACTION_INIT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT.equals(action)) {
                initApplication();
            }
        }
    }

    private void initApplication() {

        Utils.init(App.getInstance());
        CrashHandler.init(new CrashHandler(getApplicationContext()));

    }

}
