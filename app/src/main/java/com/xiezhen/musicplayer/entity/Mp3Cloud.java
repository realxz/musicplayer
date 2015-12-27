package com.xiezhen.musicplayer.entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by xiezhen on 2015/12/27 0027.
 */
public class Mp3Cloud extends BmobObject {
    private String musicName;
    private BmobFile musicUrl;
    private BmobFile lrcUrl;
    private String artist;
    private String album;

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public BmobFile getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(BmobFile musicUrl) {
        this.musicUrl = musicUrl;
    }

    public BmobFile getLrcUrl() {
        return lrcUrl;
    }

    public void setLrcUrl(BmobFile lrcUrl) {
        this.lrcUrl = lrcUrl;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
