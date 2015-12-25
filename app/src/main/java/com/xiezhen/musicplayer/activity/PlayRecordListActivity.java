package com.xiezhen.musicplayer.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.xiezhen.musicplayer.R;
import com.xiezhen.musicplayer.adapter.MyMusicListAdapter;
import com.xiezhen.musicplayer.application.CrashAppliacation;
import com.xiezhen.musicplayer.entity.Mp3Info;
import com.xiezhen.musicplayer.service.PlayService;
import com.xiezhen.musicplayer.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class PlayRecordListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView record_lv;
    private TextView record_tv;
    private ArrayList<Mp3Info> mp3Infos;
    private MyMusicListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_record_list);
        record_lv = (ListView) findViewById(R.id.record_lv);
        record_tv = (TextView) findViewById(R.id.record_tv);
        record_lv.setOnItemClickListener(this);
        initData();
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
            List<Mp3Info> list = CrashAppliacation.dbUtils.findAll(
                    Selector.from(Mp3Info.class).
                            where("playTime", "!=", 0).
                            orderBy("playTime", true).
                            limit(Constant.PLAY_RECORD_NUM));
            if (list == null || list.size() == 0) {
                record_tv.setVisibility(View.VISIBLE);
                record_lv.setVisibility(View.GONE);
            } else {
                record_tv.setVisibility(View.GONE);
                record_lv.setVisibility(View.VISIBLE);
                mp3Infos = (ArrayList<Mp3Info>) list;
                adapter = new MyMusicListAdapter(this, mp3Infos);
                record_lv.setAdapter(adapter);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (playService.getChangePlayList() != PlayService.PLAY_RECORD_LIST) {
            playService.setMp3Infos(mp3Infos);
            playService.setChangePlayList(PlayService.PLAY_RECORD_LIST);
        }
        playService.play(position);
    }

    @Override
    public void change(int position) {

    }

    @Override
    public void publish(int progress) {

    }
}
