package com.tcj.sunshine.ui.viewgroup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价等级控件
 */
public class EvaluationLevelView extends LinearLayout {

    private Context context;
    private Drawable normalDrawable;
    private Drawable selectedDrawable;
    private int width;
    private int height;
    private int gap;
    private int num;
    private int level;

    private boolean enableClick = false;

    private OnStartLevelClickListener mListener;

    private List<ImageView> starList = new ArrayList<>();

    public EvaluationLevelView(Context context) {
        super(context);
        this.initUI(context, null);
    }

    public EvaluationLevelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context, attrs);
    }

    public EvaluationLevelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context, attrs);
    }

    @SuppressLint("NewApi")
    public EvaluationLevelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {
        this.context = context;
        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EvaluationLevelViewStyle);
            int normalImgSrcId = typedArray.getResourceId(R.styleable.EvaluationLevelViewStyle_star_normal_img_src, 0);
            if (normalImgSrcId > 0) {
                this.normalDrawable = ContextCompat.getDrawable(context, normalImgSrcId);
            } else {
                this.normalDrawable = ContextCompat.getDrawable(context, R.mipmap.icon_star_normal);
            }

            int selectedImgSrcId = typedArray.getResourceId(R.styleable.EvaluationLevelViewStyle_star_selected_img_src, 0);
            if (selectedImgSrcId > 0) {
                this.selectedDrawable = ContextCompat.getDrawable(context, selectedImgSrcId);
            } else {
                this.selectedDrawable = ContextCompat.getDrawable(context, R.mipmap.icon_star_selected);
            }


            int widthId = typedArray.getResourceId(R.styleable.EvaluationLevelViewStyle_star_width, 0);
            if(widthId > 0) {
                this.width = typedArray.getResources().getDimensionPixelSize(widthId);
            }else{
                this.width = typedArray.getDimensionPixelSize(R.styleable.EvaluationLevelViewStyle_star_width, 0);
            }

            int heightId = typedArray.getResourceId(R.styleable.EvaluationLevelViewStyle_star_height, 0);
            if(heightId > 0) {
                this.height = typedArray.getResources().getDimensionPixelSize(heightId);
            }else{
                this.height = typedArray.getDimensionPixelSize(R.styleable.EvaluationLevelViewStyle_star_height, 0);
            }

            int gapId = typedArray.getResourceId(R.styleable.EvaluationLevelViewStyle_star_gap, 0);
            if(gapId > 0) {
                this.gap = typedArray.getResources().getDimensionPixelSize(gapId);
            }else{
                this.gap = typedArray.getDimensionPixelSize(R.styleable.EvaluationLevelViewStyle_star_gap, 0);
            }

            LogUtils.i("sunshine-ui", "gap->" + gap);

            this.num = typedArray.getInteger(R.styleable.EvaluationLevelViewStyle_star_num, 0);
            this.level = typedArray.getInteger(R.styleable.EvaluationLevelViewStyle_star_level, 0);

            this.enableClick = typedArray.getBoolean(R.styleable.EvaluationLevelViewStyle_star_enable_click, false);

            typedArray.recycle();

            if(this.normalDrawable != null && this.selectedDrawable != null && num > 0) {
                this.create();
            }
        }
    }

    private void create(){
        this.removeAllViews();
        this.starList.clear();
        for (int i = 0; i < num; i++) {
            ImageView mIvStar = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            if(i == 0){
                params.leftMargin = 0;
            }else {
                params.leftMargin = gap;
            }
            mIvStar.setLayoutParams(params);

            if(i <= (level - 1)) {
                mIvStar.setImageDrawable(selectedDrawable);
            }else {
                mIvStar.setImageDrawable(normalDrawable);
            }
            mIvStar.setOnClickListener(new OnStarClickListener((i + 1)));

            this.addView(mIvStar);
            starList.add(mIvStar);
        }
    }

    public void setLevel(int level){
        this.level = level;

        if(starList == null || starList.isEmpty())return;
        for (int i = 0; i < starList.size(); i++) {
            ImageView mIvStar = starList.get(i);
            if(i < level) {
                mIvStar.setImageDrawable(selectedDrawable);
            }else {
                mIvStar.setImageDrawable(normalDrawable);
            }
        }
    }

    public int getLevel(){
        return this.level;
    }

    public void setEnableClick(boolean enableClick) {
        this.enableClick = enableClick;
    }

    public void setOnStartLevelClickListener(OnStartLevelClickListener mListener) {
        this.mListener = mListener;
    }

    private class OnStarClickListener implements View.OnClickListener {
        private int levelNum;

        public OnStarClickListener(int levelNum) {
            this.levelNum = levelNum;
        }

        @Override
        public void onClick(View v) {
            if(enableClick){
                setLevel(levelNum);
                if(mListener != null) mListener.onStarLevel(levelNum);
            }
        }
    }

    public interface OnStartLevelClickListener {
        void onStarLevel(int level);
    }
}
