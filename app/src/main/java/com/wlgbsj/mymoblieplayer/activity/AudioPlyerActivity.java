package com.wlgbsj.mymoblieplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wlgbsj.mymoblieplayer.IMusicPlayerService;
import com.wlgbsj.mymoblieplayer.R;
import com.wlgbsj.mymoblieplayer.service.MusicPlayerService;
import com.wlgbsj.mymoblieplayer.utils.Utils;

/**
 * Created by wlgbsj on 2017/3/22  16:04.
 */
public class AudioPlyerActivity extends Activity implements View.OnClickListener {
    public static final int PROGRESS = 1;
    private int position;
    private IMusicPlayerService service;//服务的代理类，通过它可以调用服务的方法
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPlaymode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnLyrc;

    private BroadcastReceiver receiver;
    private Utils utils;

    private boolean notificaiton = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case PROGRESS:

                    try {
                        int currentPosition = service.getCurrentPosition();
                        seekbarAudio.setProgress(currentPosition);
                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));
                        handler.removeMessages(PROGRESS);
                        handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


                    break;


            }
        }
    };


    private ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder ibinder) {
            service = IMusicPlayerService.Stub.asInterface(ibinder);
            if (service != null) {
                try {
                    if(!notificaiton){ //列表来的
                        service.openAudio(position);
                    }else {  //通知来的
                        showData();
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (service != null) {
                try {
                    service.stop();
                    service = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audioplayer);
        initData();
        findViews();
        getData();
        bindAndStartService();

    }

    private void initData() {
        utils = new Utils();
        receiver = new MyReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.SEND_MESSAGE_TO_ACTIVITY);
        registerReceiver(receiver, intentFilter);

    }

    class MyReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            showData();
        }
    }

    private void showData() {
        if (service != null) {
            try {
                tvArtist.setText(service.getArtist());
                tvName.setText(service.getName());

                seekbarAudio.setMax(service.getDuration());


                handler.sendEmptyMessage(PROGRESS);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    private void findViews() {
        setContentView(R.layout.activity_audioplayer);

        //开启帧动画
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable rocketAnimation = (AnimationDrawable) ivIcon.getBackground();
        rocketAnimation.start();


        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnAudioPlaymode = (Button) findViewById(R.id.btn_audio_playmode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (Button) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnLyrc = (Button) findViewById(R.id.btn_lyrc);

        btnAudioPlaymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnLyrc.setOnClickListener(this);

        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * 得到数据
     */
    private void getData() {
        notificaiton = getIntent().getBooleanExtra("notification",false);
        if(!notificaiton){ //表明不是从通知来的
            position = getIntent().getIntExtra("position", 0);
        }

    }

    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.wlgbsj.MUSICSERVICE");
        bindService(intent, con, Context.BIND_AUTO_CREATE);
        startService(intent);//不至于实例化多个服务    StartService启动多次 服务不会启动多次
    }

    @Override
    public void onClick(View v) {
        if (v == btnAudioPlaymode) {
            // Handle clicks for btnAudioPlaymode
        } else if (v == btnAudioPre) {
            // Handle clicks for btnAudioPre
        } else if (v == btnAudioStartPause) {
            if (service != null) {
                try {
                    if (service.isPlaying()) {
                        //暂停
                        service.pause();
                        //按钮-播放
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                    } else {
                        //播放
                        service.start();
                        //按钮-暂停
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            // Handle clicks for btnAudioStartPause
        } else if (v == btnAudioNext) {
            // Handle clicks for btnAudioNext
        } else if (v == btnLyrc) {
            // Handle clicks for btnLyrc
        }
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);
       //取消注册广播
       if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }


        //解绑服务
        if(con != null){
            unbindService(con);
            con = null;
        }

        super.onDestroy();
    }
}
