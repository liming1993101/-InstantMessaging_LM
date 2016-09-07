package com.lm.im_huanxin.ui.widget;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import com.lm.im_huanxin.R;
import com.lm.im_huanxin.utils.DataTypeUtils;
import com.lm.im_huanxin.utils.TimeUntil;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;


/**
 * Created by Administrator on 2016/8/8.
 */
public class ControllerView extends FrameLayout implements View.OnTouchListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    private TextView starTv, endTv;
    private ImageView videoController;
    private ImageView screenController;
    private ProgressBar bufferPb;
    private LinearLayout stateBar;
    private SeekBar videoSeekBar;
    private FrameLayout videoParent;
    private boolean VIDEO_BAR_STATE=true;

    public ImageView getScreenController() {
        return screenController;
    }

    public ImageView getVideoController() {
        return videoController;
    }

    private long duration;
    private Thread t;
    private long playCourse;
    private long bufferCourse;

    public long getPlayCourse() {
        return playCourse;
    }

    private VideoView videoView;
    private final int VIDEO_CHANGE_CURRENT=1;
    private final int VIDEO_CHANGE_BAR=2;
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case VIDEO_CHANGE_CURRENT:
                    starTv.setText(new TimeUntil().convertDate(videoView.getCurrentPosition()));
                    videoSeekBar.setProgress(DataTypeUtils.toInt(videoView.getCurrentPosition()));
                    playCourse=DataTypeUtils.toInt(videoView.getCurrentPosition());
                    break;
                case VIDEO_CHANGE_BAR:
//                    ObjectAnimator animator = ObjectAnimator.ofFloat(stateBar,"alpha",1,0);
//                    animator.setDuration(2000);
//                    animator.start();
                    stateBar.setVisibility(INVISIBLE);
                    VIDEO_BAR_STATE=false;
                    break;

            }
            super.dispatchMessage(msg);
        }
    };

    public ControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

    }


    public ControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LinearLayout getStateBar() {
        return stateBar;
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.contorller_view, null);

        addView(view);
        videoParent= (FrameLayout) view.findViewById(R.id.videoView_parent);
        videoView = (VideoView)view.findViewById(R.id.video_view5);
        starTv = (TextView) view.findViewById(R.id.star_time);
        endTv = (TextView) view.findViewById(R.id.end_time);
        stateBar= (LinearLayout)view.findViewById(R.id.state_video);
        bufferPb = (ProgressBar) view.findViewById(R.id.progress_bar);
        videoSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        videoController = (ImageView) view.findViewById(R.id.play_bt);
        screenController = (ImageView) view.findViewById(R.id.screen_bt);
        initData(context);

    }

    private void initData(Context context) {

       videoSeekBar.setOnSeekBarChangeListener(this);
        videoView.setOnTouchListener(this);
        videoView.setOnPreparedListener(this);
        videoView.setOnBufferingUpdateListener(this);
        videoView.setOnCompletionListener(this);
//        videoView.setVideoLayout(videoView.VIDEO_LAYOUT_ORIGIN,0);
        t=new Thread(){
            public void run(){
                //保存信息操作
                while (true)
                {
                    mHandler.sendEmptyMessageDelayed(VIDEO_CHANGE_CURRENT,1000);
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        mHandler.sendEmptyMessageDelayed(VIDEO_CHANGE_BAR,5000);
        videoParent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (VIDEO_BAR_STATE)
                    {
                        VIDEO_BAR_STATE=true;
                        videoView.pause();
                        videoController.setImageDrawable(getResources().getDrawable(R.mipmap.llo));
                        videoController.setVisibility(VISIBLE);
                        mHandler.sendEmptyMessageDelayed(VIDEO_CHANGE_BAR,5000);
                    }
                     else
                    {
                        VIDEO_BAR_STATE=true;
                        stateBar.setVisibility(VISIBLE);
                        mHandler.sendEmptyMessageDelayed(VIDEO_CHANGE_BAR,5000);
                    }

            }
        });
      ;
//        videoSeekBar.set
    }




    public void setVideoPath(String videoUrl) {
        videoView.setVideoURI(Uri.parse(videoUrl));
    }

    /**
     * 开始播放
     */
    public void start() {
        videoView.start();
    }

    /**
     * 暂停播放
     */

    public void pause() {
        videoView.pause();
    }

    /**
     * 释放资源
     */

    public void release() {
        videoView.stopPlayback();
    }


    public VideoView getVideoView() {
        return videoView;
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

//        int y=DataTypeUtils.toInt(videoView.getDuration());
//        float z=(videoView.getBufferPercentage()/100);
       int x=DataTypeUtils.toInt(videoView.getDuration())*(percent/100);
        int y=videoView.getBufferPercentage();
       bufferPb.setProgress(percent);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {

        videoController.setVisibility(VISIBLE);
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        videoSeekBar.setProgress(0);
        videoSeekBar.setMax(DataTypeUtils.toInt(videoView.getDuration()));
        bufferPb.setMax(DataTypeUtils.toInt(videoView.getDuration()));
//        mp.setPlaybackSpeed(1.0f);
        endTv.setText(new TimeUntil().convertDate(videoView.getDuration())+"");
        t.start();
//        videoView.pause();

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

           playCourse=i;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
          videoView.seekTo(playCourse);

    }
}
