package zhanf.com.zfcustomview.app.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * activity生命周期状态监听回调
 *
 * @author zhanfeng Date:2017/3/10
 *         Time:11:20
 */

public class AppStatusTracker implements Application.ActivityLifecycleCallbacks {

    private static volatile AppStatusTracker tracker;
    private Context context;
    private boolean isForground;
    private int activeCount;
    private long timestamp;

    private List<Activity> allActivities;

    private AppStatusTracker(Context context) {
        this.context = context.getApplicationContext();
    }

    public void registerActivityLifecycleCallbacks() {
        ((Application)context).registerActivityLifecycleCallbacks(this);
    }

    public static AppStatusTracker getInstance(Context context) {
        if (null == tracker) {
            synchronized (AppStatusTracker.class) {
                if (null == tracker) {
                    tracker = new AppStatusTracker(context);
                }
            }
        }
        return tracker;
    }

    /**
     * @return App是否前台进程
     */
    public boolean isForground() {
        return isForground;
    }

    /**
     * 退出App
     */
    public void exitApp() {
        if (allActivities != null) {
            synchronized (allActivities) {
                for (Activity act : allActivities) {
                    act.finish();
                }
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (allActivities == null) {
            allActivities = new ArrayList<>();
        }
        allActivities.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

        if (activeCount == 0) {
            timestamp = System.currentTimeMillis();
        }
        activeCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        isForground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

        activeCount--;
        if (activeCount == 0) {
            isForground = false;
            timestamp = System.currentTimeMillis() - timestamp;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (allActivities != null) {
            allActivities.remove(activity);
        }
    }

    /**
     * 6.0权限获取当前Activity
     *
     * @return
     */
    public Activity getTopActivity() {
        if (allActivities.isEmpty()) {
            return null;
        } else {
            return allActivities.get(allActivities.size() - 1);
        }
    }
}