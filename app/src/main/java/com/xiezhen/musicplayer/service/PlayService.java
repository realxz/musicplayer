package com.xiezhen.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xiezhen.musicplayer.entity.Mp3Info;
import com.xiezhen.musicplayer.utils.MediaUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xiezhen on 2015/12/16 0016.
 */
public class PlayService extends Service {
    private MediaPlayer mPlayer;
    private int currentPosition = 0;//当前正在播放的歌曲位置
    ArrayList<Mp3Info> mp3Infos;
    private MusicUpdateListener musicUpdateListener;

    private ExecutorService es = Executors.newSingleThreadExecutor();

    public PlayService() {

    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public class PlayBinder extends Binder {
        public PlayService getPlayService() {
            return PlayService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("xiezhen", "onBind");
        return new PlayBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (es != null && es.isShutdown() == false) {
            es.shutdown();
        }
        Log.d("xiezhen", "onDestroy");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mp3Infos = MediaUtils.getMp3Infos(this);
        es.execute(updateStatus);
        Log.d("xiezhen", "onCreate");
    }

    Runnable updateStatus = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (musicUpdateListener != null && mPlayer != null & mPlayer.isPlaying()) {
                    musicUpdateListener.onPublish(getCurrentProgress());
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void play(int position) {
        if (position >= 0 && position < mp3Infos.size()) {
            Mp3Info mp3Info = mp3Infos.get(position);
            mPlayer.reset();
            try {
                mPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
                mPlayer.prepare();
                mPlayer.start();
                currentPosition = position;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (musicUpdateListener != null) {
                musicUpdateListener.onChange(currentPosition);
            }

        }
    }

    public void pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    public void next() {
        if (currentPosition + 1 > mp3Infos.size() - 1) {
            currentPosition = 0;
        } else {
            currentPosition++;
        }
        play(currentPosition);
    }

    public void prev() {
        if (currentPosition - 1 < 0) {
            currentPosition = mp3Infos.size() - 1;
        } else {
            currentPosition--;
        }
        play(currentPosition);
    }

    public void start() {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        }
        return false;
    }

    public int getCurrentProgress() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            return mPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public int getDuration() {
        return mPlayer.getDuration();
    }

    public void seekTO(int msec) {
        mPlayer.seekTo(msec);
    }

    public interface MusicUpdateListener {
        public void onPublish(int progress);

        public void onChange(int position);
    }

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
        this.musicUpdateListener = musicUpdateListener;
    }
}
