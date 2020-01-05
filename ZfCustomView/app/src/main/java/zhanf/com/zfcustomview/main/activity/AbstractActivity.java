package zhanf.com.zfcustomview.main.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import zhanf.com.zfcustomview.utils.StatusBarCompat;

/**
 *
 * @author zhanf
 * @date 2018/5/9
 */

public abstract class AbstractActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏status状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        StatusBarCompat.compat(this, true);
    }
}
