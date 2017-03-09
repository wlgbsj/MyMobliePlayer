package com.wlgbsj.mymoblieplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by wlgbsj on 2017/3/9  9:47.
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("111111");
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER);
        setContentView(textView);
    }
}
