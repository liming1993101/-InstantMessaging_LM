package com.lm.im_huanxin.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.RemoteViews;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.lm.im_huanxin.MyApplication;
import com.lm.im_huanxin.R;
import com.lm.im_huanxin.entity.ChatEntity;
import com.lm.im_huanxin.entity.InviteInfoEntity;
import com.lm.im_huanxin.entity.MessageListEntity;
import com.lm.im_huanxin.entity.VideoUpEntity;
import com.lm.im_huanxin.ui.MainActivity;
import com.lm.im_huanxin.utils.Contstants;
import com.lm.im_huanxin.utils.DataTypeUtils;
import com.lm.im_huanxin.utils.InviteInfoDB;
import com.lm.im_huanxin.utils.RecentContactsDB;
import com.lm.im_huanxin.utils.SharePreferenceUtil;
import com.lm.im_huanxin.utils.TimeUntil;
import com.lm.im_huanxin.utils.TimeUtil;

import java.util.List;


/**
 * Created by Administrator on 2016/6/16.
 */
public class GetMsgService extends Service {
    private static final int MSG=0x001;
    private MyApplication application;

    private NotificationManager mNotificationManager;
    private  boolean isStart=false;
    private  Notification mNotification;
    private Context context=this;
    private int MSG_TYPE;


