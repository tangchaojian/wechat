package com.tcj.sunshine.view.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tcj.sunshine.album.R;
import com.tcj.sunshine.boxing.AbsBoxingViewFragment;
import com.tcj.sunshine.boxing.Boxing;
import com.tcj.sunshine.boxing.model.BoxingManager;
import com.tcj.sunshine.boxing.model.config.BoxingConfig;
import com.tcj.sunshine.boxing.model.entity.AlbumEntity;
import com.tcj.sunshine.boxing.model.entity.BaseMedia;
import com.tcj.sunshine.boxing.model.entity.impl.CameraMedia;
import com.tcj.sunshine.tools.ActivityUtils;
import com.tcj.sunshine.tools.FileUtils;
import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.tools.StringUtils;
import com.tcj.sunshine.tools.ToastUtils;
import com.tcj.sunshine.tools.UriUtils;
import com.tcj.sunshine.view.activity.BoxingPreviewActivity;
import com.tcj.sunshine.view.adapter.AlbumAdapter;
import com.tcj.sunshine.view.adapter.AlbumMenuAdapter;
import com.tcj.sunshine.view.common.HackyGridLayoutManager;
import com.tcj.sunshine.view.common.SpacesItemDecoration;
import com.tcj.sunshine.view.dialog.LoadingDialog;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 相册Fragment
 */
public class BoxingFragment extends AbsBoxingViewFragment {

    public static final String TAG = "com.tcj.sunshine.view.fragment.BoxingFragment";

    private Context context;
    private View mContentView;
    private View mMenuView;
    private TextView mTvTitle;
    private ImageView mIvArrow;
    private RecyclerView mRecyclerView;
    private View mBottomView;
    private TextView mTvPreview;
    private Button mBtnFinish;;

    private ArrayList<BaseMedia> list = new ArrayList<>();
    private ArrayList<BaseMedia> selectedList = new ArrayList<>();
    private Map<String, BaseMedia> selectedMap = new HashMap<>();

    private List<BaseMedia> mediaList;
    private List<BaseMedia> videoList;

    private AlbumAdapter adapter;

    private View mMenuClickView;
    private View mMenuContentLayout;
    private RecyclerView mMenuRecyclerView;
    private List<AlbumEntity> menuList = new ArrayList<>();
    private AlbumMenuAdapter mMenuAdapter;

    private LoadingDialog mLoadingDialog;

    private Animation mUpAnim;
    private Animation mDownAnim;

    private Animation mMenuFadeInAnim;//菜单进入动画
    private Animation mMenuFadeOutAnim;//菜单出去动画

    private Animation mMenuAlphaInAnim;//菜单进入动画
    private Animation mMenuAlphaOutAnim;//菜单出去动画

    private boolean isNeedCamera = false;
    private int COLUM_COUNT = 4;
    private int mMaxCount = 1;

    private boolean isMenuShowing = false;//菜单是否显示

    private int lastMenuPos = 0;

    private SparseArray<ArrayList<BaseMedia>> mAllMedias = new SparseArray<>();
    private boolean isInit = true;

    private static final int IMAGE_CROP_REQUEST_CODE = 9087;

