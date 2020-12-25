package com.tcj.sunshine.ui.tablayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.tcj.sunshine.tools.GlideUtils;
import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.ui.R;

import java.util.ArrayList;
import java.util.List;

public class UITabLayout extends FrameLayout {

    private Context context;
    private LayoutInflater inflater;
    private TabLayout mTabLayout;
    private int mNormalTextColor;//正常字体颜色
    private int mSelectedTextColor;//选中字体颜色
    private int mNormalTextSize;//正常字体大小
    private int mSelectedTextSize;//选中字体大小
    private boolean isCenter;//是否居中显示
    private boolean isFixedWidth;//是否固定宽
    private int mTabItemWdth;
    private int mIndicatorIconColor;
    private int mIndicatorIconResId;//tab 底部 icon
    private @LayoutRes int mTabLayoutId = 0;//tabLayout
    private @LayoutRes int mTabItemLayoutId = 0;//tab选项卡自定义布局页面

    protected List<TextView> textList = new ArrayList<>();
    protected SparseArray<View> mTabItemVies = new SparseArray<>();
    private int position = 0;

    private ViewPager mViewPager;
    private FragmentStatePagerAdapter adapter;
    private List<TabLayoutEntity> list = new ArrayList<>();
    private boolean isChangeBold = true;//是否改变粗细

    private OnTabLayoutListener mListener;//选项卡监听

    public UITabLayout(@NonNull Context context) {
        super(context);
        this.initUI(context, null);
    }

