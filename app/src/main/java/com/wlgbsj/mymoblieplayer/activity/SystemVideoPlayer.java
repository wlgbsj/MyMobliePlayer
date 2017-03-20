package com.wlgbsj.mymoblieplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wlgbsj.mymoblieplayer.R;
import com.wlgbsj.mymoblieplayer.domain.MediaItem;
import com.wlgbsj.mymoblieplayer.utils.LogUtil;
import com.wlgbsj.mymoblieplayer.utils.Utils;
import com.wlgbsj.mymoblieplayer.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by wlgbsj on 2017/3/10  10:11.
 */
public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    /**
     * 视频进度的更新
     */
    private static final int PROGRESS = 1;
    /**
     * 隐藏控制面板
     */
    private static final int HIDE_MEDIACONTROLLER = 2;
    /**
     * 全屏
     */
    private static final int FULL_SCREEN = 1;
    /**
     * 默认屏幕
     */
    private static final int DEFAULT_SCREEN = 2;
    private VideoView videoview;
    private Uri uri;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwichPlayer;
    private LinearLayout llBottom;
    private RelativeLayout media_controller;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSiwchScreen;

    private Utils utils;
    /**
     * 监听电量变化的广播
     */
    private MyReceiver receiver;
    /**
     * 传入进来的视频列表
     */
    private ArrayList<MediaItem> mediaItems;
    /**
     * 要播放的列表中的具体位置
     */
    private int position;

    /**
     * 1.定义手势识别器
     */
    private GestureDetector detector;

    /**
     * 是否显示控制面板
     */
    private boolean isshowMediaController = false;
    /**
     * 是否全屏
     */
    private boolean isFullScreen = false;

    /**
     * 屏幕的宽
     */
    private int screenWidth = 0;

    /**
     * 屏幕的高
     */
    private int screenHeight = 0;

    /**
     * 真实视频的宽
     */
    private int videoWidth;
    /**
     * 真实视频的高
     */
    private int videoHeight;

    /**
     * 调用声音
     */
    private AudioManager am;

    /**
     * 当前的音量
     */
    private int currentVoice;

    /**
     * 0~15
     * 最大音量
     */
    private int maxVoice;
    /**
     * 是否是静音
     */
    private boolean isMute = false;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-07-18 15:26:59 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
        llTop = (LinearLayout)findViewById( R.id.ll_top );
        tvName = (TextView)findViewById( R.id.tv_name );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
        tvSystemTime = (TextView)findViewById( R.id.tv_system_time );
        btnVoice = (Button)findViewById( R.id.btn_voice );
        seekbarVoice = (SeekBar)findViewById( R.id.seekbar_voice );
        btnSwichPlayer = (Button)findViewById( R.id.btn_swich_player );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvCurrentTime = (TextView)findViewById( R.id.tv_current_time );
        seekbarVideo = (SeekBar)findViewById( R.id.seekbar_video );
        tvDuration = (TextView)findViewById( R.id.tv_duration );
        btnExit = (Button)findViewById( R.id.btn_exit );
        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
        btnVideoStartPause = (Button)findViewById( R.id.btn_video_start_pause );
        btnVideoNext = (Button)findViewById( R.id.btn_video_next );
        btnVideoSiwchScreen = (Button)findViewById( R.id.btn_video_siwch_screen );
        videoview = (VideoView) findViewById(R.id.videoview);
        media_controller = (RelativeLayout) findViewById(R.id.media_controller);

        btnVoice.setOnClickListener( this );
        btnSwichPlayer.setOnClickListener( this );
        btnExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoStartPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSiwchScreen.setOnClickListener( this );

        //最大音量和SeekBar关联
        seekbarVoice.setMax(maxVoice);
        //设置当前进度-当前音量
        seekbarVoice.setProgress(currentVoice);

    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-07-18 15:26:59 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnVoice ) {
            isMute = !isMute;
            // Handle clicks for btnVoice
            updataVoice(currentVoice,isMute);
        } else if ( v == btnSwichPlayer ) {
            // Handle clicks for btnSwichPlayer
        } else if ( v == btnExit ) {
            // Handle clicks for btnExit
            finish();
        } else if ( v == btnVideoPre ) {
            // Handle clicks for btnVideoPre
            playPreVideo();
        } else if ( v == btnVideoStartPause ) {
            // Handle clicks for btnVideoStartPause
            startAndPause();
        } else if ( v == btnVideoNext ) {
            // Handle clicks for btnVideoNext
            playNextVideo();
        } else if ( v == btnVideoSiwchScreen ) {
            // Handle clicks for btnVideoSiwchScreen
            setFullScreenAndDefault();
        }

        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
    }

    private void startAndPause() {
        if(videoview.isPlaying()){
            //视频在播放-设置暂停
            videoview.pause();
            //按钮状态设置播放
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        }else{
            //视频播放
            videoview.start();
            //按钮状态设置暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 播放上一个视频
     */
    private void playPreVideo() {
        if(mediaItems != null && mediaItems.size()>0){
            //播放上一个视频
            position--;
            if(position >= 0){

                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        }else if(uri != null){
            //设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
            setButtonState();
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        if(mediaItems != null && mediaItems.size()>0){
            //播放下一个
            position++;
            if(position < mediaItems.size()){

                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        }else if(uri != null){
            //设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
            setButtonState();
        }

    }

    private void setButtonState() {
        if(mediaItems != null && mediaItems.size() >0){
            if(mediaItems.size()==1){
                setEnable(false);
            }else if(mediaItems.size()==2) {
                if(position==0){
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);

                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);

                }else if(position==mediaItems.size()-1){
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);

                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);

                }
            }else{
                if(position==0){
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                }else if(position==mediaItems.size()-1){
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                }else{
                    setEnable(true);
                }
            }
        }else if(uri != null){
            //两个按钮设置灰色
            setEnable(false);
        }
    }

    private void setEnable(boolean isEnable) {
        if(isEnable){
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoPre.setEnabled(true);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            btnVideoNext.setEnabled(true);
        }else{
            //两个按钮设置灰色
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HIDE_MEDIACONTROLLER://隐藏控制面板
                    hideMediaController();
                    break;
                case PROGRESS:

                    //1.得到当前的视频播放进程
                    int currentPosition = videoview.getCurrentPosition();

                    //2.SeekBar.setProgress(当前进度);
                    seekbarVideo.setProgress(currentPosition);


                    //更新文本播放进度
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));


                    //设置系统时间
                    tvSystemTime.setText(getSysteTime());


                    //3.每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;
            }
        }
    };

    /**
     * 得到系统时间
     * @return
     */
    private String getSysteTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//初始化父类
        LogUtil.e("onCreate--");


        initData();
        findViews();

        setListener();

        getData();

        setData();
        //设置控制面板
