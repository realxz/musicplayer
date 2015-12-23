package com.xiezhen.musicplayer.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.astuetz.PagerSlidingTabStrip;
import com.xiezhen.musicplayer.R;
import com.xiezhen.musicplayer.application.CrashAppliacation;
import com.xiezhen.musicplayer.fragment.MyMusicListFragment;
import com.xiezhen.musicplayer.fragment.NetMusicListFragment;

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
//        bindPlayService();
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
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
//        tabs.setSelectedTextColor(Color.parseColor("#45c01a"));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
    }

    @Override
    public void publish(int progress) {
        //更新进度条
    }

    @Override
    public void change(int position) {
        //切换状态播放位置
        if (pager.getCurrentItem() == 0) {
            myMusicListFragment.changeUIStatusOnPlay(position);
        } else if (pager.getCurrentItem() == 1) {

        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {getString(R.string.tab_name1), getString(R.string.tab_name2)};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
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