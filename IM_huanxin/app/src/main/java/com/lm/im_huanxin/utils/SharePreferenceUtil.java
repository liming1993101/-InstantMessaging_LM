package com.lm.im_huanxin.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/8/16.
 */
public class SharePreferenceUtil {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Context context;
    private String file;

    public SharePreferenceUtil(Context context, String file) {
        this.context=context;
        this.file=file;
        sp=context.getSharedPreferences(file,context.MODE_PRIVATE);
    }
    public String getUser()
    {
        return sp.getString("user","no");
    }
    public String getPassword()
    {
        return sp.getString("password","no");
    }
    public void savaUser(String user,String password)
    {
        editor = sp.edit();
        editor.putString("user",user);
        editor.putString("password",password);
        editor.commit();
    }
    public void savaMsgID(String id)
    {
        editor=sp.edit();
        editor.putString("msgID",id);
        editor.commit();
    }
    public String getMsgID()
    {
        return sp.getString("msgID","no");
    }
}
