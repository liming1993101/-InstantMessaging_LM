package com.lm.im_huanxin.utils;

import android.content.Context;
import android.widget.Toast;

import com.lm.im_huanxin.ui.LoginActivity;

/**
 * Created by Administrator on 2016/8/17.
 */
public class ToastUtil {

    private Context context;

    public ToastUtil(Context context) {
        this.context=context;
    }

    public void longToast(String content)
    {
        Toast.makeText(context,content,Toast.LENGTH_LONG).show();
    }

    public void  shortToast(String content)
    {
        Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
    }
}