//        videoview.setMediaController(new MediaController(this));
    }

    private void setData() {

        if(mediaItems != null &&mediaItems .size() >0){
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());//设置视频的名称
            videoview.setVideoPath(mediaItem.getData());

        }else if(uri !=null){
            tvName.setText(uri.toString());//设置视频的名称
            videoview.setVideoURI(uri);
        }else{
            Toast.makeText(SystemVideoPlayer.this, "帅哥你没有传递数据", Toast.LENGTH_SHORT).show();
        }
        setButtonState();
    }

    private void getData() {
        //得到播放地址
        uri = getIntent().getData();
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position",0);

    }

    private void initData() {
        utils = new Utils();
        //注册电量广播
        receiver = new MyReceiver();
        IntentFilter intentFiler = new IntentFilter();
        //当电量变化的时候发这个广播
        intentFiler.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFiler);

        //2.实例化手势识别器，并且重写双击，点击，长按
        detector  = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
//                Toast.makeText(SystemVideoPlayer.this, "我被长按了", Toast.LENGTH_SHORT).show();
                startAndPause();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
//                Toast.makeText(SystemVideoPlayer.this, "我被双击了", Toast.LENGTH_SHORT).show();
                setFullScreenAndDefault();
                return super.onDoubleTap(e);

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
//                Toast.makeText(SystemVideoPlayer.this, "我被单击了", Toast.LENGTH_SHORT).show();
                if(isshowMediaController){
                    //隐藏
                    hideMediaController();
                    //把隐藏消息移除
                    handler.removeMessages(HIDE_MEDIACONTROLLER);

                }else{
                    //显示
                    showMediaController();
                    //发消息隐藏
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                }

                return super.onSingleTapConfirmed(e);
            }
        });

        //得到屏幕的宽和高
        //过时的方式
