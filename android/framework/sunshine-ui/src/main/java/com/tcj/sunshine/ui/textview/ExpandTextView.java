package com.tcj.sunshine.ui.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.tcj.sunshine.tools.StringUtils;
import com.tcj.sunshine.ui.R;


/**
 * 展开/折叠TextView
 */
public class ExpandTextView extends AppCompatTextView {

    private int mTextViewWidth;//TextView的宽度
    private int maxLines;//超过maxLines行数，内容折叠
    private String expandText;//点击展开末尾显示的文章
    private String collapseText;//点击折叠末尾显示的文章
    private int endTextColor;
    boolean isExpand;
    boolean isInit = false;

    public ExpandTextView(Context context) {
        super(context);
        this.initUI(context, null);
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context, attrs);
    }

    public ExpandTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {
        if(attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ExpandTextViewStyle);

            int mTextViewWidthId = ta.getResourceId(R.styleable.ExpandTextViewStyle_expand_textview_width, 0);
            if(mTextViewWidthId > 0){
                this.mTextViewWidth = ta.getResources().getDimensionPixelSize(mTextViewWidthId);
            }else{
                this.mTextViewWidth = ta.getDimensionPixelSize(R.styleable.ExpandTextViewStyle_expand_textview_width, 0x0);
            }

            int mMaxLinesId = ta.getResourceId(R.styleable.ExpandTextViewStyle_expand_textview_maxLines, 0);
            if(mMaxLinesId > 0){
                this.maxLines = ta.getResources().getInteger(mMaxLinesId);
            }else{
                this.maxLines = ta.getInteger(R.styleable.ExpandTextViewStyle_expand_textview_maxLines, 0x0);
            }

            int mExpandTextId = ta.getResourceId(R.styleable.ExpandTextViewStyle_expand_textview_expand_text, 0);
            if(mExpandTextId > 0){
                this.expandText = ta.getResources().getString(mExpandTextId);
            }else{
                this.expandText = ta.getString(R.styleable.ExpandTextViewStyle_expand_textview_expand_text);
            }

            int mCollapseTextId = ta.getResourceId(R.styleable.ExpandTextViewStyle_expand_textview_collapse_text, 0);
            if(mCollapseTextId > 0){
                this.collapseText = ta.getResources().getString(mCollapseTextId);
            }else{
                this.collapseText = ta.getString(R.styleable.ExpandTextViewStyle_expand_textview_collapse_text);
            }

            int mEndTextColorId = ta.getResourceId(R.styleable.ExpandTextViewStyle_expand_textview_end_text_color, 0);
            if(mEndTextColorId > 0){
                this.endTextColor =ContextCompat.getColor(context, mEndTextColorId);
            }else{
                this.endTextColor = ta.getColor(R.styleable.ExpandTextViewStyle_expand_textview_end_text_color, 0x0);
            }

            int mIsExpandId = ta.getResourceId(R.styleable.ExpandTextViewStyle_expand_textview_is_expand, 0);
            if(mIsExpandId > 0){
                this.isExpand = context.getResources().getBoolean(mIsExpandId);
            }else{
                this.isExpand = ta.getBoolean(R.styleable.ExpandTextViewStyle_expand_textview_is_expand, false);
            }

            ta.recycle();
        }

        if(!this.isInit) {
            this.isInit = true;
            toggleEllipsize(getText());
        }

    }

    /**
     * 更新显示内容，不能用setText()
     */
    public void updateText(CharSequence text){
        this.toggleEllipsize(text);
    }


    /**
     * 显示展开或者折叠
     */
    private void toggleEllipsize(CharSequence text) {
        //获取文本内容
        CharSequence content = this.getText();
        if(TextUtils.isEmpty(content)) return;

        TextPaint paint = this.getPaint();
        //每行文本的布局宽度
        //实例化StaticLayout 传入相应参数
        StaticLayout staticLayout = new StaticLayout(content , paint, mTextViewWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        //判断content是行数是否超过最大限制行数maxLines行
        if (staticLayout.getLineCount() > maxLines) {
            //展开后的文本内容
            String expandStr = content + "    " + collapseText;//展开的内容显示“收起>>”
            SpannableString ssExpand = new SpannableString(expandStr);
            //给收起两个字设成蓝色
            ssExpand.setSpan(new ForegroundColorSpan(endTextColor), expandStr.length() - collapseText.length(), expandStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            //收起内容
            int index = staticLayout.getLineStart(maxLines) - 1;
            //定义收起后的文本内容
            String collapseStr = content.subSequence(0, index - 4) + "...." + expandText;//折叠的内容显示“展开>>”
            SpannableString ssCollapse = new SpannableString(collapseStr);
            //给查看全部设成蓝色
            ssCollapse.setSpan(new ForegroundColorSpan(endTextColor), collapseStr.length() - expandText.length(), collapseStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //设置收起后的文本内容
            //将textview设成选中状态 true用来表示文本未展示完全的状态,false表示完全展示状态，用于点击时的判断
            if(isExpand) {
                //展开的内容
                this.setText(ssExpand);
                this.setSelected(false);
            }else {
                //收起的内容
                this.setText(ssCollapse);
                this.setSelected(true);
            }


            this.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.isSelected()) {
                        //如果是收起的状态
                        ExpandTextView.this.setText(ssExpand);
                        ExpandTextView.this.setSelected(false);
                    } else {
                        //如果是展开的状态
                        ExpandTextView.this.setText(ssCollapse);
                        ExpandTextView.this.setSelected(true);
                    }
                }
            });

        } else {
            //没有超过 直接设置文本
            ExpandTextView.this.setText(content);
            ExpandTextView.this.setOnClickListener(null);
        }
    }

}
