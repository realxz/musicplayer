package com.xiezhen.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiezhen.musicplayer.R;
import com.xiezhen.musicplayer.entity.Mp3Info;
import com.xiezhen.musicplayer.utils.MediaUtils;

import java.util.ArrayList;

/**
 * Created by xiezhen on 2015/12/16 0016.
 */
public class MyMusicListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Mp3Info> mp3Infos;

    public MyMusicListAdapter(Context context, ArrayList<Mp3Info> mp3Infos) {
        this.context = context;
        this.mp3Infos = mp3Infos;

    }

    public void setMp3Infos(ArrayList<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }

    @Override
    public int getCount() {
        return mp3Infos.size();
    }

    @Override
    public Object getItem(int position) {
        return mp3Infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_music_list, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.textView_title);
            viewHolder.tv_singer = (TextView) convertView.findViewById(R.id.textView_singer);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.textView_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Mp3Info mp3Info = mp3Infos.get(position);
        viewHolder.tv_title.setText(mp3Info.getTitle());
        viewHolder.tv_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
        viewHolder.tv_singer.setText(mp3Info.getArtist());
        return convertView;
    }

    private static class ViewHolder {
        TextView tv_title;
        TextView tv_singer;
        TextView tv_time;
    }
}
