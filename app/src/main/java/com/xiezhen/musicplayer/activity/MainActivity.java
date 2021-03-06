package com.xiezhen.musicplayer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;


import com.xiezhen.musicplayer.R;
import com.xiezhen.musicplayer.application.CrashAppliacation;
import com.xiezhen.musicplayer.fragment.MyMusicListFragment;
import com.xiezhen.musicplayer.fragment.NetMusicListFragment;
import com.xiezhen.musicplayer.service.PlayService;
import com.xiezhen.musicplayer.view.PagerSlidingTabStrip;

public class MainActivity extends BaseActivity {


    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private DisplayMetrics dm;
    private MyMusicListFragment myMusicListFragment;
    private NetMusicListFragment netMusicListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dm = getResources().getDisplayMetrics();

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);

        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        setTabsValue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ilike:
                Intent intent = new Intent(this, FavoriteActivity.class);
                startActivity(intent);
                break;
            case R.id.near_play:
                Intent intent1 = new Intent(this, PlayRecordListActivity.class);
                startActivity(intent1);
                break;
            case R.id.exit:
                stopService(new Intent(this, PlayService.class));
                finish();
                break;
        }
        return true;
    }

    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#45c01a"));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
        tabs.setSelectedTextColor(Color.parseColor("#45c01a"));
    }

    @Override
    public void publish(int progress) {
        //更新进度条
    }

    @Override
    public void change(int position) {
        //切换状态播放位置
        if (pager.getCurrentItem() == 0) {
            myMusicListFragment.loadData();
            myMusicListFragment.changeUIStatusOnPlay(position);

        } else if (pager.getCurrentItem() == 1) {

        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 2;

        private final String[] TITLES = {"我的音乐", "网络音乐"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (myMusicListFragment == null) {
                    myMusicListFragment = MyMusicListFragment.newInstance();
                }
                return myMusicListFragment;
            } else if (position == 1) {
                if (netMusicListFragment == null) {
                    netMusicListFragment = NetMusicListFragment.newInstance();
                }
                return netMusicListFragment;
            }
            return null;

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = CrashAppliacation.sp.edit();
        editor.putInt("currentPosition", playService.getCurrentPosition());
        editor.putInt("play_mode", playService.getPlay_mode());
        editor.commit();
    }
}