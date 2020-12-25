package com.tcj.sunshine.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tcj.sunshine.album.R;
import com.tcj.sunshine.boxing.AbsBoxingActivity;
import com.tcj.sunshine.boxing.AbsBoxingViewFragment;
import com.tcj.sunshine.boxing.model.config.BoxingConfig;
import com.tcj.sunshine.boxing.model.entity.BaseMedia;
import com.tcj.sunshine.tools.DrawableUtils;
import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.view.fragment.BoxingFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * 相册Activity
 */
public class BoxingActivity extends AbsBoxingActivity {

    private Toolbar mToolbar;
    private View mMenuView;
    private TextView mTvTitle;
    private ImageView mIvArrow;
    private BoxingFragment mBoxingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boxing);
        this.initUI();
    }

    /**
     * 初始化控件
     */
    private void initUI(){
        this.mToolbar = this.findViewById(R.id.mToolbar);
        this.mMenuView = this.findViewById(R.id.mMenuView);
        this.mTvTitle = this.findViewById(R.id.mTvTitle);
        this.mIvArrow = this.findViewById(R.id.mIvArrow);

        this.setSupportActionBar(mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.mToolbar.setNavigationOnClickListener(v -> callback());

        this.mTvTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        this.mIvArrow.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTvTitle.getLayoutParams();

        BoxingConfig config = this.getBoxingConfig();
        if (config.getMode() == BoxingConfig.Mode.VIDEO) {
            this.mTvTitle.setText("所有视频");
            this.mIvArrow.setVisibility(View.GONE);
            params.rightMargin = ScreenUtils.dip2px(10);
        }else if(config.getMode() == BoxingConfig.Mode.AUDIO){
            this.mTvTitle.setText("所有音频");
            this.mIvArrow.setVisibility(View.GONE);
            params.rightMargin = ScreenUtils.dip2px(10);
        }else if(config.getMode() == BoxingConfig.Mode.MULTI_IMG_AND_VIDEO){
            this.mTvTitle.setText("图片和视频");
            this.mIvArrow.setVisibility(View.VISIBLE);

            params.rightMargin = ScreenUtils.dip2px(5);

            this.mMenuView.setOnClickListener(v ->  {
                if(mBoxingFragment != null)mBoxingFragment.showMenu();
            });
        }else {
            this.mTvTitle.setText("所有图片");
            this.mIvArrow.setVisibility(View.VISIBLE);
            params.rightMargin = ScreenUtils.dip2px(5);

            this.mMenuView.setOnClickListener(v ->  {
                if(mBoxingFragment != null)mBoxingFragment.showMenu();
            });
        }

        mTvTitle.requestLayout();

        if(this.mBoxingFragment != null){
            this.mBoxingFragment.setMenuTitleView(this.mMenuView, this.mTvTitle, this.mIvArrow);
        }
    }


    @NonNull
    @Override
    public AbsBoxingViewFragment onCreateBoxingView(ArrayList<BaseMedia> medias) {
        FragmentManager manager = this.getSupportFragmentManager();
        this.mBoxingFragment = (BoxingFragment) manager.findFragmentByTag(BoxingFragment.TAG);
        if(this.mBoxingFragment == null) {
            this.mBoxingFragment = new BoxingFragment();
            this.mBoxingFragment.setSelectedBundle(medias);
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.mLayoutFragment, mBoxingFragment, BoxingFragment.TAG);
            transaction.commitAllowingStateLoss();
        }

        return this.mBoxingFragment;
    }

    @Override
    public void onBoxingFinish(Intent intent, @Nullable List<BaseMedia> medias) {
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    /**
     * 点击返回键
     */
    private void callback(){
        setResult(RESULT_CANCELED);
        finish();
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if(keyCode == KeyEvent.KEYCODE_BACK){
            callback();
            return true;
        }
        return false;
    }
}
