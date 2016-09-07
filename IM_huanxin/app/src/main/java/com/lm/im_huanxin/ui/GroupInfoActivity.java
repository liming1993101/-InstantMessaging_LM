package com.lm.im_huanxin.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.lm.im_huanxin.R;
import com.lm.im_huanxin.entity.GroupInfoEntity;
import com.lm.im_huanxin.utils.Contstants;
import com.lm.im_huanxin.utils.SharePreferenceUtil;
import com.lm.im_huanxin.utils.ToastUtil;

import java.util.List;
import java.util.Objects;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mGroupID;
    private TextView mGroupName;
    private TextView mGroupReturn;
    private LinearLayout mGroupMember;
    private LinearLayout mGroupBlack;
    private TextView mGroupDesc;
    private ImageView mGroupShielding;
    private Button mBtSendMsg;
    private TextView mGroupMemberCount;
    private Button mBtSetUp;
    private GroupInfoEntity mGroupInfo;
    private Button mBtInvite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        mGroupInfo= (GroupInfoEntity) getIntent().getSerializableExtra("group_info");
        initView();
        initData();
    }

    private void initView() {
        mGroupID= (TextView) findViewById(R.id.group_info_id_tv);
        mGroupName= (TextView) findViewById(R.id.group_info_name);
        mGroupReturn= (TextView) findViewById(R.id.grooup_info_return);
        mGroupMember= (LinearLayout) findViewById(R.id.group_info_members);
        mGroupBlack= (LinearLayout) findViewById(R.id.group_info_black);
        mGroupDesc= (TextView) findViewById(R.id.group_info_desc);
        mGroupShielding= (ImageView) findViewById(R.id.group_info_shielding);
        mBtSendMsg= (Button) findViewById(R.id.group_info_send_msg);
        mBtSetUp= (Button) findViewById(R.id.group_info_set);
        mGroupMemberCount= (TextView) findViewById(R.id.group_info_members_tv);
        mBtInvite= (Button) findViewById(R.id.group_info_invite);
        mBtInvite.setOnClickListener(this);
        mBtSetUp.setOnClickListener(this);
        mBtSendMsg.setOnClickListener(this);
        mGroupReturn.setOnClickListener(this);
        mGroupMember.setOnClickListener(this);
        mGroupBlack.setOnClickListener(this);
    }

    private void initData() {
        mGroupName.setText(mGroupInfo.getGroupName()+"");
        mGroupDesc.setText(mGroupInfo.getGroupDesc());
        mGroupID.setText(mGroupInfo.getGroupID());
        mGroupMemberCount.setText(mGroupInfo.getGroupMemberCount()+"人");
        if (new SharePreferenceUtil(this,Contstants.SAVE_USER).getUser().toLowerCase().equals(mGroupInfo.getGroupOwner()))
        {
            mBtSetUp.setText("解散该群");
        }
        else
        {
            mBtSetUp.setText("退出该群");
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.group_info_send_msg:
                startActivity(new Intent(this,ChatActivity.class).putExtra("chat_model",
                        Contstants.GROUP_CHAT).putExtra("contacts",mGroupInfo.getGroupName())
                        .putExtra("groupID",mGroupInfo.getGroupID()));
                break;
            case R.id.group_info_set:
                initThread();
                finish();
                break;
            case R.id.grooup_info_return:
                finish();
                break;
            case R.id.group_info_members:
                break;
            case R.id.group_info_black:
                break;
            case R.id.group_info_shielding:
                break;
            case R.id.group_info_invite:
                boolean state=false;
                if (new SharePreferenceUtil(GroupInfoActivity.this,Contstants.SAVE_USER).getUser().equals(mGroupInfo.getGroupOwner()))
                {
                    state=true;
                }
                startActivity(new Intent(this,InviteMemberActivity.class).
                        putExtra("invite_type",Contstants.INVITE_MEMBER).putExtra("pb",true).
                        putExtra("groupId",mGroupID.getText().toString()));
                break;
        }
    }

    private void initThread() {

        Observable.create(new Observable.OnSubscribe<Object>() {

            @Override
            public void call(Subscriber subscriber) {
              if (new SharePreferenceUtil(GroupInfoActivity.this,Contstants.SAVE_USER).getUser().equals(mGroupInfo.getGroupOwner()))
              {

                  try {
                      EMClient.getInstance().groupManager().destroyGroup(mGroupID.getText().toString());
                  } catch (HyphenateException e) {
                      e.printStackTrace();
                  }
              }
                else
              {
                  try {
                      EMClient.getInstance().groupManager().leaveGroup(mGroupID.getText().toString());
                  } catch (HyphenateException e) {
                      e.printStackTrace();
                  }
              }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer() {

                    @Override
                    public void onCompleted() {

                        String info="你已退出该群";
                        if (new SharePreferenceUtil(GroupInfoActivity.this,Contstants.SAVE_USER).getUser().equals(mGroupInfo.getGroupOwner())) {
                              info="你已解散该群";
                        }
                        new ToastUtil(GroupInfoActivity.this).longToast(info);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });

    }

}
