package com.atguigu.startallplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void startAllPlayer(View view){
        Intent intent = new Intent();
       intent.setDataAndType(Uri.parse("http://192.168.110.195:8081/1920.mp4"),"video/*");
//        intent.setDataAndType(Uri.parse("http://192.168.191.1:8080/rmvb.rmvb"),"video/*");
       // intent.setDataAndType(Uri.parse("http://192.168.191.1:8080/rmvb11.rmvb"),"video/*");
//        intent.setDataAndType(Uri.parse("http://vfx.mtime.cn/Video/2016/07/19/mp4/160719095812990469.mp4"),"video/*");
        startActivity(intent);

    }
}
