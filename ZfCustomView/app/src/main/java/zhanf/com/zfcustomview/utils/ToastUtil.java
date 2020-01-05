package zhanf.com.zfcustomview.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

import zhanf.com.zfcustomview.app.application.App;


/**
 * Created by zhanf on 2018/5/14.
 */
public class ToastUtil {

    static ToastUtil td;

    public static void showLong(@StringRes int resId) {
        showLong(App.getInstance().getString(resId));
    }

    public static void showLong(String msg) {
        if (td == null) {
            td = new ToastUtil(App.getInstance());
        }
        td.setText(msg);
        td.create().show();
    }

    public static void showShort(@StringRes int resId) {
        showLong(App.getInstance().getString(resId));
    }

    public static void showShort(String msg) {
        if (td == null) {
            td = new ToastUtil(App.getInstance());
        }
        td.setText(msg);
        td.createShort().show();
    }

    Context context;
    Toast toast;
    String msg;

    private ToastUtil(Context context) {
        this.context = context.getApplicationContext();
    }

    private Toast create() {
        if (null == toast) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);

        }
        toast.setText(msg);
        toast.setDuration(Toast.LENGTH_LONG);
        return toast;
    }

    private Toast createShort() {
        if (null == toast) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
    }

    private void setText(String text) {
        msg = text;
    }
}
