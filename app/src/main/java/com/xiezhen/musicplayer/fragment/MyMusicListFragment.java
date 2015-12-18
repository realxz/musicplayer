package com.xiezhen.musicplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xiezhen.musicplayer.R;
import com.xiezhen.musicplayer.activity.MainActivity;
import com.xiezhen.musicplayer.activity.PlayActivity;
import com.xiezhen.musicplayer.adapter.MyMusicListAdapter;
import com.xiezhen.musicplayer.entity.Mp3Info;
import com.xiezhen.musicplayer.utils.MediaUtils;

import java.util.ArrayList;

/**
 * Created by xiezhen on 2015/12/16 0009.
 */
public class MyMusicListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private MainActivity mainActivity;
    private ListView listView_my_music;
    private ArrayList<Mp3Info> mp3Infos;
    private MyMusicListAdapter adapter;

    private ImageView iv_head;
    private TextView tv_songName;
    private TextView tv_singer;
    private ImageView iv_play_pause;
    private ImageView iv_next;

    private int position = 0;

    public static MyMusicListFragment newInstance() {
        MyMusicListFragment my = new MyMusicListFragment();

        return my;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_music_list, null);
        listView_my_music = (ListView) view.findViewById(R.id.listView_local);
        iv_head = (ImageView) view.findViewById(R.id.imageView_head);
        iv_next = (ImageView) view.findViewById(R.id.imageView_next);
        iv_play_pause = (ImageView) view.findViewById(R.id.imageView_play_pause);
        tv_songName = (TextView) view.findViewById(R.id.textView_songName);
        tv_singer = (TextView) view.findViewById(R.id.textView_singer);

        listView_my_music.setOnItemClickListener(this);
        iv_head.setOnClickListener(this);
        iv_play_pause.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        loadData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        mainActivity.unbindPlayService();
        mainActivity.bindPlayService();
    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivity.unbindPlayService();
    }

    /*
            加载本地音乐列表
            * */
    private void loadData() {
        mp3Infos = MediaUtils.getMp3Infos(getActivity());
        adapter = new MyMusicListAdapter(getActivity(), mp3Infos);
        listView_my_music.setAdapter(adapter);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mainActivity.playService.play(position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
    }

    //    回调播放状态下的ui设置
    public void changeUIStatusOnPlay(int position) {
        Log.d("xiezhen", "changeUistatusonplay");
        if (position >= 0 && position < mp3Infos.size()) {
            Log.d("xiezhen", "" + position);
            Mp3Info mp3Inf = mp3Infos.get(position);
            tv_songName.setText(mp3Inf.getTitle());
            tv_singer.setText(mp3Inf.getArtist());

            Bitmap albumBitmap = MediaUtils.getArtwork(mainActivity, mp3Inf.getId(), mp3Inf.getAlbumId(), true, true);
            iv_head.setImageBitmap(albumBitmap);

            if (mainActivity.playService.isPlaying()) {
                iv_play_pause.setImageResource(R.mipmap.pause);
            } else {
                iv_play_pause.setImageResource(R.mipmap.play);
            }
            this.position = position;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_play_pause: {
                if (mainActivity.playService.isPlaying()) {
                    iv_play_pause.setImageResource(R.mipmap.player_btn_play_normal);
                    mainActivity.playService.pause();
                } else {
                    if (mainActivity.playService.isPause()) {
                        iv_play_pause.setImageResource(R.mipmap.player_btn_pause_normal);
                        mainActivity.playService.start();
                    } else {
                        mainActivity.playService.play(0);
                    }

                }
                break;
            }
            case R.id.imageView_next: {
                mainActivity.playService.next();
                break;
            }
            case R.id.imageView_head: {
                Intent intent = new Intent(mainActivity, PlayActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }

    }
}
