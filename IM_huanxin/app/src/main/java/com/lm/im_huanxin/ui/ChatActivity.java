package com.lm.im_huanxin.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.adapter.EMAConversation;
import com.lm.im_huanxin.R;
import com.lm.im_huanxin.RxBus.RxBus;
import com.lm.im_huanxin.RxBus.RxBusResult;
import com.lm.im_huanxin.adapter.ChatAdapter;
import com.lm.im_huanxin.emoji.DisplayRules;
import com.lm.im_huanxin.emoji.Image;
import com.lm.im_huanxin.entity.ChatEntity;
import com.lm.im_huanxin.entity.Emojicon;
import com.lm.im_huanxin.entity.Faceicon;
import com.lm.im_huanxin.entity.MessageListEntity;
import com.lm.im_huanxin.entity.VideoUpEntity;
import com.lm.im_huanxin.ui.fragment.OnOperationListener;
import com.lm.im_huanxin.ui.fragment.PhotoAlbumFragment;
import com.lm.im_huanxin.ui.widget.KJChatKeyboard;
import com.lm.im_huanxin.utils.Contstants;
import com.lm.im_huanxin.utils.FileUtils;
import com.lm.im_huanxin.utils.RecentContactsDB;
import com.lm.im_huanxin.utils.SharePreferenceUtil;
import com.lm.im_huanxin.utils.TimeUtil;
import com.lm.im_huanxin.utils.ToastUtil;

