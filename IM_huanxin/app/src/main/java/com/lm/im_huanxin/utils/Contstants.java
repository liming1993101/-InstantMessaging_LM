package com.lm.im_huanxin.utils;

/**
 * Created by Administrator on 2016/8/16.
 */
public class Contstants {

    public static final String ACTION = "com.lm.message";//消息广播action
    public static final String INVITER_MSG="inviter";
    public static final String MSGKEY = "message";//消息的key
    public static final String IP_PORT = "ipPort";//保存ip、port的xml文件名
    public static final String SAVE_USER = "saveUser";//保存用户信息的xml文件名
    public static final String BACKKEY_ACTION="com.way.backKey";//返回键发送广播的action
    public static final int NOTIFY_ID = 0x911;//通知ID
    public static final String DBNAME = "friends.db";//数据库名称
    //聊天类型
    public static final int GROUP_SGINGLE=1;
    public static final int GROUP_CHAT=2;


    //消息类型
    public static final int MSG_TEXT=1;
    public static final int MSG_IMG=2;
    public static final int MSG_FILE=3;
    public static final int MSG_VOICE=4;
    public static final int MSG_VIDEO=5;
    //邀请类型
    public static final int INVITE_FRIENDS=11;//好友邀请消息变动
    public static final int INVITE_MEMBER=12;//群邀请消息变动
    //群与好友的消息变动
    public static final int GROUP_FROM_INVITE=100;//群邀请/群拒接
    public static final int GROUP_FROM_INFO=102;//群接受
    public static final int CONTACTS_FROM_INVITE=200;
    public static final int CONTACTS_FROM_INFO=201;







}
