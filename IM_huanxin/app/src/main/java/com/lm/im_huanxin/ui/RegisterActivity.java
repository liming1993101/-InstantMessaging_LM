package com.lm.im_huanxin.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;
import com.lm.im_huanxin.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEtName;
    private EditText mEtPassword;
    private Button mBtOk;
    private ProgressBar mPBOk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
        initLitener();
    }

    private void initView() {
        mEtName= (EditText) findViewById(R.id.rg_name);
        mEtPassword= (EditText) findViewById(R.id.rg_password);
        mBtOk= (Button) findViewById(R.id.rg_bt);
        mPBOk= (ProgressBar) findViewById(R.id.rg_pb);

    }
    private void initLitener()
    {
        mBtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EMClient.getInstance().createAccount(mEtName.getText().toString(), mEtPassword.getText().toString());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });


        //注册失败会抛出HyphenateException



    }



}
