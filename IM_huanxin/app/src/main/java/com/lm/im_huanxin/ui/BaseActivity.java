package com.lm.im_huanxin.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;

import com.lm.im_huanxin.entity.ChatEntity;
import com.lm.im_huanxin.utils.Contstants;

/**
 * Created by Administrator on 2016/8/16.
 */
public abstract class BaseActivity extends AppCompatActivity {



    /**
     * 广播接收者，接收GetMsgService发送过来的消息
     */
    private BroadcastReceiver MsgReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if ((ChatEntity) intent.getSerializableExtra(Contstants.MSGKEY)!=null) {
                ChatEntity entity = (ChatEntity) intent.getSerializableExtra(Contstants.MSGKEY);
                getMessage(entity);
            }
            else
            {
                getInviterMsg();
            }

        }
    };

    /**
         * 抽象方法，用于子类处理消息，
         *
         * @param msg 传递给子类的消息对象
         */
        public abstract void getMessage(ChatEntity entity);
        public abstract void getInviterMsg();
        /**
         * 子类直接调用这个方法关闭应用
         */
        public void close() {
            Intent i = new Intent();
            i.setAction(Contstants.ACTION);
            sendBroadcast(i);
            finish();
        }

    @Override
        public void onStart() {// 在start方法中注册广播接收者
            super.onStart();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Contstants.ACTION);
            registerReceiver(MsgReceiver, intentFilter);


        }

        @Override
        protected void onStop() {// 在stop方法中注销广播接收者
            super.onStop();
            unregisterReceiver(MsgReceiver);// 注销接受消息广播
        }
    }
