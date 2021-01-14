package com.tcj.sunshine.ui.viewgroup;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * 索引指示器控件
 */
public class IndexerView extends LinearLayout {

    private Context context;
    private LayoutInflater inflater;
    private int normalIndexLayoutId;
    private int checkedIndexLayoutId;
    private int size = 0;

    private View checkedIndexView;
    private int index = 0;

    public IndexerView(Context context) {
        super(context);
        this.initUI(context);
    }

    public IndexerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context);
    }

    public IndexerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public IndexerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context);
    }

    private void initUI(Context context){

        this.context = context;
        this.inflater = LayoutInflater.from(context);

        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(Gravity.CENTER);

    }

    public IndexerView setNoramlIndexLayoutId(int layoutId) {
        this.normalIndexLayoutId = layoutId;
        return this;
    }

    public IndexerView setCheckedIndexLayoutId(int layoutId) {
        this.checkedIndexLayoutId = layoutId;
        return this;
    }

    public IndexerView setIndexSize(int size) {
        this.size = size;
        return this;
    }

    public void build(){
        this.removeAllViews();
        if(this.normalIndexLayoutId > 0 && this.checkedIndexLayoutId > 0 && size > 0) {
            for (int i = 0; i < size; i++) {
                View itemView = null;
                if(i == 0) {
                    itemView = inflater.inflate(checkedIndexLayoutId, null, false);
                    index = 0;
                    this.checkedIndexView = itemView;
                    this.addView(itemView);
                }else {
                    itemView = inflater.inflate(normalIndexLayoutId, null, false);
                    this.addView(itemView);
                }
            }
        }
    }

    public void updateIndex(int position) {

        if(index == position) return;
        if(checkedIndexView == null)return;

        this.removeView(checkedIndexView);
        this.addView(checkedIndexView, position);
        index = position;
    }
}
