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

public class StartActivity extends Activity {

    private static final int START_ACTIVITY = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);
        startService(new Intent(this, PlayService.class));
        handler.sendEmptyMessageDelayed(START_ACTIVITY, 2000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_ACTIVITY:
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();
                    break;
            }

        }
    };
}
