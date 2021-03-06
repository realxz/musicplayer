package com.xiezhen.musicplayer.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.xiezhen.musicplayer.R;
import com.xiezhen.musicplayer.application.CrashAppliacation;
import com.xiezhen.musicplayer.entity.Mp3Cloud;
import com.xiezhen.musicplayer.entity.Mp3Info;
import com.xiezhen.musicplayer.service.PlayService;
import com.xiezhen.musicplayer.utils.Constant;
import com.xiezhen.musicplayer.utils.DownloadUtils;
import com.xiezhen.musicplayer.utils.MediaUtils;
import com.xiezhen.musicplayer.utils.SearchMusicUtils;
import com.xiezhen.musicplayer.view.DefaultLrcBuilder;
import com.xiezhen.musicplayer.view.ILrcBuilder;
import com.xiezhen.musicplayer.view.ILrcView;
import com.xiezhen.musicplayer.view.LrcRow;
import com.xiezhen.musicplayer.view.LrcView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class PlayActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private TextView textView1_title;
    private TextView textView1_end_time;
    private TextView textView1_start_time;
    private ImageView imageView1_next;
    private ImageView imageView2_play_pause;
    private ImageView imageView1_album;
    private ImageView imageView3_previous;
    private ImageView imageView1_play_mode;
    private ImageView imageView1_favorite;
    private SeekBar seekBar1;
    //    private ArrayList<Mp3Info> mp3infos;
    private static final int UPDATE_TIME = 11111;
    private static final int UPDATE_LRC = 22222;
    private static MyHandler myHandler;
    //    private boolean isPause = false;
    private ViewPager viewPager;
    private List<View> data;
    private LrcView lrcView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        initView();
        initViewPager();
        addListener();
//        mp3infos = MediaUtils.getMp3Infos(this);
//        bindPlayService();
//        change(position);
        myHandler = new MyHandler(this);
//        isPause = getIntent().getBooleanExtra("isPause", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("xiezhen", "PlayActivity OnResume");
//        unbindPlayService();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindPlayService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initView() {
//        textView1_title = (TextView) findViewById(R.id.textView1_title);
        textView1_end_time = (TextView) findViewById(R.id.textView1_end_time);
        textView1_start_time = (TextView) findViewById(R.id.textView1_start_time);
        imageView1_next = (ImageView) findViewById(R.id.imageView1_next);
        imageView2_play_pause = (ImageView) findViewById(R.id.imageView2_play_pause);
