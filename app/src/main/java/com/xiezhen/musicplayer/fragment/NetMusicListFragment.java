package com.xiezhen.musicplayer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiezhen.musicplayer.R;

/**
 * Created by xiezhen on 2015/12/16 0016.
 */
public class NetMusicListFragment extends Fragment {

    public static NetMusicListFragment newInstance() {
        NetMusicListFragment net = new NetMusicListFragment();

        return net;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_net_music_list, container, false);
    }


}
