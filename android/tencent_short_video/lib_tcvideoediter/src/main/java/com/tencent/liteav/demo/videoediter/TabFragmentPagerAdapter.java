package com.tencent.liteav.demo.videoediter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by liyuejiao on 2018/6/14.
 */

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mlist;

    public TabFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.mlist = list;
    }

    @Override
    public Fragment getItem(int arg0) {
        return mlist.get(arg0);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }
}