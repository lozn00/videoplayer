package cn.qssq666.videoplayerdemo;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by qssq on 2018/4/27 qssq666@foxmail.com
 */
public class Appcontext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
