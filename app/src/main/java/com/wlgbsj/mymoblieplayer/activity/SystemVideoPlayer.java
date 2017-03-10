package com.wlgbsj.mymoblieplayer.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.wlgbsj.mymoblieplayer.R;
import com.wlgbsj.mymoblieplayer.utils.Utils;

/**
 * Created by wlgbsj on 2017/3/10  10:11.
 */
public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    private static final int PROGRESS = 0x111;
    // Content View Elements

    private LinearLayout mLl_top;
    private TextView mTv_name;
    private ImageView mIv_battery;
    private TextView mTv_system_time;
    private Button mBtn_voice;
    private SeekBar mSeekbar_voice;
    private Button mBtn_swich_player;
    private LinearLayout mLl_bottom;
    private TextView mTv_current_time;
    private SeekBar mSeekbar_video;
    private TextView mTv_duration;
    private Button mBtn_exit;
    private Button mBtn_video_pre;
    private Button mBtn_video_start_pause;
    private Button mBtn_video_next;
    private Button mBtn_video_siwch_screen;

    private VideoView videoview;
    private  Utils utils;
    private Uri uri;



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case  PROGRESS:

                    //1.得到当前的视频播放进程
                    int currentPosition = videoview.getCurrentPosition();

                    //2.SeekBar.setProgress(当前进度);
                    mSeekbar_video.setProgress(currentPosition);


                    //更新文本播放进度
                    mTv_current_time.setText(utils.stringForTime(currentPosition));


                    //3.每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);


                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        utils = new Utils();

        bindViews();
        setListener();

        //得到播放地址
        uri = getIntent().getData();
        if(uri != null){
            videoview.setVideoURI(uri);
        }

        //设置控制面板
//        videoview.setMediaController(new MediaController(this));
    }



    // End Of Content View Elements

    private void bindViews() {
        setContentView(R.layout.activity_system_video_player);
        videoview = (VideoView) findViewById(R.id.videoview);
        mLl_top = (LinearLayout) findViewById(R.id.ll_top);
        mTv_name = (TextView) findViewById(R.id.tv_name);
        mIv_battery = (ImageView) findViewById(R.id.iv_battery);
        mTv_system_time = (TextView) findViewById(R.id.tv_system_time);
        mBtn_voice = (Button) findViewById(R.id.btn_voice);
        mSeekbar_voice = (SeekBar) findViewById(R.id.seekbar_voice);
        mBtn_swich_player = (Button) findViewById(R.id.btn_swich_player);
        mLl_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        mTv_current_time = (TextView) findViewById(R.id.tv_current_time);
        mSeekbar_video = (SeekBar) findViewById(R.id.seekbar_video);
        mTv_duration = (TextView) findViewById(R.id.tv_duration);
        mBtn_exit = (Button) findViewById(R.id.btn_exit);
        mBtn_video_pre = (Button) findViewById(R.id.btn_video_pre);
        mBtn_video_start_pause = (Button) findViewById(R.id.btn_video_start_pause);
        mBtn_video_next = (Button) findViewById(R.id.btn_video_next);
        mBtn_video_siwch_screen = (Button) findViewById(R.id.btn_video_siwch_screen);


        mBtn_voice.setOnClickListener( this );
        mBtn_swich_player.setOnClickListener( this );
        mBtn_exit.setOnClickListener( this );
        mBtn_video_pre.setOnClickListener( this );
        mBtn_video_start_pause.setOnClickListener( this );
        mBtn_video_next.setOnClickListener( this );
        mBtn_video_siwch_screen.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        if ( v == mBtn_voice ) {
            // Handle clicks for btnVoice
        } else if ( v == mBtn_swich_player ) {
            // Handle clicks for btnSwichPlayer
        } else if ( v == mBtn_exit ) {
            // Handle clicks for btnExit
        } else if ( v == mBtn_video_pre ) {
            // Handle clicks for btnVideoPre
        } else if ( v == mBtn_video_start_pause ) {
            // Handle clicks for btnVideoStartPause
            if(videoview.isPlaying()){
                //视频在播放-设置暂停
                videoview.pause();
                //按钮状态设置播放
                mBtn_video_start_pause.setBackgroundResource(R.drawable.btn_video_start_selector);
            }else{
                //视频播放
                videoview.start();
                //按钮状态设置暂停
                mBtn_video_start_pause.setBackgroundResource(R.drawable.btn_video_pause_selector);
            }
        } else if ( v == mBtn_video_next ) {
            // Handle clicks for btnVideoNext
        } else if ( v == mBtn_video_siwch_screen ) {
            // Handle clicks for btnVideoSiwchScreen
        }
    }

    private void setListener() {
        //准备好的监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());

        //播放出错了的监听
        videoview.setOnErrorListener(new MyOnErrorListener());

        //播放完成了的监听
        videoview.setOnCompletionListener(new MyOnCompletionListener() );

        //设置SeeKbar状态变化的监听
        mSeekbar_video.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
    }

    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当手指滑动的时候，会引起SeekBar进度变化，会回调这个方法
         * @param seekBar
         * @param progress
         * @param fromUser 如果是用户引起的true,不是用户引起的false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                videoview.seekTo(progress);
            }

        }

        /**
         * 当手指触碰的时候回调这个方法
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        /**
         * 当手指离开的时候回调这个方法
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        //当底层解码准备好的时候
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoview.start();//开始播放
            //1.视频的总时长，关联总长度
            int duration =  videoview.getDuration();
            mSeekbar_video.setMax(duration);
            mTv_duration.setText(utils.stringForTime(duration));
            //2.发消息
            handler.sendEmptyMessage(PROGRESS);
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemVideoPlayer.this, "播放出错了哦", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(SystemVideoPlayer.this, "播放完成了="+uri, Toast.LENGTH_SHORT).show();
        }
    }
}
