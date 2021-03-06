package com.wlgbsj.mymoblieplayer.pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wlgbsj.mymoblieplayer.R;
import com.wlgbsj.mymoblieplayer.activity.SystemVideoPlayer;
import com.wlgbsj.mymoblieplayer.adapter.VideoPagerAdapter;
import com.wlgbsj.mymoblieplayer.base.BasePager;
import com.wlgbsj.mymoblieplayer.domain.MediaItem;
import com.wlgbsj.mymoblieplayer.utils.LogUtil;

import java.util.ArrayList;


/**
 * 作者：杨光福 on 2016/7/16 11:48
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：本地视频页面
 */
public class VideoPager extends BasePager {

    private ListView listview;
    private ProgressBar pb_loading;
    private TextView tv_nomedia;

    private ArrayList<MediaItem> mediaItems;

    private VideoPagerAdapter videoPagerAdapter ;

    public VideoPager(Context context) {
        super(context);
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mediaItems != null && mediaItems.size() >0){
                //有数据
                //设置适配器
                videoPagerAdapter = new VideoPagerAdapter(context,mediaItems,true);
                listview.setAdapter(videoPagerAdapter);
                //把文本隐藏
                tv_nomedia.setVisibility(View.GONE);
            }else{
                //没有数据
                //文本显示
                tv_nomedia.setVisibility(View.VISIBLE);
            }


            //ProgressBar隐藏
            pb_loading.setVisibility(View.GONE);
        }
    };

    /**
     * 初始化当前页面的控件，由父类调用
     * @return
     */
    @Override
    public View initView() {
        View view = View.inflate(context,R.layout.video_pager,null);
        listview = (ListView) view.findViewById(R.id.listview);
        pb_loading= (ProgressBar) view.findViewById(R.id.pb_loading);
        tv_nomedia= (TextView) view.findViewById(R.id.tv_nomedia);

        listview.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              MediaItem mediaItem = mediaItems.get(position);

            Intent intent = new Intent(context,SystemVideoPlayer.class);
            //1.调起系统所有的播放-隐式意图
//            Intent intent = new Intent();
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            context.startActivity(intent);

            //2.调用自己写的播放器-显示意图
           // intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
           // context.startActivity(intent);


            //3.播放列表


            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
    }


    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地视频的数据被初始化了。。。");

        getDataFromLocal();

    }

    private void getDataFromLocal() {

        mediaItems = new ArrayList<MediaItem>();

        new Thread(){
            @Override
            public void run() {
                super.run();

                isGrantExternalRW((Activity) context);
       //         SystemClock.sleep(2000);
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] strs={
                        MediaStore.Video.Media.DISPLAY_NAME,//视频文件在sdcard的名称
                        MediaStore.Video.Media.DURATION,//视频总时长
                        MediaStore.Video.Media.SIZE,//视频的文件大小
                        MediaStore.Video.Media.DATA,//视频的绝对地址
                        MediaStore.Video.Media.ARTIST,//歌曲的演唱者
                        MediaStore.Video.Media.BUCKET_ID,

                };
/*

                String[] thumbColumns = new String[]{
                        MediaStore.Video.Thumbnails.DATA,
                        MediaStore.Video.Thumbnails.VIDEO_ID
                };
*/

                Cursor cursor = contentResolver.query(uri,strs,null,null,null);
                if(cursor!=null){
                    while(cursor.moveToNext()){
                        MediaItem mediaItem = new MediaItem();
                        //提前添加  也可以 应为后面的操作 内存里的数据会自动的发生改变
                        mediaItems.add(mediaItem);

                        String name = cursor.getString(0);
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);

                        long size  = cursor.getLong(2);
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);
                        mediaItem.setData(data);

                        String artisl = cursor.getString(4);
                        mediaItem.setArtist(artisl);

                        /*int id = cursor.getInt(5);
                        String selection = MediaStore.Video.Thumbnails.VIDEO_ID +"=?";
                        String[] selectionArgs = new String[]{
                                id+""
                        };
                        Cursor thumbCursor = contentResolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns, selection, selectionArgs, null);
                        if(thumbCursor.moveToFirst()){
                            //info.thumbPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                            String image = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                            mediaItem.setIamge(image);

                        }
*/
                    }

                    cursor.close();
                }
                handler.sendEmptyMessage(0);

            }
        }.start();

    }


    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }


}
