package com.lm.im_huanxin.entity;

import java.io.PipedReader;
import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/31.
 */
public class InviteInfoEntity implements Serializable
{
    private int id;
    private String inviter;
    private int inviterType;
    private String inviterInfo;
    private String inviterReson;
    private String groupID;
    private Long inviteDate;
    private int dispose;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public int getInviterType() {
        return inviterType;
    }

    public void setInviterType(int inviterType) {
        this.inviterType = inviterType;
    }

    public String getInviterInfo() {
        return inviterInfo;
    }

    public void setInviterInfo(String inviterInfo) {
        this.inviterInfo = inviterInfo;
    }

    public String getInviterReson() {
        return inviterReson;
    }

    public void setInviterReson(String inviterReson) {
        this.inviterReson = inviterReson;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public Long getInviteDate() {
        return inviteDate;
    }

    public void setInviteDate(Long inviteDate) {
        this.inviteDate = inviteDate;
    }

    public int getDispose() {
        return dispose;
    }

    public void setDispose(int dispose) {
        this.dispose = dispose;
    }
}
