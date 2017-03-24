package com.wlgbsj.mymoblieplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.wlgbsj.mymoblieplayer.IMusicPlayerService;
import com.wlgbsj.mymoblieplayer.R;
import com.wlgbsj.mymoblieplayer.domain.MediaItem;
import com.wlgbsj.mymoblieplayer.service.MusicPlayerService;
import com.wlgbsj.mymoblieplayer.utils.LyricUtils;
import com.wlgbsj.mymoblieplayer.utils.Utils;
import com.wlgbsj.mymoblieplayer.view.ShowLyricView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * Created by wlgbsj on 2017/3/22  16:04.
 */
public class AudioPlayerActivity extends Activity implements View.OnClickListener {
    public static final int PROGRESS = 1;
    public static final int SHOW_LYRIC =2;
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

    private ShowLyricView showLyricView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case  SHOW_LYRIC:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        showLyricView.showNextLyric(currentPosition);

                        handler.removeMessages(SHOW_LYRIC);
                        handler.sendEmptyMessage(SHOW_LYRIC);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;

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

        /*receiver = new MyReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.SEND_MESSAGE_TO_ACTIVITY);
        registerReceiver(receiver, intentFilter);*/

        //采用EvnetBUs
        EventBus.getDefault().register(this);


    }

    class MyReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //ui线程
            //音乐开始播放  通知Activity更新View
            showViewData(null);
        }
    }

    //订阅方法   不能私有方法   要用public
    @Subscribe (threadMode = ThreadMode.MAIN,sticky = false,priority = 0)
    public void showViewData(MediaItem mediaItem) {
        showData();
        checkPlaymode();
    }

    private void showData() {
        if (service != null) {
            try {
                tvArtist.setText(service.getArtist());
                tvName.setText(service.getName());

                seekbarAudio.setMax(service.getDuration());


                handler.sendEmptyMessage(PROGRESS);

                ShowLyric();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    private void ShowLyric() {
        //解析歌词
        LyricUtils lyricUtils = new LyricUtils();

        try {
            String path = service.getAudioPath();//得到歌曲的绝对路径

            //传歌词文件
            //mnt/sdcard/audio/beijingbeijing.mp3
            //mnt/sdcard/audio/beijingbeijing.lrc
            path = path.substring(0,path.lastIndexOf("."));
            File file = new File(path + ".lrc");
            if(!file.exists()){
                file = new File(path + ".txt");
            }
            lyricUtils.readLyricFile(file);//解析歌词

            showLyricView.setLyrics(lyricUtils.getLyrics());

        } catch (RemoteException e) {
            e.printStackTrace();
        }



        if(lyricUtils.isExistsLyric()){
            handler.sendEmptyMessage(SHOW_LYRIC);
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
        showLyricView= (ShowLyricView) findViewById(R.id.ShowLyricView);

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
            setPlaymode();
        } else if (v == btnAudioPre) {
            if(service!=null){
                try {
                    service.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
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
            if(service!=null){
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnLyrc) {
            // Handle clicks for btnLyrc
        }
    }

    private void setPlaymode() {//模式依次切换
        try {
            int playmode = service.getPlayMode();
            if(playmode==MusicPlayerService.REPEAT_NORMAL){
                playmode = MusicPlayerService.REPEAT_SINGLE;
            }else if(playmode == MusicPlayerService.REPEAT_SINGLE){
                playmode = MusicPlayerService.REPEAT_ALL;
            }else if(playmode ==MusicPlayerService.REPEAT_ALL){
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }else{
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }

            //保持
            service.setPlayMode(playmode);

            //设置图片
            showPlaymode();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showPlaymode() {
        try {
            int playmode = service.getPlayMode();

            if(playmode==MusicPlayerService.REPEAT_NORMAL){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            }else if(playmode == MusicPlayerService.REPEAT_SINGLE){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
                Toast.makeText(AudioPlayerActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
            }else if(playmode ==MusicPlayerService.REPEAT_ALL){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
                Toast.makeText(AudioPlayerActivity.this, "全部循环", Toast.LENGTH_SHORT).show();
            }else{
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 校验状态
     */
    private void checkPlaymode() {
        try {
            int playmode = service.getPlayMode();

            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            } else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);
       //取消注册广播
      /* if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }*/
        EventBus.getDefault().unregister(this);


        //解绑服务
        if(con != null){
            unbindService(con);
            con = null;
        }

        super.onDestroy();
    }
}
