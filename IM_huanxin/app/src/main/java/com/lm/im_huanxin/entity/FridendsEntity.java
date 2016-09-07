package com.lm.im_huanxin.entity;

/**
 * Created by Administrator on 2016/8/15.
 */
public class FridendsEntity {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String headImg;
    private String name;
}