import org.kymjs.kjframe.ui.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0x1;
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_VIDEO=200;
    private RxBus rxBus = RxBus.getInstance();
    private String contacts;
    private TextView mReturn;
    private TextView mContacts;
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private List<ChatEntity>listItems=new ArrayList<ChatEntity>();
    private List<ChatEntity>listLog=new ArrayList<>();
    private int MSG_TYPE;
    private int LogPage=1;
    private File mTmpFile;
    private int CHAT_MODEL=1;
    private VideoUpEntity videoUpEntity;
    private List<Image>listSend=new ArrayList<Image>();
    private KJChatKeyboard box;
    private LinearLayout addLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String groupID;

    public void setListItems(List<ChatEntity> listItems) {
        this.listItems = listItems;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        CHAT_MODEL=getIntent().getIntExtra("chat_model",1);
        contacts=getIntent().getStringExtra("contacts");
        groupID=getIntent().getStringExtra("groupID");
        initView();
        initData();
        initBox();
        initRxBus();

    }

    private void initRxBus() {

        rxBus.toObserverableOnMainThread("photo_send", new RxBusResult() {
            @Override
            public void onRxBusResult(Object o) {


                listSend= (List<Image>) o;
                senMsg(Contstants.MSG_IMG);
//                mtvmsg.setText("second收到消息;" + msg);
//                Toast.makeText(MainActivity.this, "second收到消息;" + msg, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void initView() {
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.chat_log_refresh);
        mReturn= (TextView) findViewById(R.id.chat_return);
        mContacts= (TextView) findViewById(R.id.chat_contacts_tv);
        box= (KJChatKeyboard) findViewById(R.id.chat_msg_box);

        mRecyclerView= (RecyclerView) findViewById(R.id.chat_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReturn.setOnClickListener(this);
        mContacts.setText(contacts+"");

        mSwipeRefreshLayout.setOnRefreshListener(this);

    }

    private void initData()
    {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(contacts);

////获取此会话的所有消息
//        List<EMMessage> messages = conversation.getAllMessages();
////SDK初始化加载的聊天记录为20条，到顶时需要去DB里获取更多
//获取startMsgId之前的pagesize条消息，此方法获取的messages SDK会自动存入到此会话中，APP中无需再次把获取到的messages添加到会话中
        if (conversation!=null) {  //判断是否有该好友的聊天记录
            conversation.markAllMessagesAsRead();
            RecentContactsDB db=new RecentContactsDB(ChatActivity.this);
            db.reastMsg(contacts);
            db.colse();
            int a=conversation.getAllMessages().size();
            List<EMMessage> messages=new ArrayList<EMMessage>();
            if(a!=1) //判断消息记录数量是否只有一条 防止因为获取不到Id报错
            {
                String msgId="";
                if (a<16) {
                    msgId = conversation.getAllMessages().get(a-1).getMsgId();
                }
                else
                {
                    msgId = conversation.getAllMessages().get(a-1).getMsgId();
                }
                messages = conversation.loadMoreMsgFromDB(msgId, 15);

            }
            EMMessage messge = conversation.getLastMessage(); //获取最后一条信息
            messages.add(messge);
            listItems=getMsgLog(messages, conversation);
        }
        mAdapter=new ChatAdapter(this,listItems);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        manager.scrollToPosition(listItems.size()-1);
    }

    private void  initBox()
    {
        box.setOnOperationListener(new OnOperationListener() {
            @Override
            public void send(String content) {
//                org.kymjs.chat.bean.Message message = new org.kymjs.chat.bean.Message(org.kymjs.chat.bean.Message.MSG_TYPE_TEXT, org.kymjs.chat.bean.Message.MSG_STATE_SUCCESS,
//                        "Tom", "avatar", "Jerry",
//                        "avatar", content, true, true, new Date());
////                datas.add(message);
//                adapter.refresh(datas);
//                createReplayMsg(message);
                senMsg(Contstants.MSG_TEXT);
            }

            @Override
            public void selectedFace(Faceicon content) {
//                org.kymjs.chat.bean.Message message = new org.kymjs.chat.bean.Message(org.kymjs.chat.bean.Message.MSG_TYPE_FACE, org.kymjs.chat.bean.Message.MSG_STATE_SUCCESS,
//                        "Tom", "avatar", "Jerry", "avatar", content.getPath(), true, true, new
//                        Date());
//                datas.add(message);
//                adapter.refresh(datas);
//                createReplayMsg(message);
            }

            @Override
            public void selectedEmoji(Emojicon emoji) {
                box.getEditTextBox().append(emoji.getValue());
            }

            @Override
            public void selectedBackSpace(Emojicon back) {
                DisplayRules.backspace(box.getEditTextBox());
            }

            @Override
            public void selectedFunction(int index) {
                switch (index) {
                    case 0:


                        break;
                    case 1:
                        goToAlbum();
                        break;
                    case 3:
                        startActivityForResult(new Intent(ChatActivity.this,UpVideoActivity.class),REQUEST_VIDEO);
                        break;
                }
            }
        });

    }


    private void goToAlbum() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(this.getPackageManager()) != null){
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = FileUtils.createTmpFile(this);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }else{
            Toast.makeText(this,"没找到相机", Toast.LENGTH_SHORT).show();
        }

    }



    private List<ChatEntity> getMsgLog(List<EMMessage> messages, EMConversation conversation) {
        List<ChatEntity> list = new ArrayList<ChatEntity>();
        for (int i = 0; i < messages.size(); i++) {
            String msgUrl = "";
            String msg = "";
            ChatEntity chatEntity = new ChatEntity();
            EMClient.getInstance().chatManager().importMessages(messages);
            if (messages.get(i).getType() == EMMessage.Type.TXT) {//根据消息类型来获得不同数据的类型，该类型为文本
                EMTextMessageBody messageBody = (EMTextMessageBody) messages.get(i).getBody();
                msg = messageBody.getMessage();
                MSG_TYPE = 1;
                chatEntity.setMsgType(MSG_TYPE);

            } else if (messages.get(i).getType() == EMMessage.Type.IMAGE) {//消息类型图片
                EMImageMessageBody imageMessageBody = (EMImageMessageBody) messages.get(i).getBody();
                if (messages.get(i).getFrom().equals(contacts)) {
                    msgUrl = imageMessageBody.getThumbnailUrl().toString();
                }
                else
                {
                    msgUrl = imageMessageBody.getRemoteUrl().toString();
                }
                MSG_TYPE = 2;
                chatEntity.setMsgType(MSG_TYPE);

            }
            else if (messages.get(i).getType() == EMMessage.Type.VIDEO)
            {
                EMVideoMessageBody videoMessageBody= (EMVideoMessageBody) messages.get(i).getBody();
                VideoUpEntity entity=new VideoUpEntity();

                if (videoMessageBody.getRemoteUrl().equals(""))
                {
                    entity.setLocal(true);
                    entity.setVideoPath(videoMessageBody.getLocalUrl());
                    entity.setVideoThumImage(videoMessageBody.getLocalThumb());
                }
                else
                {
                    entity.setLocal(false);
                    entity.setVideoPath(videoMessageBody.getRemoteUrl());
                    entity.setVideoThumImage(videoMessageBody.getThumbnailUrl().toString());
                }

                entity.setVideoDuration(videoMessageBody.getDuration());
                entity.setVideoName(videoMessageBody.getFileName());
                chatEntity.setVideoUpEntity(entity);
                MSG_TYPE=Contstants.MSG_VIDEO;
                chatEntity.setMsgType(MSG_TYPE);
            }

            String formUser = messages.get(i).getFrom();
            String userName = messages.get(i).getUserName();
            chatEntity.setToUser(formUser);
            int msgCount = conversation.getUnreadMsgCount();
            long time = messages.get(i).getMsgTime();
            chatEntity.setMsg(msg);
            chatEntity.setId(messages.get(i).getMsgId());
            chatEntity.setImgurl(msgUrl);
            chatEntity.setUser(formUser);
            String my=new SharePreferenceUtil(this,Contstants.SAVE_USER).getUser();
            if (!formUser.equals(my.toLowerCase())) {
                chatEntity.setMsgSource(2);
            } else {
                chatEntity.setMsgSource(1);
            }

            list.add(chatEntity);
        }

        return list;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId())
          {
//            case R.id.chat_send:
//                if (mEtMsg.getText().toString().length()!=0)
//                {
//                    senMsg();
//                    mEtMsg.setText("");
//                }
//
//                break;
            case R.id.chat_return:
                finish();
                break;
//            case R.id.add_mode:
//
//                if (addLayout.getVisibility()==View.VISIBLE)
//                {
//                    addLayout.setVisibility(View.GONE);
//                }
//                else {
//                    addLayout.setVisibility(View.VISIBLE);
//                }
//
//
//                break;
//            case R.id.chat_recyclerview:
//
//                break;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    private void senMsg(int msg_type) {
        EMMessage message;
        if (msg_type == Contstants.MSG_TEXT) {

            ChatEntity chat=new ChatEntity();
            MessageListEntity entity=new MessageListEntity();
            if (CHAT_MODEL == Contstants.GROUP_CHAT){
                message= EMMessage.createTxtSendMessage(box.getEditTextBox().getText().toString() + "", groupID);
                message.setChatType(EMMessage.ChatType.GroupChat);
                chat.setChatType(Contstants.GROUP_CHAT);
                entity.setChatType(Contstants.GROUP_CHAT);
            }
            else {
                message = EMMessage.createTxtSendMessage(box.getEditTextBox().getText().toString() + "", contacts);
                chat.setChatType(Contstants.GROUP_SGINGLE);
                entity.setChatType(Contstants.GROUP_SGINGLE);
            }
            EMClient.getInstance().chatManager().sendMessage(message);
            List<EMMessage> list = new ArrayList<EMMessage>();
            list.add(message);
            EMClient.getInstance().chatManager().importMessages(list);
            chat.setMsg(box.getEditTextBox().getText().toString()+"");
            chat.setUser("my");
            chat.setToUser(contacts);
            chat.setMsgSource(1);
            chat.setMsgType(Contstants.MSG_TEXT);
            listItems.add(chat);
            mAdapter.setListItems(listItems);
            mAdapter.notifyDataSetChanged();
            LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            manager.scrollToPosition(listItems.size()-1);
            TimeUtil util=new TimeUtil();
            entity.setMsgTime(util.getLongtime(util.getNowDate()));
            entity.setFriendName(contacts);
            entity.setMsgType(Contstants.MSG_TEXT);
            entity.setMsgContent(box.getEditTextBox().getText().toString()+"");
            RecentContactsDB db=new RecentContactsDB(ChatActivity.this);
            db.savaRctContacts(entity);
            db.colse();
        }
        else if (msg_type==Contstants.MSG_IMG)
        {
            for (int i=0;i<listSend.size();i++) {
                ChatEntity chat=new ChatEntity();
                MessageListEntity entity=new MessageListEntity();
                if (CHAT_MODEL == Contstants.GROUP_CHAT){
                    message=EMMessage.createImageSendMessage(listSend.get(i).getPath(), false, groupID);
                    message.setChatType(EMMessage.ChatType.GroupChat);
                    chat.setChatType(Contstants.GROUP_CHAT);
                    entity.setChatType(Contstants.GROUP_CHAT);
                }
                else {
                    message=EMMessage.createImageSendMessage(listSend.get(i).getPath(), false, contacts);
                    chat.setChatType(Contstants.GROUP_SGINGLE);
                    entity.setChatType(Contstants.GROUP_SGINGLE);
                }
                EMClient.getInstance().chatManager().sendMessage(message);
                final List<EMMessage> list = new ArrayList<EMMessage>();
                list.add(message);
                EMClient.getInstance().chatManager().importMessages(list);
                chat.setMsg(box.getEditTextBox().getText().toString()+"");
                chat.setUser("my");
                chat.setImgurl(listSend.get(i).getPath());
                chat.setImgSendState(false);
                chat.setToUser(contacts);
                chat.setMsgSource(1);
                chat.setFistSend(true);
                chat.setMsgType(Contstants.MSG_IMG);

                listItems.add(chat);
                mAdapter.setListItems(listItems);
                mAdapter.notifyDataSetChanged();
                LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                manager.scrollToPosition(listItems.size()-1);

                TimeUtil util=new TimeUtil();
                entity.setMsgTime(util.getLongtime(util.getNowDate()));
                entity.setFriendName(contacts);
                entity.setMsgType(Contstants.MSG_IMG);
                entity.setMsgContent(listSend.get(i).getPath());
                RecentContactsDB db=new RecentContactsDB(ChatActivity.this);
                db.savaRctContacts(entity);
                db.colse();
            }
        }
        else if (msg_type==Contstants.MSG_VIDEO)
        {


            ChatEntity chat=new ChatEntity();
            MessageListEntity entity=new MessageListEntity();
            if (CHAT_MODEL == Contstants.GROUP_CHAT){
                message = EMMessage.createVideoSendMessage(videoUpEntity.getVideoPath(),videoUpEntity.getVideoThumImage(),
                        (int) videoUpEntity.getVideoDuration(), groupID);
                message.setChatType(EMMessage.ChatType.GroupChat);
                chat.setChatType(Contstants.GROUP_CHAT);
                entity.setChatType(Contstants.GROUP_CHAT);
            }
            else {
                message = EMMessage.createVideoSendMessage(videoUpEntity.getVideoPath(),videoUpEntity.getVideoThumImage(),
                        (int) videoUpEntity.getVideoDuration(), contacts);
                chat.setChatType(Contstants.GROUP_SGINGLE);
                entity.setChatType(Contstants.GROUP_SGINGLE);
            }
            EMClient.getInstance().chatManager().sendMessage(message);
            message.setMessageStatusCallback(new EMCallBack(){
                @Override
                public void onSuccess() {
                    int a=1;
                    String B="";
                }

                @Override
                public void onError(int i, String s) {
                     int a=1;
                    String B=s;
                }

                @Override
                public void onProgress(int i, String s) {
                    int a=1;
                    String B=s;
                }
            });
            final List<EMMessage> list = new ArrayList<EMMessage>();
            list.add(message);
            EMClient.getInstance().chatManager().importMessages(list);
            chat.setMsg(box.getEditTextBox().getText().toString()+"");
            chat.setUser("my");
            chat.setImgurl(videoUpEntity.getVideoThumImage());
            chat.setVideoUpEntity(videoUpEntity);
            chat.setToUser(contacts);
            chat.setVideoPath(videoUpEntity.getVideoPath());
            chat.setMsgSource(1);
            chat.setFistSend(true);
            chat.setMsgType(Contstants.MSG_VIDEO);

            listItems.add(chat);
            mAdapter.setListItems(listItems);
            mAdapter.notifyDataSetChanged();
            LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            manager.scrollToPosition(listItems.size()-1);
            TimeUtil util=new TimeUtil();
            entity.setMsgTime(util.getLongtime(util.getNowDate()));
            entity.setFriendName(contacts);
            entity.setMsgType(Contstants.MSG_VIDEO);
            entity.setMsgContent(videoUpEntity.getVideoPath());
            RecentContactsDB db=new RecentContactsDB(ChatActivity.this);
            db.savaRctContacts(entity);
            db.colse();

        }





//        savaMsgID(message.getMsgId());

    }

    private void savaMsgID(String id)
    {
        new SharePreferenceUtil(ChatActivity.this,contacts).savaMsgID(id);
    }
    @Override
    public void getMessage(ChatEntity entity) {

        mAdapter.getListItems().add(entity);
        mAdapter.notifyDataSetChanged();
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        manager.scrollToPosition(listItems.size()-1);
    }

    @Override
    public void getInviterMsg() {

    }

    @Override
    public void onRefresh() {

        mSwipeRefreshLayout.setRefreshing(true);

     Thread t=new Thread()
     {
         @Override
         public void run() {
             super.run();
             handler.sendEmptyMessageDelayed(1,1000);
         }
     };
        t.start();



    }
    Handler handler=new Handler()
    {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(contacts);
            if (conversation!=null) {
                int count = conversation.getAllMessages().size();
                int conut1 = count % 15;
                int count3 = LogPage * 15;
                int pageId = count - (15 * LogPage) - 1;
                if (count - count3 > 1) {
                    if (conut1 == 0) {
                        conut1 = 15;
                    }
                    String msgId = conversation.getAllMessages().get(pageId).getMsgId();
                    List<EMMessage> messages = conversation.loadMoreMsgFromDB(msgId, 15);
                    listLog = getMsgLog(messages, conversation);
                    listLog.addAll(listItems);
                    mAdapter.setListItems(listLog);
                    mAdapter.notifyDataSetChanged();
                    LogPage++;
                } else {
                    new ToastUtil(ChatActivity.this).longToast("已加载到最后");
                }
            }
            else
            {
                new ToastUtil(ChatActivity.this).longToast("已加载到最后");
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 相机拍照完成后，返回图片路径
        if(requestCode == REQUEST_CAMERA){
            if(resultCode == Activity.RESULT_OK) {
                if (mTmpFile!= null) {
                   Image image=new Image();
                    image.setPath(mTmpFile.toString());
                    listSend.add(image);
                    senMsg(Contstants.MSG_IMG);
            } else{
                    if(mTmpFile != null && mTmpFile.exists()){
                        mTmpFile.delete();
                    }
                }
        }

    }else if (requestCode==REQUEST_VIDEO)
        {
            if(resultCode == Activity.RESULT_OK) {
                   videoUpEntity= (VideoUpEntity) data.getSerializableExtra("video");
                   senMsg(Contstants.MSG_VIDEO);
            }
        }


}}
