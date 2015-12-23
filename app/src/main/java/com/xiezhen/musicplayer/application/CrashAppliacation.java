package com.xiezhen.musicplayer.application;

import android.app.Application;
import android.content.SharedPreferences;

import com.xiezhen.musicplayer.handler.CrashHandler;
import com.xiezhen.musicplayer.utils.Contant;

/**
 * Created by Administrator on 2015/12/18 0018.
 */
public class CrashAppliacation extends Application {
    private static CrashAppliacation sInstance;
    public static SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(Contant.SP_NAME, MODE_PRIVATE);
        sInstance = this;

        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    public static CrashAppliacation getInstance() {
        return sInstance;
    }
}
