package com.hoshino.companion;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("Hoshino Companion 空壳已启动");
        tv.setTextSize(22f);
        tv.setPadding(48, 48, 48, 48);
        setContentView(tv);
    }
}
