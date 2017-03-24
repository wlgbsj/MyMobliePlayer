package com.wlgbsj.mymoblieplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wlgbsj.mymoblieplayer.domain.Lyric;
import com.wlgbsj.mymoblieplayer.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlgbsj on 2017/3/23  17:23.
 */

public class ShowLyricView extends TextView {

    private List<Lyric> lyrics  ;
    private Paint paint;
    private Paint whitepaint;


    /**
     * 控件的宽
     */
    private float width;
    /**
     * 控件的高
     */
    private float height;

    /**
     * 第几句歌词-歌词序列号
     */
    private int index = 0;

    /**
     * 每行的高度
     */

    private float textHeight ;
    private float currentPositon;
    private float timePoint;
    private float sleepTime;


    public ShowLyricView(Context context) {
        this(context, null);
    }

    public ShowLyricView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowLyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;//获取 view的宽高
        height = h;
    }

    private void initView(Context context) {
        textHeight = DensityUtil.dip2px(context,17);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setTextSize(DensityUtil.dip2px(context,16));
        //设置居中对齐
        paint.setTextAlign(Paint.Align.CENTER);

        whitepaint = new Paint();
        whitepaint.setColor(Color.WHITE);
        whitepaint.setAntiAlias(true);
        whitepaint.setTextSize(DensityUtil.dip2px(context,16));
        //设置居中对齐
        whitepaint.setTextAlign(Paint.Align.CENTER);


       // lyrics = new ArrayList<Lyric>();

        /*for (int i = 0; i < 1000l; i++) {
            Lyric lyric = new Lyric();
            lyric.setContent(i + "aaaaaaaaa" + i);
            lyric.setSleepTime(1500 * i);
            lyric.setTimePoint(1000 * i);
            lyrics.add(lyric);
        }*/
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lyrics != null && lyrics.size() > 0) {

            //往上推移  添加平移动画 注意这几个值应为float，变为很细微

            float plush = 0;
            if(sleepTime ==0){
                plush = 0;
            }else{
                //平移
                //这一句所花的时间 ：休眠时间 = 移动的距离 ： 总距离（行高）
                //移动的距离 =  (这一句所花的时间 ：休眠时间)* 总距离（行高）
//                float delta = ((currentPositon-timePoint)/sleepTime )*textHeight;

                //屏幕的的坐标 = 行高 + 移动的距离
                plush = textHeight + ((currentPositon-timePoint)/sleepTime )*textHeight;
            }
            canvas.translate(0,-plush);



            //当前句
            //歌词内容
            String currentContent = lyrics.get(index).getContent();
            canvas.drawText(currentContent, width / 2, height / 2, paint);

            float tempY = height / 2;

            //前一句
            for (int i = index - 1; i >= 0; i--) {
                String preContent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, whitepaint);
            }

            //后一句
            tempY = height / 2;
            for (int i = index + 1; i < lyrics.size(); i++) {
                tempY = tempY + textHeight;
                String nextContent = lyrics.get(i).getContent();
                if (tempY > height) {
                    break;
                }
                canvas.drawText(nextContent, width / 2, tempY, whitepaint);
            }
        } else {

            canvas.drawText("没有歌词...", width / 2, height / 2, paint);
        }
    }

    //通过当前的时间 去改变 歌词里表中歌词的索引位置
    public void showNextLyric(int currentPosition) {
        this.currentPositon = currentPosition;

        if (lyrics == null) {
            return;
        }

        //就要得到当前高亮显示的这个歌词的索引 index timePoint 和sleepTime
        for (int i = 1; i < lyrics.size(); i++) {
            //找出符合高亮显示的居中
            if (currentPositon < lyrics.get(i).getTimePoint()) {
                int tempindex = i - 1;
                //找到马上就要高亮显示的具体的某一条句子
                if (currentPositon >= lyrics.get(tempindex).getTimePoint()) {
                    index = tempindex;
                    timePoint = lyrics.get(tempindex).getTimePoint();
                    sleepTime = lyrics.get(tempindex).getSleepTime();
                }


            }
        }
        //invalidate()导致onDraw方法执行
        invalidate();

    }

    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }
}
