package com.lm.im_huanxin.utils;

/**
 * Created by Administrator on 2016/8/8.
 */
public class TimeUntil {


    public String convertDate(long date)
    {

        int min= (int) (date/1000/60);
        int second=(int) (date/1000)%60;
        return min+":"+second;
    }
}
