package com.tcj.sunshine.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.tcj.sunshine.album.R;
import com.tcj.sunshine.boxing.model.BoxingManager;
import com.tcj.sunshine.boxing.model.config.BoxingConfig;
import com.tcj.sunshine.boxing.model.entity.AlbumEntity;
import com.tcj.sunshine.boxing.model.entity.BaseMedia;
import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.StringUtils;
import com.tcj.sunshine.tools.ToastUtils;
import com.tcj.sunshine.ui.UIToolbar;
import com.tcj.sunshine.ui.viewgroup.TouchFrameLayout;
import com.tcj.sunshine.view.adapter.AlbumSelectedPreviewAdapter;
import com.tcj.sunshine.view.fragment.BoxingFragment;
import com.tcj.sunshine.view.fragment.BoxingPreviewFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Stefan Lau on 2019/12/16 0016.
 */
public class BoxingPreviewActivity extends AppCompatActivity {
    private Context context = BoxingPreviewActivity.this;

    private UIToolbar mToolBar;
    private View mBackView;
    private TouchFrameLayout mCheckLayout;
    private CheckBox mCheckBox;
    private TextView mTvNumber;
    private ViewPager mViewPager;
    private RecyclerView mRecyclerView;
    private FrameLayout mBottomView;//底部view
    private TextView mBtnFinish;//完成

    private AlbumSelectedPreviewAdapter mSelectedAdapter;

    private ArrayList<BaseMedia> list = new ArrayList<>();
    private ArrayList<BaseMedia> selectedList = new ArrayList<>();
    private Map<String, BaseMedia> selectedMap = new HashMap<>();
    private AlbumFragmentStatePagerAdapter adapter;

    private int mMaxCount;
    private int position;
    public static final int TYPE_PREVIEW_ALL = 0;//预览所有图片
    public static final int TYPE_PREVIEW_SELECTED = 1;//预览选中了的

    private int type;// 0 预览所有图片，1，预览选中图片

    public static final String POSITION = "boxing_preview_position";
    public static final String PREVIEW_TYPE = "boxing_preview_type";//预览类型

