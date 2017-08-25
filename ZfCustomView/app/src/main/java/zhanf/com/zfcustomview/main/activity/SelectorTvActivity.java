package zhanf.com.zfcustomview.main.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import zhanf.com.zfcustomview.R;

public class SelectorTvActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);

        findViewById(R.id.stv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SelectorTvActivity.this, "onClick", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
