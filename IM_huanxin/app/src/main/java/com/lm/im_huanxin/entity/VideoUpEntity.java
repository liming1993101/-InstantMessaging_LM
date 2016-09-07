package com.lm.im_huanxin.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/23.
 */
public class VideoUpEntity implements Serializable {

    private boolean mark;
    private boolean isLocal;

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    private  String videoName;
    private String  videoPath;
    private long videoDuration;

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public long getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(long videoDuration) {
        this.videoDuration = videoDuration;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public String getVideoThumImage() {
        return videoThumImage;
    }

    public void setVideoThumImage(String videoThumImage) {
        this.videoThumImage = videoThumImage;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    private long videoSize;
    private String videoThumImage;
    private String toUser;
    private String user;
}
