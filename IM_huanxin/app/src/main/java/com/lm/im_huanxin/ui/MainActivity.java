package com.lm.im_huanxin.ui;

import android.content.Intent;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Bundle;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.lm.im_huanxin.R;
import com.lm.im_huanxin.RxBus.RxBus;
import com.lm.im_huanxin.entity.ChatEntity;
import com.lm.im_huanxin.service.GetMsgService;
import com.lm.im_huanxin.ui.fragment.ContactsFragment;
import com.lm.im_huanxin.ui.fragment.MessageFragment;
import com.lm.im_huanxin.ui.fragment.OptionsFragment;
import com.lm.im_huanxin.ui.widget.TabFragmentHost;

import java.util.List;

public class MainActivity extends BaseActivity {


    private RxBus rxBus = RxBus.getInstance();

    public TabFragmentHost mTabHost;
    // 标签
    private String[] TabTag = { "tab1", "tab2", "tab3" };
    // 自定义tab布局显示文本和顶部的图片
    private Integer[] ImgTab = { R.layout.tab_main_message,
            R.layout.tab_main_contacts, R.layout.tab_main_options };

    // tab 选中的activity
    private Class[] ClassTab = { MessageFragment.class, ContactsFragment.class,
            OptionsFragment.class };

    // tab选中背景 drawable 样式图片 背景都是同一个,背景颜色都是 白色。。。
    private Integer[] StyleTab = { R.color.gray, R.color.gray, R.color.gray,
            R.color.white };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();
        InitTabView();
    }


    private void initView() {

        // 实例化framentTabHost
        mTabHost = (TabFragmentHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(),
                android.R.id.tabcontent);

    }


    private void InitTabView() {

        // 可以传递参数 b;传递公共的userid,version,sid
        Bundle b = new Bundle();
        // 循环加入自定义的tab
        for (int i = 0; i < TabTag.length; i++) {
            // 封装的自定义的tab的样式
            View indicator = getIndicatorView(i);
            mTabHost.addTab(
                    mTabHost.newTabSpec(TabTag[i]).setIndicator(indicator),
                    ClassTab[i], b);// 传递公共参数

        }
        mTabHost.getTabWidget().setDividerDrawable(R.color.white);
    }

    // 设置tab自定义样式:注意 每一个tab xml子布局的linearlayout 的id必须一样
    private View getIndicatorView(int i) {
        // 找到tabhost的子tab的布局视图
        View v = getLayoutInflater().inflate(ImgTab[i], null);
        LinearLayout tv_lay = (LinearLayout) v.findViewById(R.id.layout_back);
        tv_lay.setBackgroundResource(StyleTab[i]);

        return v;
    }
    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
        
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    @Override
    public void getMessage(ChatEntity entity) {
        rxBus.post("msg", new String("hello rxbus"));
    }

    @Override
    public void getInviterMsg() {
            rxBus.post("msg", new String("hello rxbus"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rxBus.release();
    }
}
