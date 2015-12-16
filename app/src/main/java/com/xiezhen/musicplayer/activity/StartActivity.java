package com.xiezhen.musicplayer.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xiezhen.musicplayer.R;

public class StartActivity extends AppCompatActivity {

    private static final int START_ACTIVITY = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        handler.sendEmptyMessageDelayed(START_ACTIVITY, 2000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_ACTIVITY:
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    break;
            }
            finish();
        }
    };
}
