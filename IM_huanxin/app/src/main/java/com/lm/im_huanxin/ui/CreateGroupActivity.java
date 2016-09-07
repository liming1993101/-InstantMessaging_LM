package com.lm.im_huanxin.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;
import com.lm.im_huanxin.R;
import com.lm.im_huanxin.utils.ToastUtil;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mGroupName;
    private EditText mGropuDesc;
    private ImageView mPbMark;
    private ImageView mPxMark;
    private TextView mReturn;
    private TextView mOK;
    private TextView mGroupSet;
    private boolean PB=false;
    private boolean PX=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initView();
    }
    private void initView() {
        mGropuDesc= (EditText) findViewById(R.id.create_group_desc);
        mGroupName= (EditText) findViewById(R.id.create_group_namt);
        mPbMark= (ImageView) findViewById(R.id.create_group_pb);
        mPxMark= (ImageView) findViewById(R.id.create_group_qx);
        mReturn= (TextView) findViewById(R.id.create_group_return);
        mOK= (TextView) findViewById(R.id.create_group_ok);
        mGroupSet= (TextView) findViewById(R.id.grooup_set_tv);
        mOK.setOnClickListener(this);
        mPbMark.setOnClickListener(this);
        mReturn.setOnClickListener(this);
        mPxMark.setOnClickListener(this);
    }

    private void initListener() {
        EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
        option.maxUsers = 200;
        if (PB) {
            if(PX)
            {
                option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
            }
            else option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;

        }
        else {
            if(PX)
            {
                option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
            }
            else option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
        }
        String b[]={"xiaoming"};
        try {
            EMClient.getInstance().groupManager().createGroup(mGroupName.getText().toString(),mGropuDesc.getText().toString(),b, "你渴望力量吗？", option);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.create_group_pb:
                if (!PB)
                {
                    mPbMark.setImageResource(R.mipmap.btn_selected);
                    mGroupSet.setText("不受控的公开群");
                    PB=true;
                }
                else {
                    mPbMark.setImageResource(R.mipmap.btn_unselected);
                    mGroupSet.setText("开发群成员邀请");
                    PB=false;
                }
                break;
            case R.id.create_group_qx:
                if (!PX)
                {
                    mPxMark.setImageResource(R.mipmap.btn_unselected);
                    PX=true;
                }
                else
                {
                    mPxMark.setImageResource(R.mipmap.btn_selected);
                    PX=false;
                }
                break;
            case R.id.create_group_ok:
                if (mGroupName.getText().toString().length()==0||mGropuDesc.getText().toString().length()==0)
                {
                    new ToastUtil(this).longToast("群组名或群描述不能为空");
                }
                else {
                    initListener();
                    finish();
                }
                break;

        }

    }
}
