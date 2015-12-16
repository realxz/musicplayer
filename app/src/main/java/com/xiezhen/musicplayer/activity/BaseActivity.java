package com.xiezhen.musicplayer.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

import com.xiezhen.musicplayer.service.PlayService;

/**
 * Created by xiezhen on 2015/12/16 0016.
 */
public abstract class BaseActivity extends FragmentActivity {
    public  PlayService playService;
    private boolean isBind = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayService.PlayBinder playBinder = (PlayService.PlayBinder) service;
            playService = playBinder.getPlayService();
            playService.setMusicUpdateListener(musicUpdateListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playService=null;
        }
    };
    private PlayService.MusicUpdateListener musicUpdateListener = new PlayService.MusicUpdateListener() {
        @Override
        public void onPublish(int progress) {
            publish(progress);
        }

        @Override
        public void onChange(int position) {
            change(position);
        }
    };

    public abstract void publish(int progress);

    public abstract void change(int position);

    public void bindPlayService() {
        if (!isBind) {
            Intent intent = new Intent(this, PlayService.class);
            bindService(intent, conn, BIND_AUTO_CREATE);
            isBind = true;
        }
    }

    public void unbindPlayService() {
        if (isBind) {
            unbindService(conn);
            isBind = false;
        }
    }
}