//        imageView1_album = (ImageView) findViewById(R.id.imageView1_album);
        imageView3_previous = (ImageView) findViewById(R.id.imageView3_previous);
        imageView1_play_mode = (ImageView) findViewById(R.id.imageView1_play_mode);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        imageView1_favorite = (ImageView) findViewById(R.id.imageView1_favorite);
    }

    private void addListener() {
        imageView1_next.setOnClickListener(this);
        imageView2_play_pause.setOnClickListener(this);
        imageView3_previous.setOnClickListener(this);
        imageView1_play_mode.setOnClickListener(this);
        seekBar1.setOnSeekBarChangeListener(this);
        imageView1_favorite.setOnClickListener(this);
    }

    private void initViewPager() {
        data = new ArrayList<View>();
        View album_image_layout = getLayoutInflater().inflate(R.layout.album_image_layout, null);
        imageView1_album = (ImageView) album_image_layout.findViewById(R.id.imageView1_album);
        textView1_title = (TextView) album_image_layout.findViewById(R.id.textView1_title);
        data.add(album_image_layout);

        View lrc_layout = getLayoutInflater().inflate(R.layout.lrc_layout, null);
        lrcView = (LrcView) lrc_layout.findViewById(R.id.lrcView);
        lrcView.setListener(new ILrcView.LrcViewListener() {
            @Override
            public void onLrcSeeked(int newPosition, LrcRow row) {
                if (playService.isPlaying()) {
                    playService.seekTO((int) row.time);
                }
            }
        });
        lrcView.setLoadingTipText("正在加载歌词");
        lrcView.setBackgroundResource(R.mipmap.jb_bg);
        lrcView.getBackground().setAlpha(150);

        data.add(lrc_layout);
        viewPager.setAdapter(new ViewPagerAdapter(data));
    }

    class ViewPagerAdapter extends PagerAdapter {

        private List<View> data;


        public ViewPagerAdapter(List<View> data) {
            super();
            this.data = data;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            container.addView(data.get(position));
            return data.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(data.get(position));
        }

    }

    private void loadLRC(File lrcFile) {
        StringBuffer buf = new StringBuffer(1024 * 10);
        char[] chars = new char[1024];
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(lrcFile)));
            int len = -1;
            while ((len = in.read(chars)) != -1) {
                buf.append(chars, 0, len);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ILrcBuilder builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(buf.toString());
        lrcView.setLrc(rows);

    }

    static class MyHandler extends Handler {
        private PlayActivity playActivity;


        public MyHandler(PlayActivity playActivity) {
            this.playActivity = playActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (playActivity != null) {
                switch (msg.what) {
                    case UPDATE_TIME:
                        playActivity.textView1_start_time.setText(MediaUtils.formatTime(msg.arg1));
                        break;
                    case UPDATE_LRC:
                        playActivity.lrcView.seekLrcToTime((int) msg.obj);
                        break;
                    case DownloadUtils.SUCCESS_LRC:
                        playActivity.loadLRC(new File((String) msg.obj));
                        break;
                    case DownloadUtils.FAILED_LRC:
                        Toast.makeText(playActivity, "歌词下载失败", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }

        }
    }

    @Override
    public void publish(int progress) {
//        textView1_start_time.setText(MediaUtils.formatTime(progress));
        Message msg = myHandler.obtainMessage(UPDATE_TIME);
        msg.arg1 = progress;
        myHandler.sendMessage(msg);
        Log.d("xiezhen",""+progress);
        seekBar1.setProgress(progress);
        myHandler.obtainMessage(UPDATE_LRC, progress).sendToTarget();
    }

    @Override
    public void change(int position) {
        Log.d("xiezhen", "PlayActivity");

        Mp3Info mp3Info = playService.mp3Infos.get(position);
        textView1_title.setText(mp3Info.getTitle());
        Bitmap albumBitmap = MediaUtils.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, false);
        imageView1_album.setImageBitmap(albumBitmap);
        textView1_end_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));

        seekBar1.setProgress(0);
        seekBar1.setMax((int) mp3Info.getDuration());
        if (playService.isPlaying()) {
            imageView2_play_pause.setImageResource(R.mipmap.pause);
        } else {
            imageView2_play_pause.setImageResource(R.mipmap.play);
        }

        switch (playService.getPlay_mode()) {
            case PlayService.ORDER_PLAY:
                imageView1_play_mode.setImageResource(R.mipmap.order);
                imageView1_play_mode.setTag(PlayService.ORDER_PLAY);
                break;
            case PlayService.RANDOM_PLAY:
                imageView1_play_mode.setImageResource(R.mipmap.random);
                imageView1_play_mode.setTag(PlayService.RANDOM_PLAY);
                break;
            case PlayService.SINGLE_PLAY:
                imageView1_play_mode.setImageResource(R.mipmap.single);
                imageView1_play_mode.setTag(PlayService.SINGLE_PLAY);
                break;
            default:
                break;
        }

        try {
            Mp3Info likeMp3Info = CrashAppliacation.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId", "=", getId(mp3Info)));

            if (null == likeMp3Info) {
                imageView1_favorite.setImageResource(R.mipmap.xin_bai);
            } else {
                int isLike = likeMp3Info.getIsLike();
                if (isLike == 1) {
                    imageView1_favorite.setImageResource(R.mipmap.xin_hong);
                } else {
                    imageView1_favorite.setImageResource(R.mipmap.xin_bai);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        String songName = mp3Info.getTitle();
        String lrcPath = Environment.getExternalStorageDirectory() + Constant.DIR_LRC + "/" + songName + ".lrc";
        File lrcFile = new File(lrcPath);
        if (!lrcFile.exists()) {
            SearchMusicUtils.getsInstance(this).setListener(new SearchMusicUtils.OnSearchResultListener() {
                @Override
                public void onSearchResult(ArrayList<Mp3Cloud> results) {
                    if (results != null && results.size() > 0) {
                        Mp3Cloud searchResult = results.get(0);
                        DownloadUtils.getsInstance(getApplicationContext()).downloadLRC(searchResult.getLrcUrl().getFileUrl(getApplicationContext()), searchResult.getMusicName(), myHandler);
                    }
                }
            }).search(songName, 1);
        } else {
            loadLRC(lrcFile);
        }

    }

    private long getId(Mp3Info mp3Info) {
        long id = 0;
        switch (playService.getChangePlayList()) {
            case PlayService.MY_MUSIC_LIST:
                id = mp3Info.getId();
                break;
            case PlayService.PLAY_RECORD_LIST:
                id = mp3Info.getMp3InfoId();
                break;
            case PlayService.LIKE_MUSIC_LIST:
                id = mp3Info.getMp3InfoId();
                break;


            default:
                break;
        }
        return id;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView2_play_pause: {
                if (playService.isPlaying()) {
                    imageView2_play_pause.setImageResource(R.mipmap.play);
                    playService.pause();
                } else {
                    if (playService.isPause()) {
                        imageView2_play_pause.setImageResource(R.mipmap.pause);
                        playService.start();
                    } else {
                        playService.play(playService.getCurrentPosition());
                    }
                }
                break;
            }
            case R.id.imageView1_next:
                playService.next();
                break;
            case R.id.imageView3_previous:
                playService.prev();
                break;
            case R.id.imageView1_play_mode: {
                int mode = (int) imageView1_play_mode.getTag();
                switch (mode) {
                    case PlayService.ORDER_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.random);
                        imageView1_play_mode.setTag(PlayService.RANDOM_PLAY);
                        playService.setPlay_mode(PlayService.RANDOM_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.random_play), Toast.LENGTH_SHORT).show();
                        break;
                    case PlayService.RANDOM_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.single);
                        imageView1_play_mode.setTag(PlayService.SINGLE_PLAY);
                        playService.setPlay_mode(PlayService.SINGLE_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.single_play), Toast.LENGTH_SHORT).show();

                        break;
                    case PlayService.SINGLE_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.order);
                        imageView1_play_mode.setTag(PlayService.ORDER_PLAY);
                        playService.setPlay_mode(PlayService.ORDER_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.order_play), Toast.LENGTH_SHORT).show();

                        break;
                    default:
                        break;
                }
                break;
            }
            case R.id.imageView1_favorite: {
                Mp3Info mp3Info = playService.mp3Infos.get(playService.getCurrentPosition());
                try {
                    Mp3Info likeMp3Info = CrashAppliacation.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId", "=", getId(mp3Info)));

                    if (null == likeMp3Info) {
                        Log.d("xiezhen","查询数据库没有这条消息");
                        mp3Info.setMp3InfoId(mp3Info.getId());
                        mp3Info.setIsLike(1);
                        mp3Info.setPlayTime(0);
                        CrashAppliacation.dbUtils.save(mp3Info);
                        imageView1_favorite.setImageResource(R.mipmap.xin_hong);
                    } else {
                        Log.d("xiezhen","查询数据库有这条消息");
                        int isLike = likeMp3Info.getIsLike();
                        if (isLike == 1) {
                            Log.d("xiezhen","查询数据库有这条消息--喜欢");
                            likeMp3Info.setIsLike(0);
                            imageView1_favorite.setImageResource(R.mipmap.xin_bai);
                        } else {
                            likeMp3Info.setIsLike(1);
                            Log.d("xiezhen", "查询数据库有这条消息，不喜欢");
                            imageView1_favorite.setImageResource(R.mipmap.xin_hong);
                        }
                        CrashAppliacation.dbUtils.update(likeMp3Info, "isLike");

                    }
                } catch (DbException e) {
                    Log.d("xiezhen","db error");
                    e.printStackTrace();
                    Log.d("xiezhen",e.getMessage());
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            playService.pause();
            playService.seekTO(progress);
            playService.start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