    private BroadcastReceiver backKeyReceiveer=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           setMsgNotification();//根据我们是否在后台来显示一个通知
        }
    };

    private void setMsgNotification() {

        int icon= R.mipmap.ic_launcher;
        CharSequence tickerText="";
        long when = System.currentTimeMillis();
        mNotification=new Notification(icon,tickerText,when);
        RemoteViews contentView=new RemoteViews(context.getPackageName(),
                R.layout.notify_view);

        contentView.setTextViewText(R.id.notify_name,"admin");
        contentView.setTextViewText(R.id.notify_msg, "IM正在后台运行");
        contentView.setTextViewText(R.id.notify_time, "aa");
        mNotification.contentView=contentView;
        Intent intent=new Intent(this, MainActivity.class);
        PendingIntent contentIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.contentIntent=contentIntent;
        mNotificationManager.notify(Contstants.NOTIFY_ID,mNotification);

    }

    private Handler handler=new Handler()
    {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void handleMessage(Message msg) {

                    ChatEntity chatEntity= (ChatEntity) msg.getData().getSerializable("msg");
                    String from=chatEntity.getToUser();
                    String messagetext="";
                    if (chatEntity.getMsgType()==Contstants.MSG_TEXT)
                    {
                        messagetext=chatEntity.getMsg();
                    }
                    else
                    {
                        messagetext="图片";
                    }
                    int icon = R.mipmap.ic_launcher;
                    CharSequence tickerText = ":";
                    Intent intent = new Intent(context, MainActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
                    mNotification = new Notification.Builder(context)
                            .setContentTitle("来自"+from+"消息")
                            .setContentIntent(contentIntent)
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(true)//设置可以清除
                            .setContentText(messagetext)
                            .setTicker("" + tickerText)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                            .build();


                    mNotificationManager.notify(Contstants.NOTIFY_ID, mNotification);


        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter=new IntentFilter();
        filter.addAction(Contstants.BACKKEY_ACTION);
        registerReceiver(backKeyReceiveer,filter);
        mNotificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        application= (MyApplication) this.getApplicationContext();
        application.setmNotificationManager(mNotificationManager);
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        EMClient.getInstance().contactManager().setContactListener(emContactListener);
        EMClient.getInstance().groupManager().addGroupChangeListener(emGroupChangeListener);
    }

    //接收消息监听
    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
             //消息监听
            for (int i=0;i<messages.size();i++) {
                String msgUrl = "";
                String msg = "";
                int CHAT_TYPE=1;
                ChatEntity chatEntity = new ChatEntity();
                EMClient.getInstance().chatManager().importMessages(messages);
                if (messages.get(i).getType() == EMMessage.Type.TXT) {//根据消息类型来获得不同数据的类型，该类型为文本
                    EMTextMessageBody messageBody = (EMTextMessageBody) messages.get(i).getBody();
                    msg = messageBody.getMessage();
                    MSG_TYPE=Contstants.MSG_TEXT;
                    chatEntity.setMsgType(MSG_TYPE);
                } else if (messages.get(i).getType() == EMMessage.Type.IMAGE) {//消息类型图片
                    EMImageMessageBody imageMessageBody = (EMImageMessageBody) messages.get(i).getBody();
                    msgUrl = imageMessageBody.getRemoteUrl().toString();
                    MSG_TYPE=Contstants.MSG_IMG;
                    chatEntity.setMsgType(MSG_TYPE);
                }
                else if (messages.get(i).getType() == EMMessage.Type.VIDEO)
                {
                    EMVideoMessageBody videoMessageBody= (EMVideoMessageBody) messages.get(i).getBody();
                    VideoUpEntity entity=new VideoUpEntity();
                    entity.setVideoPath(videoMessageBody.getRemoteUrl());
                    entity.setVideoDuration(videoMessageBody.getDuration());
                    entity.setVideoThumImage(videoMessageBody.getThumbnailUrl());
                    entity.setVideoName(videoMessageBody.getFileName());
                    chatEntity.setVideoUpEntity(entity);
                    MSG_TYPE=Contstants.MSG_VIDEO;
                    chatEntity.setMsgType(MSG_TYPE);
                }
                int msgCount;

                String formUser = "";
                String userName=messages.get(i).getUserName();
                if (messages.get(i).getChatType()==EMMessage.ChatType.GroupChat)
                {
                    formUser=messages.get(i).getTo();
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(formUser);
                    msgCount=conversation.getUnreadMsgCount();
                    CHAT_TYPE=Contstants.GROUP_CHAT;
                    chatEntity.setChatType(CHAT_TYPE);
                }
                else
                {
                    formUser = messages.get(i).getFrom();
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(formUser);
                    chatEntity.setChatType(CHAT_TYPE);
                    msgCount=conversation.getUnreadMsgCount();
                }

                long time = messages.get(i).getMsgTime();
                chatEntity.setMsg(msg);
                chatEntity.setId(messages.get(i).getMsgId());
                chatEntity.setImgurl(msgUrl);
                chatEntity.setToUser(formUser);
                chatEntity.setUser(userName);
                chatEntity.setMsgSource(2);
                Bundle bundle = new Bundle();
                bundle.putSerializable("msg",chatEntity);
                Message message=new Message();
                message.setData(bundle);
                RecentContactsDB db=new RecentContactsDB(context);
                MessageListEntity entity=new MessageListEntity();

                entity.setMsgTime(time);
                entity.setFirendImg("wu");
                entity.setMsgCount(msgCount);
                entity.setFriendName(formUser);
                entity.setMsgType(MSG_TYPE);
                entity.setMsgSource(2);
                entity.setChatType(CHAT_TYPE);
                entity.setMsgContent(msg);
                db.savaRctContacts(entity);
                db.colse();
                new SharePreferenceUtil(context,formUser).savaMsgID(messages.get(i).getMsgId());
                if (application.isBackgroud()) //判断程序是否是后台运行 如果是 我们将此消息发送个通知显示，不是则发送给广播给相应的界面接收消息
                {
                    handler.sendMessage(message);//发送给通知
                }
                else
                {
                    //发送给广播
                    Intent broadCast=new Intent();
                    broadCast.setAction(Contstants.ACTION);
                    broadCast.putExtra(Contstants.MSGKEY,chatEntity);
                    sendBroadcast(broadCast);
                }
            }
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

    //好友变动监听
    EMContactListener emContactListener=new EMContactListener() {

        @Override
        public void onContactAgreed(String username) {
            //好友请求被同意
            RecentContactsDB db=new RecentContactsDB(context);
            MessageListEntity entity=new MessageListEntity();
            String data=new TimeUtil().getNowDate();
            entity.setMsgTime(new TimeUtil().getLongtime(data));
            entity.setFirendImg("wu");
            entity.setMsgCount(0);
            entity.setFriendName("好友请求");
            entity.setMsgType(Contstants.INVITE_FRIENDS);
            entity.setMsgContent(username+"你的好友接受跟你搞基的请求");
            db.savaRctContacts(entity);
            db.colse();
            InviteInfoDB inviteInfoDB=new InviteInfoDB(context);
            InviteInfoEntity inviteInfoEntity=new InviteInfoEntity();
            inviteInfoEntity.setGroupID("1111");
            inviteInfoEntity.setInviter(username);
            inviteInfoEntity.setInviterReson("你的好友接受跟你搞基的请求");
            inviteInfoEntity.setDispose(2);
            inviteInfoEntity.setInviterInfo("用户"+username+"你的好友接受跟你搞基的请求");
            inviteInfoEntity.setInviterType(Contstants.CONTACTS_FROM_INFO);
            inviteInfoDB.savaInvitInfo(inviteInfoEntity);
            inviteInfoDB.colse();
            sendBroadcast();

        }

        @Override
        public void onContactRefused(String username) {
            //好友请求被拒绝
            RecentContactsDB db=new RecentContactsDB(context);
            MessageListEntity entity=new MessageListEntity();
            String data=new TimeUtil().getNowDate();
            entity.setMsgTime(new TimeUtil().getLongtime(data));
            entity.setFirendImg("wu");
            entity.setMsgCount(0);
            entity.setFriendName("好友请求");
            entity.setMsgType(Contstants.INVITE_FRIENDS);
            entity.setMsgContent(username+"拒绝了你的请求");
            db.savaRctContacts(entity);
            db.colse();
            InviteInfoDB inviteInfoDB=new InviteInfoDB(context);
            InviteInfoEntity inviteInfoEntity=new InviteInfoEntity();
            inviteInfoEntity.setGroupID("1111");
            inviteInfoEntity.setInviter(username);
            inviteInfoEntity.setInviterReson("没错你就是个丑人");
            inviteInfoEntity.setDispose(2);
            inviteInfoEntity.setInviterInfo("用户"+username+"拒绝了你的好友请求");
            inviteInfoEntity.setInviterType(Contstants.CONTACTS_FROM_INFO);
            inviteInfoDB.savaInvitInfo(inviteInfoEntity);
            inviteInfoDB.colse();
            sendBroadcast();
        }

        @Override
        public void onContactInvited(String username, String reason) {
            //收到好友邀请
            RecentContactsDB db=new RecentContactsDB(context);
            MessageListEntity entity=new MessageListEntity();
            String data=new TimeUtil().getNowDate();
            entity.setMsgTime(new TimeUtil().getLongtime(data));
            entity.setFirendImg("wu");
            entity.setMsgCount(0);
            entity.setFriendName("好友请求");
            entity.setMsgType(Contstants.INVITE_FRIENDS);
            entity.setMsgContent(username+"把你加为好友");
            db.savaRctContacts(entity);
            db.colse();
            InviteInfoDB inviteInfoDB=new InviteInfoDB(context);
            InviteInfoEntity inviteInfoEntity=new InviteInfoEntity();
            inviteInfoEntity.setGroupID("1111");
            inviteInfoEntity.setInviter(username);
            inviteInfoEntity.setInviterReson(reason);
            inviteInfoEntity.setDispose(1);
            inviteInfoEntity.setInviterInfo("用户"+username+"想把你加为好友");
            inviteInfoEntity.setInviterType(Contstants.CONTACTS_FROM_INVITE);
            inviteInfoDB.savaInvitInfo(inviteInfoEntity);
            inviteInfoDB.colse();
            sendBroadcast();
        }

        @Override
        public void onContactDeleted(String username) {
            //被删除时回调此方法
        }


        @Override
        public void onContactAdded(String username) {
            //增加了联系人时回调此方法
        }
    };

    //  群组变动监听
    EMGroupChangeListener emGroupChangeListener=new EMGroupChangeListener() {
        @Override
        public void onUserRemoved(String groupId, String groupName) {
            //当前用户被管理员移除出群组
            RecentContactsDB db=new RecentContactsDB(context);
            MessageListEntity entity=new MessageListEntity();
            String data=new TimeUtil().getNowDate();
            entity.setMsgTime(new TimeUtil().getLongtime(data));
            entity.setFirendImg("wu");
            entity.setMsgCount(0);
            entity.setFriendName("群组请求");
            entity.setMsgType(Contstants.INVITE_MEMBER);
            entity.setMsgContent("群"+groupId+"移除了成员groupName");
            db.savaRctContacts(entity);
            db.colse();
            InviteInfoDB inviteInfoDB=new InviteInfoDB(context);
            InviteInfoEntity inviteInfoEntity=new InviteInfoEntity();
            inviteInfoEntity.setGroupID(groupId);
            inviteInfoEntity.setInviter(groupName);
            inviteInfoEntity.setInviterReson("");
            inviteInfoEntity.setDispose(2);
            inviteInfoEntity.setInviterInfo("群"+groupId+"移除了成员groupName");
            inviteInfoEntity.setInviterType(Contstants.CONTACTS_FROM_INFO);
            inviteInfoDB.savaInvitInfo(inviteInfoEntity);
            inviteInfoDB.colse();
            sendBroadcast();
        }
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            //收到加入群组的邀请
            RecentContactsDB db=new RecentContactsDB(context);
            MessageListEntity entity=new MessageListEntity();
            String data=new TimeUtil().getNowDate();
            entity.setMsgTime(new TimeUtil().getLongtime(data));
            entity.setFirendImg("wu");
            entity.setMsgCount(0);
            entity.setFriendName("群组请求");
            entity.setMsgType(Contstants.INVITE_MEMBER);
            entity.setMsgContent("群"+groupId+"邀请你加入");
            db.savaRctContacts(entity);
            db.colse();
            InviteInfoDB inviteInfoDB=new InviteInfoDB(context);
            InviteInfoEntity inviteInfoEntity=new InviteInfoEntity();
            inviteInfoEntity.setGroupID(groupId);
            inviteInfoEntity.setInviter(inviter);
            inviteInfoEntity.setInviterReson(reason);
            inviteInfoEntity.setDispose(1);
            inviteInfoEntity.setInviterInfo("群"+groupId+"邀请你加入搞基");
            inviteInfoEntity.setInviterType(Contstants.GROUP_FROM_INVITE);
            inviteInfoDB.savaInvitInfo(inviteInfoEntity);
            inviteInfoDB.colse();
            sendBroadcast();
        }
        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
            //群组邀请被拒绝
            RecentContactsDB db=new RecentContactsDB(context);
            MessageListEntity entity=new MessageListEntity();
            String data=new TimeUtil().getNowDate();
            entity.setMsgTime(new TimeUtil().getLongtime(data));
            entity.setFirendImg("wu");
            entity.setMsgCount(0);
            entity.setFriendName("群组请求");
            entity.setMsgType(Contstants.INVITE_MEMBER);
            entity.setMsgContent("群"+groupId+"拒绝了你的加入");
            db.savaRctContacts(entity);
            db.colse();
            InviteInfoDB inviteInfoDB=new InviteInfoDB(context);
            InviteInfoEntity inviteInfoEntity=new InviteInfoEntity();
            inviteInfoEntity.setGroupID(groupId);
            inviteInfoEntity.setInviter(invitee);
            inviteInfoEntity.setInviterReson(reason);
            inviteInfoEntity.setDispose(2);
            inviteInfoEntity.setInviterInfo("群管理"+invitee+"拒绝你进来搞基");
            inviteInfoEntity.setInviterType(Contstants.GROUP_FROM_INFO);
            inviteInfoDB.savaInvitInfo(inviteInfoEntity);
            inviteInfoDB.colse();
            sendBroadcast();
        }
        @Override
        public void onInvitationAccpted(String groupId, String inviter, String reason) {
            //群组邀请被接受
            RecentContactsDB db=new RecentContactsDB(context);
            MessageListEntity entity=new MessageListEntity();
            String data=new TimeUtil().getNowDate();
            entity.setMsgTime(new TimeUtil().getLongtime(data));
            entity.setFirendImg("wu");
            entity.setMsgCount(0);
            entity.setFriendName("群组请求");
            entity.setMsgType(Contstants.INVITE_MEMBER);
            entity.setMsgContent("群"+groupId+"同意了你的加入请求");
            db.savaRctContacts(entity);
            db.colse();
            InviteInfoDB inviteInfoDB=new InviteInfoDB(context);
            InviteInfoEntity inviteInfoEntity=new InviteInfoEntity();
            inviteInfoEntity.setGroupID(groupId);
            inviteInfoEntity.setInviter(inviter);
            inviteInfoEntity.setInviterReson(reason);
            inviteInfoEntity.setDispose(2);
            inviteInfoEntity.setInviterInfo("群管理"+inviter+"介绍你进来搞基");
            inviteInfoEntity.setInviterType(Contstants.GROUP_FROM_INFO);
            inviteInfoDB.savaInvitInfo(inviteInfoEntity);
            inviteInfoDB.colse();
            sendBroadcast();
        }
        @Override
        public void onGroupDestroy(String groupId, String groupName) {
            //群组被创建者解散
            RecentContactsDB db=new RecentContactsDB(context);
            MessageListEntity entity=new MessageListEntity();
            String data=new TimeUtil().getNowDate();
            entity.setMsgTime(new TimeUtil().getLongtime(data));
            entity.setFirendImg("wu");
            entity.setMsgCount(0);
            entity.setFriendName("群组请求");
            entity.setMsgType(Contstants.INVITE_MEMBER);
            entity.setMsgContent("群"+groupId+"被管理员给解散了");
            db.savaRctContacts(entity);
            db.colse();
        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {

        }

        @Override
        public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
            //收到加群申请
            RecentContactsDB db=new RecentContactsDB(context);
            MessageListEntity entity=new MessageListEntity();
            String data=new TimeUtil().getNowDate();
            entity.setMsgTime(new TimeUtil().getLongtime(data));
            entity.setFirendImg("wu");
            entity.setMsgCount(0);
            entity.setFriendName("群组请求");
            entity.setMsgType(Contstants.INVITE_MEMBER);
            entity.setMsgContent(applyer+"想加入"+groupName);
            db.savaRctContacts(entity);
            db.colse();
            InviteInfoDB inviteInfoDB=new InviteInfoDB(context);
            InviteInfoEntity inviteInfoEntity=new InviteInfoEntity();
            inviteInfoEntity.setGroupID(groupId);
            inviteInfoEntity.setInviter(groupName);
            inviteInfoEntity.setInviterReson(reason);
            inviteInfoEntity.setInviterInfo(applyer+"想加入"+groupName);
            inviteInfoEntity.setInviterType(Contstants.GROUP_FROM_INVITE);
            inviteInfoDB.savaInvitInfo(inviteInfoEntity);
            inviteInfoDB.colse();
            sendBroadcast();

        }
        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {
            //加群申请被同意
            RecentContactsDB db=new RecentContactsDB(context);
            MessageListEntity entity=new MessageListEntity();
            entity.setMsgTime(Long.parseLong(new TimeUtil().getNowDate()));
            entity.setFirendImg("wu");
            entity.setMsgCount(0);
            entity.setFriendName("群组请求");
            entity.setMsgType(Contstants.INVITE_MEMBER);
            entity.setMsgContent("群"+groupId+"的加入请求被同意");
            db.savaRctContacts(entity);
            db.colse();
            InviteInfoDB inviteInfoDB=new InviteInfoDB(context);
            InviteInfoEntity inviteInfoEntity=new InviteInfoEntity();
            inviteInfoEntity.setGroupID(groupId);
            inviteInfoEntity.setInviter(groupName);
            inviteInfoEntity.setInviterInfo("群"+groupId+"的加入请求被同意");
            inviteInfoEntity.setInviterType(Contstants.GROUP_FROM_INFO);
            inviteInfoDB.savaInvitInfo(inviteInfoEntity);
            inviteInfoDB.colse();
            sendBroadcast();
        }
        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
            // 加群申请被拒绝
            RecentContactsDB db=new RecentContactsDB(context);
            MessageListEntity entity=new MessageListEntity();
            entity.setMsgTime(Long.parseLong(new TimeUtil().getNowDate()));
            entity.setFirendImg("wu");
            entity.setMsgCount(0);
            entity.setFriendName("群组请求");
            entity.setMsgType(Contstants.INVITE_MEMBER);
            entity.setMsgContent("群"+groupId+"的加入请求被拒绝");
            db.savaRctContacts(entity);
            db.colse();
            InviteInfoDB inviteInfoDB=new InviteInfoDB(context);
            InviteInfoEntity inviteInfoEntity=new InviteInfoEntity();
            inviteInfoEntity.setGroupID(groupId);
            inviteInfoEntity.setInviter(groupName);
            inviteInfoEntity.setInviterReson(reason);
            inviteInfoEntity.setInviterInfo("群"+decliner+"并不想跟你搞基");
            inviteInfoEntity.setInviterType(Contstants.GROUP_FROM_INFO);
            inviteInfoDB.savaInvitInfo(inviteInfoEntity);
            inviteInfoDB.colse();
            sendBroadcast();
        }
    };

    private void sendBroadcast()
    {
        Intent broadCast=new Intent();
        broadCast.setAction(Contstants.ACTION);
        broadCast.putExtra(Contstants.INVITER_MSG,"inviter");
        sendBroadcast(broadCast);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(backKeyReceiveer);
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        mNotificationManager.cancel(Contstants.NOTIFY_ID);

    }
}
