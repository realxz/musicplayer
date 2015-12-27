package com.xiezhen.musicplayer.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xiezhen.musicplayer.entity.Mp3Cloud;
import com.xiezhen.musicplayer.entity.SearchResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by xiezhen on 2015/12/26 0026.
 */
public class DownloadUtils {
    private static final String DOWNLOAD_URL = "/download?_o=%2Fsearch%2Fsong";
    public static final int SUCCESS_LRC = 1;//下载歌词成功
    public static final int FAILED_LRC = 2;//下载歌词失败
    public static final int SUCCESS_MP3 = 3;//下载mp3成功
    public static final int FAILED_MP3 = 4;//下载MP3失败
    public static final int GET_MP3_URL = 5;//获取MP3URL成功
    public static final int GET_FAILED_MP3_URL = 6;//获取MP3URL失败
    public static final int MUSIC_EXISTS = 7;//音乐是否存在

    private static DownloadUtils sInstance;
    private OnDownloadListener mListener;
    private ExecutorService mThreadPool;

    public static Context context;

    public DownloadUtils setListener(OnDownloadListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public synchronized static DownloadUtils getsInstance(Context context) {
        DownloadUtils.context = context;
        if (sInstance == null) {
            sInstance = new DownloadUtils();
        }
        return sInstance;
    }

    private DownloadUtils() {
        mThreadPool = Executors.newSingleThreadExecutor();
    }

    public void download(final Mp3Cloud searchResult) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SUCCESS_LRC:
                        if (mListener != null) mListener.onDownload("歌词下载成功");
                        break;
                    case FAILED_LRC:
                        if (mListener != null) mListener.onFailed("歌词下载失败");
                        break;
                    case GET_MP3_URL:
                        Log.d("xiezhen", "d");
                        downloadMusic(searchResult, (BmobFile) msg.obj, this);
                        break;
                    case GET_FAILED_MP3_URL:
                        if (mListener != null) mListener.onFailed("下载失败，该歌曲为收费或VIP类型");
                        break;
                    case SUCCESS_MP3:
                        if (mListener != null)
                            mListener.onDownload(searchResult.getMusicName() + "已下载");
//                        String url = Constant.BAIDU_URL + searchResult.getUrl();
                        Log.d("xiezhen", "lrc=" + searchResult.getLrcUrl().getFileUrl(context));
                        Log.d("xiezhen", "lrc=" + searchResult.getLrcUrl().getFilename());
                        downloadLRC(searchResult.getLrcUrl().getFileUrl(context), searchResult.getMusicName(), this);
                        break;
                    case FAILED_MP3:
                        if (mListener != null)
                            mListener.onFailed(searchResult.getMusicName() + "下载失败");
                        break;
                    case MUSIC_EXISTS:
                        if (mListener != null) mListener.onFailed("音乐已存在");
                        break;
                }
            }
        };
        getDownloadMusicURL(searchResult, handler);
    }


    private void getDownloadMusicURL(final Mp3Cloud searchResult, final Handler handler) {
        handler.obtainMessage(GET_MP3_URL, searchResult.getMusicUrl()).sendToTarget();
       /* BmobQuery<BmobFile> query = new BmobQuery<BmobFile>();
        query.getObject(context, searchResult.getObjectId(), new GetListener<BmobFile>() {
            @Override
            public void onSuccess(BmobFile mp3Cloud) {
                Message msg = handler.obtainMessage(GET_MP3_URL, mp3Cloud);
                Log.d("xiezhen", "mp3cloud=" + mp3Cloud.getFileUrl(context));
                msg.sendToTarget();
            }

            @Override
            public void onFailure(int i, String s) {
                handler.obtainMessage(GET_FAILED_MP3_URL).sendToTarget();
            }
        });*/
       /* mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String url = Constant.BAIDU_URL + "/song/" + searchResult.getUrl().substring(searchResult.getUrl().lastIndexOf("/") + 1) + DOWNLOAD_URL;
                Log.d("xiezhen", "url=" + url);
                try {
                    Document doc = Jsoup.connect(url).userAgent(Constant.USER_AGENT).timeout(6000).get();
                    Log.d("xiezhen", doc + "");
                    Elements targetElements = doc.select("a[data-btndata]");
                    Log.d("xiezhen", "getDownloadMusicURL");
                    if (targetElements.size() <= 0) {
                        Log.d("xiezhen", "a");
                        handler.obtainMessage(GET_FAILED_MP3_URL).sendToTarget();
                        return;
                    }
                    for (Element e : targetElements) {
                        if (e.attr("href").contains(".mp3")) {
                            String result = e.attr("href");
                            Message msg = handler.obtainMessage(GET_MP3_URL, result);
                            msg.sendToTarget();
                            return;
                        }
                        if (e.attr("href").startsWith("/vip")) {
                            targetElements.remove(e);
                        }
                    }
                    if (targetElements.size() <= 0) {
                        handler.obtainMessage(GET_FAILED_MP3_URL).sendToTarget();
                        Log.d("xiezhen", "b");
                        return;
                    }
                    String result = targetElements.get(0).attr("href");
                    Message msg = handler.obtainMessage(GET_MP3_URL, result);
                    msg.sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.obtainMessage(GET_FAILED_MP3_URL).sendToTarget();
                    Log.d("xiezhen", "c");
                }
            }
        });*/
    }

    /**
     * searchResult 下载的歌曲对象
     * url  下载的歌曲对象中的MP3
     */
    private void downloadMusic(final Mp3Cloud searchResult, final BmobFile url, final Handler handler) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                File musicDirFile = new File(Environment.getExternalStorageDirectory() + Constant.DIR_MUSIC);
                Log.d("xiezhen", "aaa");
                if (!musicDirFile.exists()) {

                    musicDirFile.mkdirs();
                }
//                String mp3url = Constant.BAIDU_URL + url;
                String target = musicDirFile + "/" + searchResult.getMusicName() + ".mp3";
                File fileTarget = new File(target);
                if (fileTarget.exists()) {
                    handler.obtainMessage(MUSIC_EXISTS).sendToTarget();
                    return;
                } else {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url.getFileUrl(context)).build();
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            PrintStream ps = new PrintStream(fileTarget);
                            byte[] bytes = response.body().bytes();
                            ps.write(bytes, 0, bytes.length);
                            ps.close();
                            handler.obtainMessage(SUCCESS_MP3).sendToTarget();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.obtainMessage(FAILED_MP3).sendToTarget();
                    }
                }
            }
        });
    }

    public  void downloadLRC(final String url, final String musicName, final Handler handler) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
//                    Document doc = Jsoup.connect(url).userAgent(Constant.USER_AGENT).timeout(6000).get();
//                    Elements lrcTag = doc.select("div.lyric-content");
//                    String lrcURL = lrcTag.attr("data-lrclink");

                    File lrcDirFile = new File(Environment.getExternalStorageDirectory() + Constant.DIR_LRC);
                    if (!lrcDirFile.exists()) {
                        lrcDirFile.mkdirs();
                        Log.d("xiezhen", "alalal");
                    }
//                    lrcURL = Constant.BAIDU_URL + lrcURL;
                    String target = lrcDirFile + "/" + musicName + ".lrc";
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).build();
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            PrintStream ps = new PrintStream(new File(target));
                            byte[] bytes = response.body().bytes();
                            ps.write(bytes, 0, bytes.length);
                            ps.close();
                            handler.obtainMessage(SUCCESS_LRC, target).sendToTarget();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.obtainMessage(FAILED_LRC).sendToTarget();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface OnDownloadListener {
        public void onDownload(String mp3Url);

        public void onFailed(String error);
    }
}
