package com.lm.im_huanxin.emoji;

/**
 * 图片实体
 * Created by Nereo on 2015/4/7.

 */
public class Image {
    private String path;
    private String name;
    private long time;
    private boolean mark;


    public Image(String path, String name, long time){
        this.path = path;
        this.name = name;
        this.time = time;
    }

    public Image() {

    }

    @Override
    public boolean equals(Object o) {
        try {
            Image other = (Image) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }
}
