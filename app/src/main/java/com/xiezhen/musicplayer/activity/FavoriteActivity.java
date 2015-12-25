package com.xiezhen.musicplayer.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.xiezhen.musicplayer.R;
import com.xiezhen.musicplayer.adapter.MyMusicListAdapter;
import com.xiezhen.musicplayer.application.CrashAppliacation;
import com.xiezhen.musicplayer.entity.Mp3Info;
import com.xiezhen.musicplayer.service.PlayService;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private MyMusicListAdapter adapter;
    private ArrayList<Mp3Info> mp3Infos;
    private boolean isChange = false;//是不是已经填充了

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        listView = (ListView) findViewById(R.id.listView_like);
        initData();
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindPlayService();
    }

    private void initData() {
        try {
            List<Mp3Info> list=CrashAppliacation.dbUtils.findAll(Selector.from(Mp3Info.class).where("isLike","=","1"));
            if(list==null||list.size()==0){
                return ;
            }
            mp3Infos = (ArrayList<Mp3Info>) list;
            adapter = new MyMusicListAdapter(this, mp3Infos);
            listView.setAdapter(adapter);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(int progress) {

    }

    @Override
    public void change(int position) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (playService.getChangePlayList() != PlayService.LIKE_MUSIC_LIST) {
            playService.setMp3Infos(mp3Infos);
            playService.setChangePlayList(PlayService.LIKE_MUSIC_LIST);
        } else {

        }
        playService.play(position);
    }
}