    public UITabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context, attrs);
    }

    public UITabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UITabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UITabLayoutStyle);

            int mNormalTextColorId = typedArray.getResourceId(R.styleable.UITabLayoutStyle_tab_normal_text_color, 0);
            if(mNormalTextColorId > 0){
                this.mNormalTextColor = ContextCompat.getColor(context, mNormalTextColorId);
            }else{
                this.mNormalTextColor = typedArray.getColor(R.styleable.UITabLayoutStyle_tab_normal_text_color, 0x0);
            }

            int mSelectedTextColorId = typedArray.getResourceId(R.styleable.UITabLayoutStyle_tab_selected_text_color, 0);
            if(mSelectedTextColorId > 0){
                this.mSelectedTextColor = ContextCompat.getColor(context, mSelectedTextColorId);
            }else{
                this.mSelectedTextColor = typedArray.getColor(R.styleable.UITabLayoutStyle_tab_selected_text_color, 0x0);
            }

            int mNormalTextSizeId = typedArray.getResourceId(R.styleable.UITabLayoutStyle_tab_normal_text_size, 0);
            if(mNormalTextSizeId > 0){
                this.mNormalTextSize = typedArray.getResources().getDimensionPixelSize(mNormalTextSizeId);
            }else{
                this.mNormalTextSize = typedArray.getDimensionPixelSize(R.styleable.UITabLayoutStyle_tab_normal_text_size, 0);
            }

            int mSelectedTextSizeId = typedArray.getResourceId(R.styleable.UITabLayoutStyle_tab_selected_text_size, 0);
            if(mSelectedTextSizeId > 0){
                this.mSelectedTextSize = typedArray.getResources().getDimensionPixelSize(mSelectedTextSizeId);
            }else{
                this.mSelectedTextSize = typedArray.getDimensionPixelSize(R.styleable.UITabLayoutStyle_tab_selected_text_size, 0);
            }

            int mIndicatorIconColorId = typedArray.getResourceId(R.styleable.UITabLayoutStyle_tab_indicator_icon_color, 0);
            if(mIndicatorIconColorId > 0){
                this.mIndicatorIconColor = ContextCompat.getColor(context, mIndicatorIconColorId);
            }else{
                this.mIndicatorIconColor = typedArray.getColor(R.styleable.UITabLayoutStyle_tab_indicator_icon_color, Color.parseColor("#FFFFFF"));
            }

            this.mIndicatorIconResId = typedArray.getResourceId(R.styleable.UITabLayoutStyle_tab_indicator_icon, 0);

            int isCenterId = typedArray.getResourceId(R.styleable.UITabLayoutStyle_tab_is_center, 0);
            if(isCenterId > 0){
                this.isCenter = typedArray.getResources().getBoolean(isCenterId);
            }else{
                this.isCenter = typedArray.getBoolean(R.styleable.UITabLayoutStyle_tab_is_center, false);
            }

            int isFixedWidthId = typedArray.getResourceId(R.styleable.UITabLayoutStyle_tab_is_fixed_width, 0);
            if(isFixedWidthId > 0){
                this.isFixedWidth = typedArray.getResources().getBoolean(isFixedWidthId);
            }else{
                this.isFixedWidth = typedArray.getBoolean(R.styleable.UITabLayoutStyle_tab_is_fixed_width, false);
            }

            int mTabItemWdthId = typedArray.getResourceId(R.styleable.UITabLayoutStyle_tab_item_width, 0);
            if(mTabItemWdthId > 0){
                this.mTabItemWdth = typedArray.getResources().getDimensionPixelSize(mTabItemWdthId);
            }else{
                this.mTabItemWdth = typedArray.getDimensionPixelSize(R.styleable.UITabLayoutStyle_tab_item_width, 0);
            }

            this.mTabLayoutId = typedArray.getResourceId(R.styleable.UITabLayoutStyle_tab_view, 0);

            this.mTabItemLayoutId = typedArray.getResourceId(R.styleable.UITabLayoutStyle_tab_item_view, 0);

            typedArray.recycle();
        }

        if(this.mTabLayoutId > 0) {
            View itemView = this.inflater.inflate(mTabLayoutId, null);
            this.mTabLayout = itemView.findViewById(R.id.tab_layout);
            this.addView(itemView);
        }else {
            View itemView = this.inflater.inflate(R.layout.view_tablayout, null);
            this.mTabLayout = itemView.findViewById(R.id.tab_layout);
            this.addView(itemView);
        }

        if(this.mIndicatorIconResId > 0) {
            this.mTabLayout.setSelectedTabIndicator(mIndicatorIconResId);
        }

        this.mTabLayout.setSelectedTabIndicatorColor(mIndicatorIconColor);
    }


    public TabLayout getTabLayout() {
        return mTabLayout;
    }

    /**
     * 设置ViewPager控件
     * setViewPager要在setTabList之前调用
     * @param mViewPager
     * @param adapter
     */
    public void setViewPager(ViewPager mViewPager, FragmentStatePagerAdapter adapter){
        this.mViewPager = mViewPager;
        this.adapter = adapter;
    }


    public void setTabList(List<TabLayoutEntity> tabList){
        this.list.clear();
        if(tabList != null && !tabList.isEmpty()) {
            this.list.addAll(tabList);
        }
        this.initTabLayout();
    }

    /**
     * 设置当前选项卡
     * @param position
     */
    public void setCurrentItem(int position){
        int lastPos = this.position;
        if(lastPos != position) {
            this.position = position;
            if(mTabItemVies.size() > position && mTabItemVies.get(position) != null) {
                setTabSelected(position, mTabItemVies.get(position));
                setTabUnselected(lastPos, mTabItemVies.get(lastPos));
            }
        }

    }

    public void setChangeBold(boolean changeBold) {
        isChangeBold = changeBold;
    }

    private void setTabSelected(int position, View view){
        if(mViewPager != null){
            mViewPager.setCurrentItem(position, false);
        }
        this.position = position;
        if (view != null && view instanceof ViewGroup) {

            TextView mTvItem = view.findViewById(R.id.tv_tab_name);
            mTvItem.setTextColor(mSelectedTextColor);
            mTvItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSelectedTextSize);
            if(isChangeBold) {
                mTvItem.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }


            if(!isFixedWidth && !this.isCenter) {
                TextPaint paint = new TextPaint();
                paint.setTextSize(mSelectedTextSize);
                paint.setFakeBoldText(true);
                int itemWidth = (int) Math.ceil(paint.measureText(mTvItem.getText().toString())) + ScreenUtils.dip2px(10);
                ViewGroup.LayoutParams params = mTvItem.getLayoutParams();
                params.width = itemWidth;
                mTvItem.requestLayout();
            }
        }

        if(mListener != null) {
            mListener.onTabSelected(view, position, list.get(position));
        }
    }


    private void setTabUnselected(int position, View view) {

        if (view != null && view instanceof ViewGroup) {
            TextView mTvItem = view.findViewById(R.id.tv_tab_name);
            mTvItem.setTextColor(mNormalTextColor);
            mTvItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize);
            if(isChangeBold) {
                mTvItem.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }

            if(!isFixedWidth && !this.isCenter) {
                TextPaint paint = new TextPaint();
                paint.setTextSize(mNormalTextSize);
                paint.setFakeBoldText(false);
                int itemWidth = (int) Math.ceil(paint.measureText(mTvItem.getText().toString())) + ScreenUtils.dip2px(10);
                ViewGroup.LayoutParams params = mTvItem.getLayoutParams();
                params.width = itemWidth;
                mTvItem.requestLayout();
            }
        }

        if(mListener != null) {
            mListener.onTabUnselected(view, position, list.get(position));
        }
    }

    private void initTabLayout() {
        try {
            if (this.list.size() > 0) {
                this.mTabLayout.removeAllTabs();
                this.mTabItemVies.clear();

                if(this.mViewPager != null && adapter != null) {
                    this.mViewPager.setOffscreenPageLimit(1);
                    this.mViewPager.setAdapter(this.adapter);
                    this.adapter.notifyDataSetChanged();
                    this.mTabLayout.setupWithViewPager(this.mViewPager);
                }

                this.mTabLayout.setTabIndicatorFullWidth(false);
                this.mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        setTabSelected(tab.getPosition(), tab.getCustomView());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        setTabUnselected(tab.getPosition(), tab.getCustomView());
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

                int gap = ScreenUtils.dip2px(20);
                int gapCount = (this.list.size() + 1) * gap;
                int totalWidth = 0;
                this.textList.clear();
                for (int i = 0; i < this.list.size(); i++) {
                    String mTabName = this.list.get(i).getName();
                    Object iconUrl = this.list.get(i).getIcon();
                    TabLayout.Tab tab = null;
                    if(mViewPager != null) {
                        tab = mTabLayout.getTabAt(i);
                    }else {
                        tab = mTabLayout.newTab();
                        mTabLayout.addTab(tab);
                    }
                    if (tab != null) {

                        if(!isFixedWidth) {
                            TextPaint paint = new TextPaint();
                            paint.setTextSize(mSelectedTextSize);
                            paint.setFakeBoldText(true);
                            float trueItemWidth = paint.measureText(mTabName) + ScreenUtils.dip2px(10);//TextView显示的真实宽度
                            totalWidth += trueItemWidth;
                        }else {
                            //固定宽
                            float trueItemWidth = mTabItemWdth;//TextView显示的真实宽度
                            totalWidth += trueItemWidth;
                        }

                        View customView = createTabItemView(i, mTabName, iconUrl);
                        if(mListener != null) mListener.onCreateTabItem(customView, i, this.list.get(i));
                        tab.setCustomView(customView);
                        this.mTabItemVies.put(i, customView);
                    }
                }

                this.mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
                this.mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

                if(this.isCenter) {
                    int screenWidth = ScreenUtils.getScreenWidth();
                    if (totalWidth + gapCount < screenWidth) {
                        int tabAddWidth = (screenWidth - (totalWidth + gapCount)) / list.size();
                        for (TextView mTvItem : textList) {
                            ViewGroup.LayoutParams params = mTvItem.getLayoutParams();
                            params.width += tabAddWidth;
                            mTvItem.requestLayout();
                        }
                    }
                }

                if(this.mViewPager != null){
                    this.mViewPager.setCurrentItem(position);
                    this.mViewPager.setVisibility(View.VISIBLE);
                }
            }

        }catch (Exception e) {
            LogUtils.i("MY-LOG", "异常->" + e.getMessage());
            e.printStackTrace();
        }
    }

    private View createTabItemView(int position, String tabName, Object iconUrl) {

        View view = null;
        if(mTabItemLayoutId != 0) {
            view = this.inflater.inflate(mTabItemLayoutId, null);
        }else {
            view = this.inflater.inflate(R.layout.view_default_tablayout_item, null);
        }

        TextView mTvTabItem = view.findViewById(R.id.tv_tab_name);
        ImageView mIvTabIcon = view.findViewById(R.id.iv_tab_icon);
        if(!isFixedWidth) {
            TextPaint paint = new TextPaint();
            if(position != this.position) {
                paint.setTextSize(mNormalTextSize);
                paint.setFakeBoldText(false);
            }else {
                paint.setTextSize(mSelectedTextSize);
                paint.setFakeBoldText(true);
            }

            int itemWidth = (int) Math.ceil(paint.measureText(tabName)) + ScreenUtils.dip2px(10);
            ViewGroup.LayoutParams params = mTvTabItem.getLayoutParams();
            params.width = itemWidth;
            mTvTabItem.requestLayout();
        }

        mTvTabItem.setText(tabName);
        if(mIvTabIcon != null) {
            if(iconUrl instanceof String) {
                GlideUtils.loadImage((String)iconUrl, mIvTabIcon);
            }else if(iconUrl instanceof Integer){
                GlideUtils.loadImage((Integer)iconUrl, mIvTabIcon);
            }
        }

        if (position == this.position) {
            mTvTabItem.setTextColor(mSelectedTextColor);
            mTvTabItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSelectedTextSize);
            if(isChangeBold) {
                mTvTabItem.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }

        } else {
            mTvTabItem.setTextColor(mNormalTextColor);
            mTvTabItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize);
            if(isChangeBold) {
                mTvTabItem.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }

        }

        textList.add(mTvTabItem);
        return view;
    }

    public void setOnTabLayoutListener(OnTabLayoutListener mListener) {
        this.mListener = mListener;
    }

    public interface OnTabLayoutListener {

        public void onTabSelected(View tabItemView, int position, TabLayoutEntity item);

        public void onTabUnselected(View tabItemView, int position, TabLayoutEntity item);

        public void onCreateTabItem(View tabItemView, int position, TabLayoutEntity item);
    }
}
