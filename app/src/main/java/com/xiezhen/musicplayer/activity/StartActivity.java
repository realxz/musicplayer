package com.xiezhen.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import com.xiezhen.musicplayer.R;
import com.xiezhen.musicplayer.service.PlayService;

import java.lang.ref.WeakReference;

import cn.bmob.v3.Bmob;

public class StartActivity extends Activity {

    private static final int START_ACTIVITY = 0x1;
    private final MyHandler handler=new MyHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_start);
        Bmob.initialize(this, "42d2ada2da07f49a3753ce86dadbfde3");

        Intent intent = new Intent(this, PlayService.class);
        startService(intent);

        handler.sendEmptyMessageDelayed(START_ACTIVITY, 2000);
    }

    private static class MyHandler extends Handler{
        private final WeakReference<StartActivity> mActivity;

        public MyHandler(StartActivity mActivity) {
            this.mActivity = new WeakReference<StartActivity>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            StartActivity activity=mActivity.get();
            if(activity!=null){
                super.handleMessage(msg);
                switch (msg.what) {
                    case START_ACTIVITY:
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        activity.finish();
                        break;
                }
            }

        }
    }

}
