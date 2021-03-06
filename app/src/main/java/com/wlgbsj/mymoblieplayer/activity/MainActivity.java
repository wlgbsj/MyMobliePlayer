package com.wlgbsj.mymoblieplayer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.wlgbsj.mymoblieplayer.R;
import com.wlgbsj.mymoblieplayer.base.BasePager;
import com.wlgbsj.mymoblieplayer.pager.AudioPager;
import com.wlgbsj.mymoblieplayer.pager.NetAudioPager;
import com.wlgbsj.mymoblieplayer.pager.NetVideoPager;
import com.wlgbsj.mymoblieplayer.pager.VideoPager;

import java.util.ArrayList;


/**
 * 作者：杨光福 on 2016/7/16 10:26
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：主页面
 */
public class MainActivity extends FragmentActivity {


    private RadioGroup rg_bottom_tag;

    /**
     * 页面的集合
     */
    private ArrayList<BasePager> basePagers;

    /**
     * 选中的位置
     */
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);


        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this));//添加本地视频页面-0
        basePagers.add(new AudioPager(this));//添加本地音乐页面-1
        basePagers.add(new NetVideoPager(this));//添加网络视频页面-2
        basePagers.add(new NetAudioPager(this));//添加网络音频页面-3


        //设置RadioGroup的监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_bottom_tag.check(R.id.rb_video);//默认选中首页

    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (checkedId) {
                default:
                    position = 0;
                    break;
                case R.id.rb_audio://音频
                    position = 1;
                    break;
                case R.id.rb_net_video://网络视频
                    position = 2;
                    break;
                case R.id.rb_netaudio://网络音频
                    position = 3;
                    break;
            }

            setFragment();


        }
    }

    /**
     * 把页面添加到Fragment中
     */
    private void setFragment() {
        //1.得到FragmentManger
        FragmentManager manager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction ft = manager.beginTransaction();
        //3.替换
        //这种 new Fragment用法  早  compile 'com.android.support:appcompat-v7:24.2.1'  不行 23 才可以
        ft.replace(R.id.fl_main_content, new Fragment() {
            @Nullable
            @Override
            public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                BasePager basePager = getBasePager();
                if (basePager != null) {
                    //各个页面的视图
                    return basePager.rootView;
                }
                return null;
            }
        });
        //4.提交事务
        ft.commit();

    }

    /**
     * 根据位置得到对应的页面
     *
     * @return
     */
    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        if (basePager != null && !basePager.isInitData) {
            basePager.initData();//联网请求或者绑定数据
            basePager.isInitData = true;
        }
        return basePager;
    }

    private boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (position != 0) {//不是第一个界面的退出
                rg_bottom_tag.check(0);
                return true;
            } else if (!isExit) {// 已经到了第一个界面 的退出
                isExit = true;
                Toast.makeText(MainActivity.this,"再次点击，2秒后会退出应用",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      isExit = false;
                    }
                },2000);
               return  true;
            }
        }
        return super.onKeyDown(keyCode, event);//系统默认的推出方法
    }
}
