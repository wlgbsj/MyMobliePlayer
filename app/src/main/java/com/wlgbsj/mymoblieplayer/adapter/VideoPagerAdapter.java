package com.wlgbsj.mymoblieplayer.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wlgbsj.mymoblieplayer.R;
import com.wlgbsj.mymoblieplayer.domain.MediaItem;
import com.wlgbsj.mymoblieplayer.utils.Utils;

import java.util.ArrayList;

/**
 * Created by wlgbsj on 2017/3/9  18:41.
 */

public class VideoPagerAdapter extends BaseAdapter {

    private final boolean isVideo;
    private Context context;
    private ArrayList<MediaItem> mediaItems;

    private Utils utils;

    public VideoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems, boolean isVideo) {
        this.context = context;
        this.mediaItems = mediaItems;
        this.isVideo = isVideo;
        utils = new Utils();
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder viewHoder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_video_pager, null);
            viewHoder = new ViewHoder();
            viewHoder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHoder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHoder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHoder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);

            convertView.setTag(viewHoder);
        } else {
            viewHoder = (ViewHoder) convertView.getTag();
        }

        MediaItem mediaItem = mediaItems.get(position);

        viewHoder.tv_name.setText(mediaItem.getName());
        viewHoder.tv_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()));
        viewHoder.tv_time.setText(utils.stringForTime((int) mediaItem.getDuration()));
        viewHoder.iv_icon.setImageResource(R.drawable.video_default_icon);

        /*if(!TextUtils.isEmpty(mediaItem.getIamge())){
        viewHoder.iv_icon.setImageURI(Uri.parse(mediaItem.getIamge()));
        }else {
            viewHoder.iv_icon.setImageResource(R.drawable.video_default_icon);
            //android:src="@drawable/video_default_icon"
        }*/

        if (!isVideo) {
            viewHoder.iv_icon.setImageResource(R.drawable.music_default_bg);

        }

        return convertView;
    }

    static class ViewHoder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }

}
