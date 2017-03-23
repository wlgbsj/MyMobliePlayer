package com.wlgbsj.mymoblieplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wlgbsj.mymoblieplayer.domain.Lyric;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlgbsj on 2017/3/23  17:23.
 */

public class ShowLyricView extends TextView {

    private List<Lyric> lyrics;
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

    private float textHeight = 50;
    private long currentTime;
    private long timePoint;
    private long sleepTime;


    public ShowLyricView(Context context) {
        this(context, null);
    }

    public ShowLyricView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowLyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void initView() {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setTextSize(50);
        //设置居中对齐
        paint.setTextAlign(Paint.Align.CENTER);

        whitepaint = new Paint();
        whitepaint.setColor(Color.WHITE);
        whitepaint.setAntiAlias(true);
        whitepaint.setTextSize(50);
        //设置居中对齐
        whitepaint.setTextAlign(Paint.Align.CENTER);


        lyrics = new ArrayList<Lyric>();

        for (int i = 0; i < 1000l; i++) {
            Lyric lyric = new Lyric();
            lyric.setContent(i + "aaaaaaaaa" + i);
            lyric.setSleepTime(1500 * i);
            lyric.setTimePoint(1000 * i);
            lyrics.add(lyric);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lyrics != null && lyrics.size() > 0) {
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

    public void showNextLyric(int currentPosition) {
        this.currentTime = currentPosition;

        if (lyrics == null) {
            return;
        }

        //就要得到当前高亮显示的这个歌词的索引 index timePoint 和sleepTime
        for (int i = 1; i < lyrics.size(); i++) {
            //找出符合高亮显示的居中
            if (currentTime < lyrics.get(i).getTimePoint()) {
                int tempindex = i - 1;
                //找到马上就要高亮显示的具体的某一条句子
                if (currentTime >= lyrics.get(tempindex).getTimePoint()) {
                    index = tempindex;
                    timePoint = lyrics.get(tempindex).getTimePoint();
                    sleepTime = lyrics.get(tempindex).getSleepTime();
                }


            }
        }
        //invalidate()导致onDraw方法执行
        invalidate();

    }
}
