package com.lm.im_huanxin.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/15.
 */
public class ChatEntity implements Serializable {
    private  String id;
    private  String user;
    private String videoPath;
    private boolean imgSendState;
    private VideoUpEntity videoUpEntity;
    private int chatType;

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public VideoUpEntity getVideoUpEntity() {
        return videoUpEntity;
    }

    public void setVideoUpEntity(VideoUpEntity videoUpEntity) {
        this.videoUpEntity = videoUpEntity;
    }

    public String getVideoPath() {
        return videoPath;

    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    private boolean fistSend;

    public boolean isFistSend() {
        return fistSend;
    }

    public void setFistSend(boolean fistSend) {
        this.fistSend = fistSend;
    }

    public boolean isImgSendState() {
        return imgSendState;
    }

    public void setImgSendState(boolean imgSendState) {
        this.imgSendState = imgSendState;
    }

    private int msgSource;

    public int getMsgSource() {
        return msgSource;
    }

    public void setMsgSource(int msgSource) {
        this.msgSource = msgSource;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    private  String toUser;
    private int  msgType;
    private  String imgurl;

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private  String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private  String msg;
}
