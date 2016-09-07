package com.lm.im_huanxin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.lm.im_huanxin.utils.Contstants;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MyApplication extends Application  implements Application.ActivityLifecycleCallbacks {
    private NotificationManager mNotificationManager;
    private boolean isBackgroud;
    private ImageLoader imageLoader;
    private int activityCount=0;

    public boolean isBackgroud() {
        return isBackgroud;
    }

    public void setBackgroud(boolean backgroud) {
        isBackgroud = backgroud;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        EMOptions options = new EMOptions();
// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        options.setAutoAcceptGroupInvitation(false);
        ImageLoaderConfiguration configuration=new ImageLoaderConfiguration.
                Builder(this).threadPriority(3)
                .denyCacheImageMultipleSizesInMemory().//缓存显示不同大小的同一张图片
                diskCacheFileNameGenerator(new Md5FileNameGenerator()). //文件名字的加密策略
                tasksProcessingOrder(QueueProcessingType.LIFO).build();

        imageLoader= ImageLoader.getInstance();
        imageLoader.init(configuration);
//初始化
        registerActivityLifecycleCallbacks(this);
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
// 如果APP启用了远程的service，此application:onCreate会被调用2次
// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
// 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回


        if (processAppName == null ||!processAppName.equalsIgnoreCase(this.getPackageName())) {
            Log.e("aa", "enter the service process!");

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        EMClient.getInstance().init(getApplicationContext(),options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
    }


    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    public void setmNotificationManager(NotificationManager mNotificationManager) {
        this.mNotificationManager = mNotificationManager;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {


    }

    @Override
    public void onActivityStarted(Activity activity) {

        activityCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

        activityCount--;
        if (activityCount==0)
        {
            Intent i = new Intent();
            i.setAction(Contstants.BACKKEY_ACTION);
            sendBroadcast(i);
            setBackgroud(true);
            // 设置后台运行标志，正在运行
        }
        else
        {
            setBackgroud(false);
        }

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