    private BoxingConfig config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_boxing_preview);
        this.initUI();
    }

    private void initUI() {
        this.config = BoxingManager.getInstance().getBoxingConfig();

        this.mMaxCount = this.config.getMaxCount();
        this.position = this.getIntent().getIntExtra(POSITION, 1);
        this.type = this.getIntent().getIntExtra(PREVIEW_TYPE, 0);
        this.list = this.config.list;
        this.selectedList = this.config.selectedList;
        this.selectedMap = this.config.selectedMap;

        this.mToolBar = this.findViewById(R.id.mToolBar);
        this.mBackView = mToolBar.mBackView;

        this.mCheckLayout = this.findViewById(R.id.mCheckLayout);
        this.mCheckBox = this.findViewById(R.id.mCheckBox);
        this.mTvNumber = this.findViewById(R.id.mTvNumber);


        this.mViewPager = this.findViewById(R.id.mViewPager);
        this.mViewPager.setOffscreenPageLimit(3);
        this.mRecyclerView = this.findViewById(R.id.mRecyclerView);
        this.mBottomView = this.findViewById(R.id.mBottomView);
        this.mBtnFinish = this.findViewById(R.id.mBtnFinish);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.mRecyclerView.setLayoutManager(mLayoutManager);

        this.mSelectedAdapter = new AlbumSelectedPreviewAdapter(context, this.selectedList, new OnAlbumSelectedListener(), this.type);
        this.mRecyclerView.setAdapter(this.mSelectedAdapter);
        this.mSelectedAdapter.notifyDataSetChanged();

        FragmentManager manager = this.getSupportFragmentManager();
        this.adapter = new AlbumFragmentStatePagerAdapter(manager);
        this.mViewPager.setAdapter(adapter);
        this.mViewPager.addOnPageChangeListener(new OnPicturePageChangeListener());
        this.mViewPager.setOffscreenPageLimit(3);
        if(this.config.isNeedCamera()) {
            this.mViewPager.setCurrentItem(position - 1, false);
        }else {
            this.mViewPager.setCurrentItem(position, false);
        }
        this.adapter.notifyDataSetChanged();

        this.mBackView.setOnClickListener(new OnViewClickListener());
        this.mCheckLayout.setOnClickListener(new OnViewClickListener());
        this.mBtnFinish.setOnClickListener(new OnViewClickListener());

        this.sort();//数字排序
        //检索当前这个相片对象，是否是在选中的集合里面
        this.indexSelectedListChecked(position);
    }


    //----------------------------私有方法---------------------------------

    /**
     * 重新设置数字顺序
     */
    private void sort() {
        int sum = 0;

        for (int i = 0; i < this.selectedList.size(); i++) {
            BaseMedia entity = this.selectedList.get(i);
            if (this.type == TYPE_PREVIEW_ALL) {
                entity.setNum(String.valueOf(++sum));
            } else if (this.type == TYPE_PREVIEW_SELECTED) {
                if (entity.getIsSelected() == 1) {
                    entity.setNum(String.valueOf(++sum));
                }
            }
        }

        if (this.selectedList.size() > 0) {
            this.mRecyclerView.setVisibility(View.VISIBLE);
            this.mBtnFinish.setEnabled(true);
            this.mBtnFinish.setText("完成(" + sum + ")");
        } else {
            this.mRecyclerView.setVisibility(View.GONE);
            this.mBtnFinish.setEnabled(false);
            this.mBtnFinish.setText("完成");
        }
    }

    /**
     * 检索当前选中的图片
     *
     * @param position
     */
    private void indexSelectedListChecked(int position) {
        BaseMedia entity = this.list.get(position);

        boolean hasChecked = false;

        if (this.selectedList != null && this.selectedList.size() > 0) {
            for (int i = 0; i < this.selectedList.size(); i++) {
                BaseMedia item = selectedList.get(i);
                if (entity == item && item.getIsSelected() == 1) {
                    hasChecked = true;
                    item.setCurrent(true);
                    this.mCheckBox.setChecked(true);
                    this.mTvNumber.setText("" + entity.getNum());
                    this.mTvNumber.setVisibility(View.VISIBLE);
                    this.mRecyclerView.scrollToPosition(i);
                } else {
                    item.setCurrent(false);
                }
            }

            this.mSelectedAdapter.notifyDataSetChanged();
        }

        if (!hasChecked) {
            this.mCheckBox.setChecked(false);
            this.mTvNumber.setVisibility(View.GONE);
        }
    }

    /**
     * 校验选中个数
     * @param status 0 添加元素时候的校验，1：点击完成时候的校验
     * @return
     */
    private boolean check(int status){

        boolean result = status == 0 ? (selectedList.size() >= mMaxCount) : (selectedList.size() > mMaxCount);

        if(result) {
            BoxingConfig config = BoxingManager.getInstance().getBoxingConfig();
            String hint = "你最多只能选择" + mMaxCount + "张图片";
            if(config.getMode() == BoxingConfig.Mode.MULTI_IMG_AND_VIDEO) {
                hint = "你最多只能选择" + mMaxCount + "个图片或者视频";
            }else if(config.getMode() == BoxingConfig.Mode.VIDEO) {
                hint = "你最多只能选择" + mMaxCount + "个视频";
            }else if(config.getMode() == BoxingConfig.Mode.AUDIO){
                hint = "你最多只能选择" + mMaxCount + "个音频";
            }
            ToastUtils.show(hint);
            return false;
        }else {
            return true;
        }
    }

    //选中或者取消
    private void selected(BaseMedia media){
        //路径不能为空
        if(StringUtils.isEmpty(media.getPath()))return;

        String key = StringUtils.getMD5(media.getPath().getBytes(), 16);
        int isSelected = media.getIsSelected();
        if(isSelected == 0) {

            if(check(0)) {
                media.setIsSelected(1);
                //选中
                selectedList.add(media);
                selectedMap.put(key, media);
            }
        }else {
            media.setIsSelected(0);
            //移除选中media
            selectedList.remove(media);
            selectedMap.remove(key);
        }

        //对选中的BaseMedia排序
        sort();
        indexSelectedListChecked(position);
    }

    private void callback(int resultCode) {

        if (selectedList.size() > 0) {
            Iterator<BaseMedia> iter = selectedList.iterator();
            while (iter.hasNext()) {
                BaseMedia entity = iter.next();
                if (entity.getIsSelected() == 0) {
                    iter.remove();
                }
            }
        }

        if (resultCode == RESULT_CANCELED) {
            setResult(RESULT_CANCELED);
            finish();
        } else {

            Intent broadcast = new Intent(BoxingFragment.BOXING_ACTION);
            broadcast.putExtra("cmd", "finish");
            context.sendBroadcast(broadcast);

            setResult(RESULT_OK);
            finish();
        }
    }


    private class AlbumFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

        public AlbumFragmentStatePagerAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }


        @Override
        public int getItemPosition(@NonNull Object object) {
            LogUtils.i("sunshine-app", "POSITION_NONE");
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if(config.isNeedCamera()) {
                position += 1;
            }
            BaseMedia media = list.get(position);
            BoxingPreviewFragment fragment = new BoxingPreviewFragment();
            Bundle args = new Bundle();
            args.putParcelable("media", media);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public int getCount() {
            if(config.isNeedCamera()) {
                return list.size() > 1 ? list.size() - 1 : 0;
            }
            return list.size();
        }
    }

    private class OnViewClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.mBackView) {
                callback(RESULT_CANCELED);
            } else if (v.getId() == R.id.mBtnFinish) {
                callback(RESULT_OK);
            } else if (v.getId() == R.id.mCheckLayout) {
                boolean checkd = !mCheckBox.isChecked();

                //如果是选中，则校验数量
                if(checkd && !check(0)) {
                    return;
                }

                selected(list.get(position));

                Intent broadcast = new Intent(BoxingFragment.BOXING_ACTION);
                broadcast.putExtra("cmd", "update");
                context.sendBroadcast(broadcast);


            }
        }
    }


    /**
     * 图片左右切换监听
     */
    private class OnPicturePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                // 未拖动页面时执行此处
            } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                // 正在拖动页面时执行此处
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if(config.isNeedCamera()) {
                position ++;
            }

            if (positionOffset == 0 && positionOffsetPixels == 0) {
                if (position != 0) {

                }
            }
        }

        @Override
        public void onPageSelected(int index) {
            if(config.isNeedCamera()) {
                index ++;
            }
            position = index;
            indexSelectedListChecked(index);
        }

    }


    /**
     * 选中图片切换
     */
    private class OnAlbumSelectedListener implements AlbumSelectedPreviewAdapter.OnAlbumSelectedListener {

        @Override
        public void onSelected(int index) {

            BaseMedia entity = selectedList.get(index);
            int position = list.indexOf(entity);
            mViewPager.setCurrentItem(position);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            callback(RESULT_CANCELED);
            return true;
        }
        return false;
    }
}
