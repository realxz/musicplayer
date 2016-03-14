package com.xiezhen.musicplayer.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.xiezhen.musicplayer.entity.Mp3Cloud;
import com.xiezhen.musicplayer.entity.SearchResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.Manifest;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by xiezhen on 2015/12/26 0026.
 */
public class SearchMusicUtils {
    private static final int SIZE = 20;
    private static final String URL = Constant.BAIDU_URL + Constant.BAIDU_SEARCH;
    private static SearchMusicUtils sInstance;
    private OnSearchResultListener mListener;
    private ExecutorService mThreadPool;
    private static Context context;
    private ArrayList<Mp3Cloud> mp3Clouds;

    public synchronized static SearchMusicUtils getsInstance(Context context) {
        SearchMusicUtils.context = context;
        if (sInstance == null) {
            sInstance = new SearchMusicUtils();
        }
        return sInstance;
    }

    private SearchMusicUtils() {
        mThreadPool = Executors.newSingleThreadExecutor();
    }

    public SearchMusicUtils setListener(OnSearchResultListener listener) {
        this.mListener = listener;
        return this;
    }

    public void search(final String key, final int page) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constant.SUCCESS:
                        if (mListener != null)
                            mListener.onSearchResult((ArrayList<Mp3Cloud>) msg.obj);

                        break;
                    case Constant.FAILED:
                        if (mListener != null) mListener.onSearchResult(null);
                        break;
                }
            }
        };
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                getMusicList(key, page, handler);

            }
        });
    }

    private void getMusicList(final String key, final int page, final Handler handler) {
        BmobQuery<Mp3Cloud> eq1 = new BmobQuery<Mp3Cloud>();
        eq1.addWhereEqualTo("musicName", key);
        BmobQuery<Mp3Cloud> eq2 = new BmobQuery<Mp3Cloud>();
        eq2.addWhereEqualTo("artist", key);
        List<BmobQuery<Mp3Cloud>> queries = new ArrayList<BmobQuery<Mp3Cloud>>();
        queries.add(eq1);
        queries.add(eq2);
        BmobQuery<Mp3Cloud> mainQuery = new BmobQuery<Mp3Cloud>();
        mainQuery.or(queries);
        mainQuery.findObjects(context, new FindListener<Mp3Cloud>() {
            @Override
            public void onSuccess(List<Mp3Cloud> list) {
                Log.d("xiezhen", list.size() + "aaa");
                mp3Clouds = (ArrayList<Mp3Cloud>) list;
                handler.obtainMessage(Constant.SUCCESS, mp3Clouds).sendToTarget();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "查找失败，没有这首歌曲或者歌手", Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessage(Constant.FAILED);
            }
        });

    }

    public interface OnSearchResultListener {
        public void onSearchResult(ArrayList<Mp3Cloud> results);
    }
}
