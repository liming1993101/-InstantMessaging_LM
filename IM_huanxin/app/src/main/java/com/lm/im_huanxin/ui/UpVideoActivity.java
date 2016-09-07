package com.lm.im_huanxin.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lm.im_huanxin.R;
import com.lm.im_huanxin.RxBus.RxBus;
import com.lm.im_huanxin.adapter.VideoUpAdatper;
import com.lm.im_huanxin.entity.VideoUpEntity;
import com.lm.im_huanxin.utils.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpVideoActivity extends AppCompatActivity implements View.OnClickListener,VideoUpAdatper.OnSelectContactsListener {

    private RxBus rxBus = RxBus.getInstance();
    private TextView mReturn;
    private TextView mUpVideo;
    private RecyclerView mRecyclerView;
    private VideoUpAdatper mAdapter;
    private String toUser;
    private VideoUpEntity videoUpEntity;
    private List<VideoUpEntity>listVideo=new ArrayList<VideoUpEntity>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_video);
        initView();
        initData();
    }

    private void initView()
    {
        mReturn= (TextView) findViewById(R.id.up_video_return);
        mUpVideo= (TextView) findViewById(R.id.up_video_tv);
        mRecyclerView= (RecyclerView) findViewById(R.id.up_video_rv);
        mReturn.setOnClickListener(this);
        mUpVideo.setOnClickListener(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mAdapter=new VideoUpAdatper(this,listVideo);
        mAdapter.setOnItemSelectedListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }
    private void initData() {
        String progress[]={

                MediaStore.Video.Media.DISPLAY_NAME,//视频的名字
                MediaStore.Video.Media.SIZE,//大小
                MediaStore.Video.Media.DURATION,//长度
                MediaStore.Video.Media.DATA,//播放地址
        };

//获取数据提供者,this是上下文
        ContentResolver cr = this.getContentResolver();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
//有sd卡的情况
            Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,progress,null,null,null);
            while(cursor.moveToNext()){
// 到视频文件的信息
                String name = cursor.getString(0);//得到视频的名字
                long size = cursor.getLong(1);//得到视频的大小
                long durantion = cursor.getLong(2);//得到视频的时间长度
                String data = cursor.getString(3);//得到视频的路径，可以转化为uri进行视频播放
//使用静态方法获取视频的缩略图
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(data, MediaStore.Video.Thumbnails.MINI_KIND);
                String thumUri;
                if (thumbnail==null) {
                    Bitmap noneImg = BitmapFactory.decodeResource(getResources(), R.mipmap.default_error);
                    thumUri = saveMyBitmap(name, noneImg);
                }
                else
                {
                    thumUri = saveMyBitmap(name, thumbnail);
                }
                VideoUpEntity entity = new VideoUpEntity();
//创建视频信息对象
                entity.setVideoName(name);
                entity.setVideoSize(size);
                entity.setMark(false);
                entity.setVideoDuration(durantion);
                entity.setVideoPath(data);
                entity.setVideoThumImage(thumUri);
                listVideo.add(entity);
            }
        }
//不论是否有sd卡都要查询手机内存
        Cursor cursor = cr.query(MediaStore.Video.Media.INTERNAL_CONTENT_URI,progress,null,null,null);
        while(cursor.moveToNext()) {
// 到视频文件的信息
            String name = cursor.getString(0);//得到视频的名字
            long size = cursor.getLong(1);//得到视频的大小
            long durantion = cursor.getLong(2);//得到视频的时间长度
            String data = cursor.getString(3);//得到视频的路径，可以转化为uri进行视频播放
//使用静态方法获取视频的缩略图
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(data, MediaStore.Video.Thumbnails.MINI_KIND);
            String thumUri;
            if (thumbnail==null) {
                Bitmap noneImg = BitmapFactory.decodeResource(getResources(), R.mipmap.default_error);
                thumUri = saveMyBitmap(name, noneImg);
            }
            else
            {
                thumUri = saveMyBitmap(name, thumbnail);
            }

            VideoUpEntity entity = new VideoUpEntity();
//创建视频信息对象
            entity.setVideoName(name);
            entity.setVideoSize(size);
            entity.setVideoDuration(durantion);
            entity.setMark(false);
            entity.setVideoPath(data);
            entity.setVideoThumImage(thumUri);
            listVideo.add(entity);
        }
        mAdapter.setListVideo(listVideo);
        mAdapter.notifyDataSetChanged();
    }


    public String saveMyBitmap(String bitName,Bitmap mBitmap) {
        File f = new File("/sdcard/im_huanxin" + bitName + ".png");
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block

        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f.toString();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.up_video_return:
                finish();
                break;
            case R.id.up_video_tv:

                if (videoUpEntity==null) {
                    new ToastUtil(this).longToast("你还没有选择你要发送的视频");
                }
                else {
                    Intent intent=new Intent();
                    intent.putExtra("video",videoUpEntity);
                    this.setResult(Activity.RESULT_OK,intent);
                    finish();
                }
                break;
        }
    }

    @Override
    public void selectContacts(int position) {
        for (int i=0;i<listVideo.size();i++)
        {
            listVideo.get(i).setMark(false);
        }
        listVideo.get(position).setMark(true);
        videoUpEntity= listVideo.get(position);
        mAdapter.setListVideo(listVideo);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rxBus.release();
    }
}
