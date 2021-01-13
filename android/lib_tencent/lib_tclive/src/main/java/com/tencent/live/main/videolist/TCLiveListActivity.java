package com.tencent.live.main.videolist;

import com.tcj.sunshine.base.activity.BaseActivity;
import com.tencent.live.R;
import com.tencent.live.main.videolist.ui.TCVideoListFragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class TCLiveListActivity extends BaseActivity {


    @Override
    public int getLayoutResID() {
        return R.layout.activity_live_list;
    }

    @Override
    public void initUI() {
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);
        TCVideoListFragment fragment = new TCVideoListFragment();
        transaction.add(R.id.fragment_layout, fragment);
        transaction.commitAllowingStateLoss();
    }
}