//        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
//        screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        //得到屏幕的宽和高最新方式
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //得到音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    }

    private void setFullScreenAndDefault() {
        if(isFullScreen){
            //默认
            setVideoType(DEFAULT_SCREEN);
        }else{
            //全屏
            setVideoType(FULL_SCREEN);
        }
    }

    private void setVideoType(int defaultScreen) {
        switch (defaultScreen){
            case FULL_SCREEN://全屏
                //1.设置视频画面的大小-屏幕有多大就是多大
                videoview.setVideoSize(screenWidth,screenHeight);
                //2.设置按钮的状态-默认
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);
                isFullScreen = true;
                break;
            case DEFAULT_SCREEN://默认
                //1.设置视频画面的大小
                //视频真实的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                //屏幕的宽和高
                int width = screenWidth;
                int height = screenHeight;

                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }

                videoview.setVideoSize(width,height);
                //2.设置按钮的状态--全屏
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);
                isFullScreen = false;
                break;
        }
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level",0);//0~100;
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if(level <= 0){
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        }else if(level <=10){
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        }else if(level <= 20){
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        }else if(level <= 40){
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        }else if(level <= 60){
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        }else if(level <= 80){
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        }else if(level <= 100){
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
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
        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());

        seekbarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());
    }

    class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                if(progress >0 ){
                    isMute = false;
                }else{
                    isMute = true;
                }
                updataVoice(progress,isMute);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
        }
    }

    /**
     * 设置音量的大小
     * @param progress
     */
    private void updataVoice(int progress,boolean isMute) {
        if(isMute){
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            seekbarVoice.setProgress(0);
        }else{
            am.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            seekbarVoice.setProgress(progress);
            currentVoice = progress;
        }


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

            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        /**
         * 当手指离开的时候回调这个方法
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        //当底层解码准备好的时候
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();
            videoview.start();//开始播放
            //1.视频的总时长，关联总长度
            int duration =  videoview.getDuration();
            seekbarVideo.setMax(duration);
            tvDuration.setText(utils.stringForTime(duration));

            hideMediaController();//默认是隐藏控制面板
            //2.发消息
            handler.sendEmptyMessage(PROGRESS);

//            videoview.setVideoSize(200,200);
//            videoview.setVideoSize(mp.getVideoWidth(),mp.getVideoHeight());

            //屏幕的默认播放
            setVideoType(DEFAULT_SCREEN);


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
            playNextVideo();
//            Toast.makeText(SystemVideoPlayer.this, "播放完成了="+uri, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.e("onRestart--");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("onStart--");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume--");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("onPause--");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("onStop--");
    }

    @Override
    protected void onDestroy() {

        //释放资源的时候，先释放子类，在释放父类
        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }
        LogUtil.e("onDestroy--");
        super.onDestroy();

    }

    private float startY;
    /**
     * 屏幕的高
     */
    private float touchRang;

    /**
     * 当一按下的音量
     */
    private int mVol;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //3.把事件传递给手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://手指按下
                //1.按下记录值
                startY = event.getY();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight, screenWidth);//screenHeight
                handler.removeMessages(HIDE_MEDIACONTROLLER);

                break;
            case MotionEvent.ACTION_MOVE://手指移动
                //2.移动的记录相关值
                float endY = event.getY();
                float distanceY = startY - endY;
                //改变声音 = （滑动屏幕的距离： 总距离）*音量最大值
                float delta = (distanceY/touchRang)*maxVoice;
                //最终声音 = 原来的 + 改变声音；
                int voice = (int) Math.min(Math.max(mVol+delta,0),maxVoice);
                if(delta != 0){
                    isMute = false;
                    updataVoice(voice,isMute);
                }

//                startY = event.getY();//不要加
                break;
            case MotionEvent.ACTION_UP://手指离开
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 显示控制面板
     */
    private void showMediaController(){
        media_controller.setVisibility(View.VISIBLE);
        isshowMediaController = true;
    }


    /**
     * 隐藏控制面板
     */
    private void hideMediaController(){
        media_controller.setVisibility(View.GONE);
        isshowMediaController = false;
    }

    /**
     * 监听物理健，实现声音的调节大小
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode ==KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice --;
            updataVoice(currentVoice,false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
            return true;
        }else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice ++;
            updataVoice(currentVoice,false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
