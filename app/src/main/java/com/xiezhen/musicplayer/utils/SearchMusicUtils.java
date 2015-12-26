package com.xiezhen.musicplayer.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.xiezhen.musicplayer.entity.SearchResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xiezhen on 2015/12/26 0026.
 */
public class SearchMusicUtils {
    private static final int SIZE = 20;//分页显示数量
    private static final String URL = Constant.BAIDU_URL + Constant.BAIDU_SEARCH;
    private static SearchMusicUtils sInstance;
    private OnSearchResultListener mListener;
    private ExecutorService mThreadPool;

    public synchronized static SearchMusicUtils getsInstance() {
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
                            mListener.onSearchResult((ArrayList<SearchResult>) msg.obj);

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
                ArrayList<SearchResult> results = getMusicList(key, page);
                if (results == null) {
                    handler.sendEmptyMessage(Constant.FAILED);
                    return;
                }
                handler.obtainMessage(Constant.SUCCESS, results).sendToTarget();
            }
        });
    }

    private ArrayList<SearchResult> getMusicList(final String key, final int page) {
        final String start = String.valueOf((page - 1) * SIZE);
        try {
            Document doc = Jsoup.connect(URL)
                    .data("key", key, "start", start, "size", String.valueOf(SIZE))
                    .userAgent(Constant.USER_AGENT)
                    .timeout(6 * 1000).get();
//            System.out.println(doc);
            Elements songTitles = doc.select("div.song-item.clearfix");
            Elements songInfos;
            ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
            TAG:
            for (Element song : songTitles) {
                songInfos = song.getElementsByTag("a");
                SearchResult searchResult = new SearchResult();
                for (Element info : songInfos) {
                    if (info.attr("href").startsWith("http://y.baidu.com/song/")) {
                        continue TAG;
                    }
                    if (info.attr("href").equals("#") && !TextUtils.isEmpty(info.attr("data-songdata"))) {
                        continue TAG;
                    }
                    if (info.attr("href").startsWith("/song")) {
                        searchResult.setMusicName(info.text());
                        searchResult.setUrl(info.attr("href"));
                    }
                    if (info.attr("href").startsWith("/data")) {
                        searchResult.setArtist(info.text());
                    }
                    if (info.attr("href").startsWith("/album")) {
                        searchResult.setAlbum(info.text().replaceAll("《|》", ""));
                    }
                }
                searchResults.add(searchResult);
            }
            return searchResults;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface OnSearchResultListener {
        public void onSearchResult(ArrayList<SearchResult> results);
    }
}
