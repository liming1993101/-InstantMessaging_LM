package com.lm.im_huanxin.entity;

/**
 * Created by Administrator on 2016/8/17.
 */
public class MessageListEntity {

    private int id;
    private int msgType;
    private String friendName;
    private String firendImg;
    private String msgContent;
    private int msgCount;
    private long msgTime;
    private int chatType;
    private int msgSource;

    public int getMsgSource() {
        return msgSource;
    }

    public void setMsgSource(int msgSource) {
        this.msgSource = msgSource;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getFirendImg() {
        return firendImg;
    }

    public void setFirendImg(String firendImg) {
        this.firendImg = firendImg;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }
}
