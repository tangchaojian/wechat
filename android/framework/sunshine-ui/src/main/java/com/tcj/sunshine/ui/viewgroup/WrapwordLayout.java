package com.tcj.sunshine.ui.viewgroup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tcj.sunshine.ui.R;


/**
 * 自动换行ViewGroup
 */
public class WrapwordLayout extends ViewGroup  {

    private Context context;
    private LayoutInflater inflater;

    //自定义属性
    private int horizontalSpacing; //dip
    private int verticalSpacing;
    private int maxRow;

    private BaseAdapter adapter;//适配器
    private MDataSetObserver mDataSetObserver;

    private SparseArray<View> mViewPools = new SparseArray<>();

    public WrapwordLayout(Context context) {
        super(context);
        this.initUI(context, null);
    }

    public WrapwordLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context, attrs);
    }

    public WrapwordLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context, attrs);
    }

    @SuppressLint("NewApi")
    public WrapwordLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context, attrs);
    }


    private void initUI(final Context context, AttributeSet attrs) {

        this.context = context;
        this.inflater = LayoutInflater.from(context);

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WrapwordLayout);

            int horizontalSpacingId = array.getResourceId(R.styleable.WrapwordLayout_horizontalSpacing, 0);
            if(horizontalSpacingId > 0){
                this.horizontalSpacing = (int)array.getResources().getDimension(horizontalSpacingId);
            }else{
                this.horizontalSpacing = (int)array.getDimension(R.styleable.WrapwordLayout_horizontalSpacing, 0);
            }

            int verticalSpacingId = array.getResourceId(R.styleable.WrapwordLayout_verticalSpacing, 0);
            if(verticalSpacingId > 0){
                this.verticalSpacing = (int)array.getResources().getDimension(verticalSpacingId);
            }else{
                this.verticalSpacing = (int)array.getDimension(R.styleable.WrapwordLayout_verticalSpacing, 0);
            }

            int maxRowId = array.getResourceId(R.styleable.WrapwordLayout_max_row, 0);
            if(maxRowId > 0) {
                this.maxRow = (int)array.getResources().getInteger(maxRowId);
            }else {
                this.maxRow = (int)array.getInteger(R.styleable.WrapwordLayout_max_row, 5);
            }
            array.recycle();
        }
    }

    public void setAdapter(BaseAdapter adapter){

        if(adapter != null){

            if(mDataSetObserver != null && this.adapter != null){
                this.adapter.unregisterDataSetObserver(mDataSetObserver);
            }

            this.mDataSetObserver = new MDataSetObserver();
            this.adapter = adapter;
            this.adapter.registerDataSetObserver(this.mDataSetObserver);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //为所有的标签childView计算宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        //获取高的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //建议的高度
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //布局的宽度采用建议宽度（match_parent或者size），如果设置wrap_content也是match_parent的效果
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int height ;
        if (heightMode == MeasureSpec.EXACTLY) {
            //如果高度模式为EXACTLY（match_perent或者size），则使用建议高度
            height = heightSize;
        } else {
            //其他情况下（AT_MOST、UNSPECIFIED）需要计算计算高度
            int childCount = getChildCount();
            if(childCount<=0){
                height = 0;   //没有标签时，高度为0
            }else{
                int row = 1;  // 标签行数
                int widthSpace = width;// 当前行右侧剩余的宽度
                for(int i = 0;i<childCount; i++){
                    View view = getChildAt(i);
                    //获取标签宽度
                    int childW = view.getMeasuredWidth();
                    if(widthSpace >= childW ){
                        //如果剩余的宽度大于此标签的宽度，那就将此标签放到本行
                        widthSpace -= childW;
                    }else{
                        row ++;    //增加一行
                        //如果剩余的宽度不能摆放此标签，那就将此标签放入一行
                        widthSpace = width-childW;
                    }
                    //减去标签左右间距
                    widthSpace -= horizontalSpacing;
                    if(row > maxRow) {
                        break;
                    }
                }
                //由于每个标签的高度是相同的，所以直接获取第一个标签的高度即可
                int childH = getChildAt(0).getMeasuredHeight();
                //最终布局的高度=标签高度*行数+行距*(行数-1)
                height = (childH * row) + verticalSpacing * (row-1);

            }
        }

        //设置测量宽度和测量高度
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int row = 0;
        int right = 0;   // 标签相对于布局的右侧位置
        int botom;       // 标签相对于布局的底部位置
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            int childW = childView.getMeasuredWidth();
            int childH = childView.getMeasuredHeight();
            //右侧位置=本行已经占有的位置+当前标签的宽度
            right += childW;
            //底部位置=已经摆放的行数*（标签高度+行距）+当前标签高度
            botom = row * (childH + verticalSpacing) + childH;
            // 如果右侧位置已经超出布局右边缘，跳到下一行
            // if it can't drawing on a same line , skip to next line
            if (right > (r - horizontalSpacing)){
                row++;
                right = childW;
                botom = row * (childH + verticalSpacing) + childH;
            }
            childView.layout(right - childW, botom - childH,right,botom);

            right += horizontalSpacing;
        }
    }

    private class MDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            //刷新view,重新绘制
            updateUI();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }

    private void updateUI(){

        this.removeAllViews();

        if(this.adapter == null)return;

        for(int i = 0; i < this.adapter.getCount(); i++){

            View view = this.mViewPools.get(i);
            view = this.adapter.getView(i, view, null);

            this.addView(view);
        }

        if(this.mViewPools.size() > this.adapter.getCount()){
            for (int i = this.adapter.getCount(); i < this.mViewPools.size(); i++){
                this.mViewPools.remove(i);//移除多余的view
            }
        }
    }

}
