package com.lm.im_huanxin.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lm.im_huanxin.R;
import com.lm.im_huanxin.entity.ChatEntity;
import com.lm.im_huanxin.ui.widget.ControllerView;

import io.vov.vitamio.LibsChecker;


public class VideoPlayActivity extends AppCompatActivity {

    private TextView mReturn;
    private ControllerView mControillerView;
    private ChatEntity entitiy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        entitiy= (ChatEntity) getIntent().getSerializableExtra("video_info");
        initView();
        initData();
    }

    private void initView() {
        mReturn= (TextView) findViewById(R.id.video_play_return);
        mControillerView= (ControllerView) findViewById(R.id.video_play);
        mControillerView.setVideoPath(entitiy.getVideoUpEntity().getVideoPath());
        mControillerView.getVideoController().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              mControillerView.start();
                mControillerView.getVideoController().setVisibility(View.GONE);
            }
        });
    }
    private void initData()
    {

    }
}
