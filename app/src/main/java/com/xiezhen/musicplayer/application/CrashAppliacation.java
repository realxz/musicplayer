package com.xiezhen.musicplayer.application;

import android.app.Application;
import android.content.SharedPreferences;

import com.lidroid.xutils.DbUtils;
import com.xiezhen.musicplayer.handler.CrashHandler;
import com.xiezhen.musicplayer.utils.Constant;

/**
 * Created by Administrator on 2015/12/18 0018.
 */
public class CrashAppliacation extends Application {
    private static CrashAppliacation sInstance;
    public static SharedPreferences sp;
    public static DbUtils dbUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(Constant.SP_NAME, MODE_PRIVATE);
        dbUtils = DbUtils.create(getApplicationContext(), Constant.DB_NAME);
        sInstance = this;

        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    public static CrashAppliacation getInstance() {
        return sInstance;
    }
}