    public static final String BOXING_ACTION = "COM_TCJ_SUNSHINE_ALBUM_BOXING_ACTION";
    private BoxingBroadcastReceiver receiver = new BoxingBroadcastReceiver();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mContentView = inflater.inflate(R.layout.fragment_boxing, container, false);
        this.initUI();
        return this.mContentView;
    }

    @Override
    public void onCreateWithSelectedMedias(Bundle savedInstanceState, @Nullable List<BaseMedia> selectedMedias) {

        if(selectedMedias != null && !selectedMedias.isEmpty()){
            this.selectedList.addAll(selectedMedias);
        }
    }

    private void initUI(){

        this.context = getContext();

        this.context.registerReceiver(receiver, new IntentFilter(BOXING_ACTION));

        this.mRecyclerView = this.mContentView.findViewById(R.id.mRecyclerView);
        this.mBottomView = this.mContentView.findViewById(R.id.mBottomView);
        this.mTvPreview = this.mContentView.findViewById(R.id.mTvPreview);
        this.mBtnFinish = this.mContentView.findViewById(R.id.mBtnFinish);

        this.mMenuClickView = this.mContentView.findViewById(R.id.mMenuClickView);
        this.mMenuContentLayout = this.mContentView.findViewById(R.id.mMenuContentLayout);
        this.mMenuRecyclerView = this.mContentView.findViewById(R.id.mMenuRecyclerView);

        GridLayoutManager mLayoutManager = new HackyGridLayoutManager(getActivity(), COLUM_COUNT);
        mLayoutManager.setSmoothScrollbarEnabled(true);
        this.mRecyclerView.setLayoutManager(mLayoutManager);
        int gap = ScreenUtils.dip2px(5);
        this.mRecyclerView.addItemDecoration(new SpacesItemDecoration(gap, COLUM_COUNT));
        this.adapter = new AlbumAdapter(context, this.list, new OnAlbumListener());
        this.mRecyclerView.setAdapter(adapter);
        this.adapter.notifyDataSetChanged();


        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.mMenuAdapter = new AlbumMenuAdapter(context,menuList, new OnAlbumMenuListener());
        this.mMenuRecyclerView.setLayoutManager(mLinearLayoutManager);
        this.mMenuRecyclerView.setAdapter(this.mMenuAdapter);
        this.mMenuAdapter.notifyDataSetChanged();

        this.mLoadingDialog = new LoadingDialog(context);

        BoxingConfig config = BoxingManager.getInstance().getBoxingConfig();
        config.list = this.list;
        config.selectedList = this.selectedList;
        config.selectedMap = this.selectedMap;

        if(config.getMode() == BoxingConfig.Mode.AUDIO){
            this.isNeedCamera = false;
            mBottomView.setVisibility(View.GONE);
        }else {
            this.isNeedCamera = BoxingManager.getInstance().getBoxingConfig().isNeedCamera();
            if(config.getMode() == BoxingConfig.Mode.SINGLE_IMG) {
                //单图选中，不需底部确定
                mBottomView.setVisibility(View.GONE);
            }
        }

        this.mMaxCount = getMaxCount();

        this.mUpAnim = AnimationUtils.loadAnimation(context, R.anim.anim_album_menu_arrow_up);
        this.mDownAnim = AnimationUtils.loadAnimation(context, R.anim.anim_album_menu_arrow_down);

        this.mMenuFadeInAnim = AnimationUtils.loadAnimation(context, R.anim.boxing_fade_in);
        this.mMenuFadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.boxing_fade_out);

        this.mMenuAlphaInAnim = AnimationUtils.loadAnimation(context, R.anim.boxing_alpha_in);
        this.mMenuAlphaOutAnim = AnimationUtils.loadAnimation(context, R.anim.boxing_alpha_out);

        this.mMenuClickView.setOnClickListener(new OnViewClickListener());
        this.mBtnFinish.setOnClickListener(new OnViewClickListener());
        this.mTvPreview.setOnClickListener(new OnViewClickListener());


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            this.context.unregisterReceiver(receiver);
        }
    }

    @Override
    public void startLoading() {
        if(mLoadingDialog != null)mLoadingDialog.show();
        //开始加载数据
        mContentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                BoxingConfig config = BoxingManager.getInstance().getBoxingConfig();
                if(config.getMode() == BoxingConfig.Mode.MULTI_IMG_AND_VIDEO){
                    loadMedias();
                    loadVideos();
                    loadAlbum();
                }else if (config.getMode() == BoxingConfig.Mode.VIDEO) {
                    loadVideos();
                }else if(config.getMode() == BoxingConfig.Mode.AUDIO){
                    loadAudios();
                }else {
                    loadMedias();
                    loadAlbum();
                }
            }
        }, 200);
    }

    @Override
    public void showMedia(@Nullable List<BaseMedia> medias, int allCount) {
        super.showMedia(medias, allCount);
        this.mediaList = medias;
        if(this.menuList == null) {
            this.menuList = new ArrayList<>();
        }
        //合并数据(同时显示图片和视频的时候，需要合并数据)
        this.merge(0);
    }

    @Override
    public void showVideo(@Nullable List<BaseMedia> medias, int allCount) {
        super.showVideo(medias, allCount);
        this.videoList = medias;
        if(this.videoList == null) {
            this.videoList = new ArrayList<>();
        }
        //因为不知道showMedia()和showVideo()哪个先回调，所以需要判断合并数据(同时显示图片和视频的时候，需要合并数据)
        this.merge(1);
    }

    @Override
    public void showAudio(@Nullable List<BaseMedia> medias, int allCount) {
        super.showAudio(medias, allCount);
        //音频
    }

    @Override
    public void showAlbum(@Nullable List<AlbumEntity> albums) {
        super.showAlbum(albums);
        //如果是图片和视频选取增加一个"图片和视频",一个"所有视频"
        menuList.addAll(albums);

        this.mergeMenu();
    }


    @Override
    public void onCameraFinish(BaseMedia media) {
        if (media == null) {
            return;
        }
        if (hasCropBehavior()) {
            startCrop(media, IMAGE_CROP_REQUEST_CODE);
        } else {
            selectedList.add(media);
            onFinish(selectedList);
        }
    }



    /**
     * 合并数据
     * 0 表示图片
     * 1 表示视频
     */
    private void merge(int mediaType){

        BoxingConfig config = BoxingManager.getInstance().getBoxingConfig();
        List<BaseMedia> dataList = new ArrayList<>();
        boolean isMergeMenu = false;
        //视频
        if(config.getMode() == BoxingConfig.Mode.VIDEO) {
            //视频
            dataList.addAll(videoList);
        }else if(config.getMode() == BoxingConfig.Mode.MULTI_IMG_AND_VIDEO) {
            //图片和视频

            if(mediaList != null && videoList != null && isInit) {
                isInit = false;
                dataList.addAll(mediaList);
                dataList.addAll(videoList);
                //按时间排序
                Collections.sort(dataList, new DateComparator());
                //可以去判断是否合并菜单
                isMergeMenu = true;
            }else if(!isInit){
                if(mediaType == 0) {
                    //图片
                    dataList.addAll(mediaList);
                }else {
                    dataList.addAll(videoList);
                }
            }else {
                //还要等到另一个回调成功，才能合并数据
                return;
            }

        }else {
            dataList.addAll(mediaList);
        }

        this.list.clear();
        if(dataList != null && !dataList.isEmpty()) {
            ArrayList<BaseMedia> array = this.getFilterMedias(dataList);
            if(lastMenuPos == 0 && isNeedCamera) {
                //有照相机
                CameraMedia cameraMedia = new CameraMedia();
                cameraMedia.setViewType(AlbumAdapter.CAMERA_TYPE);
                this.list.add(cameraMedia);
            }

            this.list.addAll(array);
            this.mAllMedias.put(lastMenuPos, array);
            if(isMergeMenu) {
                //合并菜单
                if(videoList.size() > 0) {
                    this.mAllMedias.put(1, (ArrayList<BaseMedia>) videoList);
                }
                this.mergeMenu();
            }
        }
        this.adapter.notifyDataSetChanged();
        if(mLoadingDialog != null)mLoadingDialog.cancel();

    }


    /**
     * 合并菜单数据
     */
    private void mergeMenu(){

        BoxingConfig mode = BoxingManager.getInstance().getBoxingConfig();
        if(mode.getMode() == BoxingConfig.Mode.MULTI_IMG_AND_VIDEO) {

            if(!menuList.isEmpty() && mAllMedias.size() > lastMenuPos && !mAllMedias.get(lastMenuPos).isEmpty()) {
                AlbumEntity firstItem = menuList.get(0);//所有图片
                //把图片和视频数据替换原来的数据
                firstItem.mCount = mAllMedias.get(lastMenuPos).size();
                List<BaseMedia> imageList1 = new ArrayList<>();
                imageList1.add(mAllMedias.get(lastMenuPos).get(0));
                firstItem.mImageList = imageList1;
                firstItem.mBucketName = "图片和视频";

                if(!videoList.isEmpty()) {
                    AlbumEntity secondItem = new AlbumEntity();
                    secondItem.mCount = videoList.size();
                    List<BaseMedia> imageList2 = new ArrayList<>();
                    imageList2.add(videoList.get(0));
                    secondItem.mImageList = imageList2;
                    secondItem.mBucketName = "所有视频";
                    menuList.add(1, secondItem);
                }

            }else {
                return;
            }
        }

        if ((menuList == null || menuList.isEmpty())) {
            if(mMenuView != null && mIvArrow != null) {
                mIvArrow.setVisibility(View.GONE);
                mMenuView.setOnClickListener(null);
            }
            return;
        }

        mMenuAdapter.notifyDataSetChanged();

        int size = menuList.size();
        int itemHeight = ScreenUtils.dip2px(50);
        int trueHeight = size * itemHeight;//计算显示的高度
        int maxHeight = 10 * itemHeight;//最大高度
        int minHeight = 3 * itemHeight;//最小高度
        if(trueHeight < minHeight)trueHeight = minHeight -1;
        if(trueHeight > maxHeight)trueHeight = maxHeight -1;

        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) mMenuRecyclerView.getLayoutParams();
        params.height = trueHeight;
        mMenuRecyclerView.requestLayout();
    }

    public void setMenuTitleView(View mMenuView, TextView mTvTitle, ImageView mIvArrow) {
        this.mMenuView = mMenuView;
        this.mTvTitle = mTvTitle;
        this.mIvArrow = mIvArrow;
    }

    /**
     * 显示菜单view
     */
    public void showMenuView(){
        this.isMenuShowing = true;
        if (this.mIvArrow != null) {
            this.mIvArrow.clearAnimation();
            this.mIvArrow.startAnimation(mUpAnim);
        }
        this.mMenuClickView.startAnimation(mMenuAlphaInAnim);
        this.mMenuContentLayout.startAnimation(mMenuFadeInAnim);
        this.mMenuClickView.setVisibility(View.VISIBLE);
    }


    /**
     * 隐藏菜单view
     */
    private void hideMenuView(){
        this.isMenuShowing = false;
        if (this.mIvArrow != null) {
            this.mIvArrow.clearAnimation();
            this.mIvArrow.startAnimation(mDownAnim);
        }

        this.mMenuClickView.startAnimation(mMenuAlphaOutAnim);
        this.mMenuContentLayout.startAnimation(mMenuFadeOutAnim);
        this.mMenuClickView.setVisibility(View.GONE);
    }


    /**
     * 重新设置数字顺序
     */
    private void sort(){
        for (int i = 0; i < this.selectedList.size(); i++){
            BaseMedia item = this.selectedList.get(i);
            item.setNum("" + (i + 1));
            adapter.notifyDataSetChanged();
        }
        this.updateBottomView();
    }

    /**
     * 更新底部view
     */
    private void updateBottomView(){
        if(this.selectedList.size() > 0){
            this.mBtnFinish.setEnabled(true);
            this.mTvPreview.setEnabled(true);
            this.mBtnFinish.setText("完成(" + this.selectedList.size() + ")");
        }else {
            this.mBtnFinish.setEnabled(false);
            this.mTvPreview.setEnabled(false);
            this.mBtnFinish.setText("完成");
        }
    }


    /**
     * 获取在选中数组中的索引
     * @param media
     * @return
     */
    private int getSelectedListPosition(BaseMedia media) {
        if(selectedList != null && !selectedList.isEmpty()) {
            for (int i = 0; i < selectedList.size(); i++) {
                BaseMedia entity = selectedList.get(i);
                if(entity.getPath().equals(media.getPath())) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * 筛选选中的media
     */
    private ArrayList<BaseMedia> getFilterMedias(List<BaseMedia> medias) {

        ArrayList<BaseMedia> array = new ArrayList<>();
        if(medias != null && !medias.isEmpty()) {
            for (BaseMedia media : medias) {
                if(StringUtils.isEmpty(media.getPath()))continue;

                String key = StringUtils.getMD5(media.getPath().getBytes(), 16);
                if(selectedMap.containsKey(key)) {
                    BaseMedia selectedMedia = selectedMap.get(key);
                    int position = this.getSelectedListPosition(selectedMedia);
                    selectedList.remove(selectedMedia);
                    selectedMap.remove(key);
                    selectedList.add(position, media);
                    selectedMap.put(key, media);
                    media.setIsSelected(1);
                    media.setNum(selectedMedia.getNum());

                }else {
                    media.setIsSelected(0);
                    media.setNum("");
                }

                media.setViewType(AlbumAdapter.NORMAL_TYPE);
                array.add(media);
            }
        }
        return array;
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



    /**
     * 显示菜单
     */
    public void showMenu(){

        if(!isMenuShowing) {
            this.showMenuView();
        }else {
            this.hideMenuView();
        }
    }

    public ArrayList<BaseMedia> getSelectedList() {
        return selectedList;
    }

    private class OnViewClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.mBtnFinish) {
                //完成
                if(check(1)) {
                    onFinish(selectedList);
                }
            }else if(v.getId() == R.id.mTvPreview) {
                //预览
            }else if(v.getId() == R.id.mMenuClickView) {
                //收起菜单
                hideMenuView();
            }
        }
    }

    /**
     * 菜单点击监听
     */
    private class OnAlbumMenuListener implements AlbumMenuAdapter.OnAlbumMenuListener {

        @Override
        public void onClick(View view, int position, AlbumEntity item) {
            LogUtils.i("sunshine-album", "position:[" + position + "]  item.name:[" + item.mBucketName + "]");
            hideMenuView();
            if(mMenuAdapter != null && lastMenuPos != position) {
                String bucketId = item.mBucketId;
                String buckeName = item.mBucketName;
                item.mIsSelected = true;
                mTvTitle.setText(buckeName);
                menuList.get(lastMenuPos).mIsSelected = false;
                mMenuAdapter.notifyDataSetChanged();
                lastMenuPos = position;

                if(mAllMedias.get(lastMenuPos) != null) {
                    //如果当前查询的是第一条数据，即所有数据
                    list.clear();

                    if(lastMenuPos == 0 && isNeedCamera) {
                        //有照相机
                        CameraMedia cameraMedia = new CameraMedia();
                        cameraMedia.setViewType(AlbumAdapter.CAMERA_TYPE);
                        list.add(cameraMedia);
                    }

                    List<BaseMedia> medias = mAllMedias.get(lastMenuPos);
                    list.addAll(getFilterMedias(medias));
                    adapter.notifyDataSetChanged();
                }else {
                    mContentView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mLoadingDialog != null) mLoadingDialog.show();
                            loadMedias(0, bucketId);
                        }
                    }, 150);
                }

            }
        }
    }

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
        adapter.notifyDataSetChanged();
    }


    /**
     * 相册点击监听
     */
    private class OnAlbumListener implements AlbumAdapter.OnAlbumListener {

        @Override
        public void camera() {

            try {

                File mSaveFile = FileUtils.createFile(FileUtils.DIR_CAMERA, System.currentTimeMillis() + ".jpg");
                startCamera(getActivity(),BoxingFragment.this,mSaveFile.getAbsolutePath());
            }catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onChecked(View view, int position, BaseMedia media) {
            selected(media);
        }

        @Override
        public void onClick(View view, int position, BaseMedia media) {
            BoxingConfig config = BoxingManager.getInstance().getBoxingConfig();
            if(config.getMode() == BoxingConfig.Mode.SINGLE_IMG) {
                //单图选中
                //路径不能为空
                if(StringUtils.isEmpty(media.getPath()))return;

                if (hasCropBehavior()) {
                    startCrop(media, IMAGE_CROP_REQUEST_CODE);
                } else {
                    selectedList.add(media);
                    onFinish(selectedList);
                }

            }else {
                //预览
                Intent intent = new Intent(context, BoxingPreviewActivity.class);
                intent.putExtra(BoxingPreviewActivity.POSITION, position);
                intent.putExtra(BoxingPreviewActivity.PREVIEW_TYPE, BoxingPreviewActivity.TYPE_PREVIEW_ALL);
                startActivity(intent);
            }
        }
    }

    private class DateComparator implements Comparator<BaseMedia> {

        @Override
        public int compare(BaseMedia o1, BaseMedia o2) {
            String date1 = o1.getDateModified();
            String date2 = o2.getDateModified();
            return date2.compareTo(date1);
        }
    }

    private class BoxingBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String cmd = intent.getStringExtra("cmd");
            if("finish".equals(cmd)) {
                //广播通知选中
                onFinish(selectedList);
            }else if("update".equals(cmd)) {
                //对选中的BaseMedia排序
                sort();
                adapter.notifyDataSetChanged();
            }
        }
    }

}
