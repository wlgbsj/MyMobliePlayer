package com.wlgbsj.mymoblieplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.wlgbsj.mymoblieplayer.activity.MainActivity;


public class SplashActivity extends Activity {

    private Handler mhandler = new Handler() ;
    private boolean isStartMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, 2000);

    }

    public void  startMainActivity(){
        if(!isStartMain){
            isStartMain = true;
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startMainActivity();
        return super.onTouchEvent(event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mhandler.removeCallbacksAndMessages(null);
    }
}
