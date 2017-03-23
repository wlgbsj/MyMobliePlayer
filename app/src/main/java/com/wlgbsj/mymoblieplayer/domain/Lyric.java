package com.wlgbsj.mymoblieplayer.domain;

/**
 * Created by wlgbsj on 2017/3/23  17:25.
 */
public class Lyric {
    /**
     * 歌词的内容
     */
    String content;
    /**
     * 歌词的时间戳
     */
    long TimePoint;
    /**
     * 歌词显示的高亮时间
     */
    long SleepTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return TimePoint;
    }

    public void setTimePoint(long timePoint) {
        TimePoint = timePoint;
    }

    public long getSleepTime() {
        return SleepTime;
    }

    public void setSleepTime(long sleepTime) {
        SleepTime = sleepTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", TimePoint=" + TimePoint +
                ", SleepTime=" + SleepTime +
                '}';
    }
}
