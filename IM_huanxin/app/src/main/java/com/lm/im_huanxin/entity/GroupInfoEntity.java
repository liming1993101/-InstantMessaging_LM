package com.lm.im_huanxin.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/8/29.
 */
public class GroupInfoEntity implements Serializable {

    private int GroupMemberCount;
    private String GroupOwner;
    private String GroupName;
    private String GroupID;
    private String GroupDesc;
    private List<String>GroupMember;

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public String getGroupDesc() {
        return GroupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        GroupDesc = groupDesc;
    }

    public List<String> getGroupMember() {
        return GroupMember;
    }

    public void setGroupMember(List<String> groupMember) {
        GroupMember = groupMember;
    }

    public int getGroupMemberCount() {
        return GroupMemberCount;
    }

    public void setGroupMemberCount(int groupMemberCount) {
        GroupMemberCount = groupMemberCount;
    }

    public String getGroupOwner() {
        return GroupOwner;
    }

    public void setGroupOwner(String groupOwner) {
        GroupOwner = groupOwner;
    }

}
