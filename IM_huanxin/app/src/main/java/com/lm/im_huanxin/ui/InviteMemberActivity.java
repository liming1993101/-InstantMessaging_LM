package com.lm.im_huanxin.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lm.im_huanxin.R;
import com.lm.im_huanxin.utils.Contstants;
import com.lm.im_huanxin.utils.ToastUtil;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InviteMemberActivity extends AppCompatActivity implements View.OnClickListener{

    private int INVITE_TYPE=1;
    private TextView mReturn;
    private EditText mEditText;
    private Button mBtInvite;
    private boolean VALID;
    private String groupID;
    private EditText mReson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_member);
        INVITE_TYPE=getIntent().getIntExtra("invite_type",1);
        VALID=getIntent().getBooleanExtra("pb",false);
        groupID=getIntent().getStringExtra("groupId");
        initView();
    }

    private void initView() {
        mReturn= (TextView) findViewById(R.id.invite_member_return);
        mBtInvite= (Button) findViewById(R.id.invite_member_bt);
        mEditText= (EditText) findViewById(R.id.invite_member_et);
        mReson= (EditText) findViewById(R.id.invite_member_reson);
        mBtInvite.setOnClickListener(this);
        mReturn.setOnClickListener(this);
        if (INVITE_TYPE==1)
        {
            mReson.setVisibility(View.VISIBLE);
        }

    }

    private void initData() {

        Observable.create(new Observable.OnSubscribe<Object>() {

            @Override
            public void call(Subscriber subscriber) {
             if (INVITE_TYPE==Contstants.INVITE_MEMBER)
             {
                 if (VALID)
                 {
                     //群主加人调用此方法
                     try {
                         EMClient.getInstance().groupManager().addUsersToGroup(groupID, new String[]{mEditText.getText().toString()});
                     } catch (HyphenateException e) {
                         e.printStackTrace();
                     }
                 }
                 else
                 {
                     try {
                         EMClient.getInstance().groupManager().inviteUser(groupID, new String[]{mEditText.getText().toString()}, null);
                     } catch (HyphenateException e) {
                         e.printStackTrace();
                     }
                 }
             }
                else
             {
                 try {
                     EMClient.getInstance().contactManager().addContact(mEditText.getText().toString(), mReson.getText().toString());
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
                        new ToastUtil(InviteMemberActivity.this).longToast("你的邀请消息已发送");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.invite_member_return:
                finish();
                break;
            case R.id.invite_member_bt:
                if (mEditText.getText().toString().length()!=0) {
                    initData();
                }
                else new ToastUtil(this).longToast("哥你至少写点东西啊");
                break;
        }
    }
}
