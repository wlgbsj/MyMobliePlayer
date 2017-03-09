package com.wlgbsj.mymoblieplayer.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.wlgbsj.mymoblieplayer.base.BasePager;
import com.wlgbsj.mymoblieplayer.utils.LogUtil;


/**
 * 作者：杨光福 on 2016/7/16 11:48
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：网络音频页面
 */
public class NetAudioPager extends BasePager {

    private TextView textView;

    public NetAudioPager(Context context) {
        super(context);
    }

    /**
     * 初始化当前页面的控件，由父类调用
     * @return
     */
    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setTextSize(30);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }


    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络音频的数据被初始化了。。。");
        //联网
        //音频内容
        textView.setText("网络音频的内容");
    }
}
