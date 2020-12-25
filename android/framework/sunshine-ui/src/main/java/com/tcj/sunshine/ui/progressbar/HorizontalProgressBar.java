package com.tcj.sunshine.ui.progressbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.ui.R;


@SuppressLint("NewApi")
public class HorizontalProgressBar extends View {

    private Context context;
    private int mBackColor;
    private int mBeforeColor;
    private int mLineColor;
    private int mTextColor;
    private int mOverTextColor;

    private int outsizeBorderWidth;
    private int round;

    private boolean mIsShowProgress = false;
    private boolean mIsRound = false;//注意是进度条是否圆角，不是外面的进度框是否圆角
    private int mTextSize;

    private Paint paint;

    private float maxProgress = 100.0f;
    private float currentProgress = 0.0f;

    private int width = 0;
    private int height = 0;

    private RectF mBackRectF;

    private RectF mLineRectF;

    private RectF mForeRectF;

    private RectF mTextRectF;

    private PorterDuffXfermode xfermode;

    private Paint fill;
    private Paint mTextPaint;

    public HorizontalProgressBar(Context context) {
        super(context);
        this.initUI(context, null);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context, attrs);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context, attrs);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs){
        this.context = context;
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressBar);
            int mBackColorId = typedArray.getResourceId(R.styleable.ProgressBar_progressbar_back_color, 0);
            if(mBackColorId > 0){
                this.mBackColor = typedArray.getResources().getColor(mBackColorId);
            }else{
                this.mBackColor = typedArray.getColor(R.styleable.ProgressBar_progressbar_back_color, 0xFF9BCD05);
            }

            int mBeforeColorId = typedArray.getResourceId(R.styleable.ProgressBar_progressbar_before_color, 0);
            if(mBeforeColorId > 0){
                this.mBeforeColor = typedArray.getResources().getColor(mBeforeColorId);
            }else{
                this.mBeforeColor = typedArray.getColor(R.styleable.ProgressBar_progressbar_before_color, 0xFF9BCD05);
            }

            int mLineColorId = typedArray.getResourceId(R.styleable.ProgressBar_progressbar_line_color, 0);
            if(mLineColorId > 0){
                this.mLineColor = typedArray.getResources().getColor(mLineColorId);
            }else{
                this.mLineColor = typedArray.getColor(R.styleable.ProgressBar_progressbar_line_color, 0xFF9BCD05);
            }

            int mTextColorId = typedArray.getResourceId(R.styleable.ProgressBar_progressbar_text_color, 0);
            if(mTextColorId > 0){
                this.mTextColor = typedArray.getResources().getColor(mTextColorId);
            }else{
                this.mTextColor = typedArray.getColor(R.styleable.ProgressBar_progressbar_text_color, 0xFF000000);
            }

            int mOverTextColorId = typedArray.getResourceId(R.styleable.ProgressBar_progressbar_over_text_color, 0);
            if(mOverTextColorId > 0){
                this.mOverTextColor = typedArray.getResources().getColor(mOverTextColorId);
            }else{
                this.mOverTextColor = typedArray.getColor(R.styleable.ProgressBar_progressbar_over_text_color, 0xFFFFFFFF);
            }

            int mOutsideBorderWidth = typedArray.getResourceId(R.styleable.ProgressBar_progressbar_outside_border_width, 0);
            if(mOutsideBorderWidth > 0){
                this.outsizeBorderWidth = typedArray.getResources().getDimensionPixelSize(mOutsideBorderWidth);
            }else{
                this.outsizeBorderWidth = typedArray.getDimensionPixelSize(R.styleable.ProgressBar_progressbar_outside_border_width, 0);
            }

            int mRoundId = typedArray.getResourceId(R.styleable.ProgressBar_progressbar_round, 0);
            if(mRoundId > 0){
                this.round = typedArray.getResources().getDimensionPixelSize(mRoundId);
            }else{
                this.round = typedArray.getDimensionPixelSize(R.styleable.ProgressBar_progressbar_round, 0);
            }

            int mIsRoundId = typedArray.getResourceId(R.styleable.ProgressBar_progressbar_progress_is_round, 0);
            if(mIsRoundId > 0){
                this.mIsRound = typedArray.getResources().getBoolean(mIsRoundId);
            }else {
                this.mIsRound = typedArray.getBoolean(R.styleable.ProgressBar_progressbar_progress_is_round, false);
            }

            int mShowProgressId = typedArray.getResourceId(R.styleable.ProgressBar_progressbar_is_show_progress, 0);
            if(mShowProgressId > 0){
                this.mIsShowProgress = typedArray.getResources().getBoolean(mShowProgressId);
            }else {
                this.mIsShowProgress = typedArray.getBoolean(R.styleable.ProgressBar_progressbar_is_show_progress, false);
            }



            int mTextSizeId = typedArray.getResourceId(R.styleable.ProgressBar_progressbar_text_size, 0);
            if(mTextSizeId > 0){
                this.mTextSize = typedArray.getResources().getDimensionPixelSize(mTextSizeId);
            }else{
                this.mTextSize = typedArray.getDimensionPixelSize(R.styleable.ProgressBar_progressbar_text_size, 40);
            }


            this.maxProgress = typedArray.getInteger(R.styleable.ProgressBar_progressbar_max_progress, 100);

            this.currentProgress = typedArray.getInteger(R.styleable.ProgressBar_progressbar_current_progress, 0);

            this.xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

            typedArray.recycle();
        }
        this.paint = new Paint();

        this.paint.setStrokeWidth(1);
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);

        this.fill = new Paint();

        this.mLineRectF = new RectF();
        this.mBackRectF = new RectF();
        this.mForeRectF = new RectF();
        this.mTextRectF = new RectF();

        if(mIsShowProgress) {
            this.mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.mTextPaint.setTextSize(mTextSize);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if(widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.EXACTLY){
            this.width = widthSpecSize;
        }else{
            this.width = 0;
        }

        if(heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED){
            this.height = ScreenUtils.dip2px(15f);
        }else{
            this.height = heightSpecSize;
        }

        this.setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));

        this.paint.setColor(mLineColor);
        this.paint.setStyle(Style.FILL);

        this.mLineRectF.left = 0;
        this.mLineRectF.top = 0;
        this.mLineRectF.right = width;
        this.mLineRectF.bottom = height;

        //划出外框
        canvas.drawRoundRect(this.mLineRectF, round, round, paint);

        int save1 = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);

        fill.setAntiAlias(true);
        fill.setDither(true);

        fill.setColor(mBackColor);
        fill.setStyle(Style.FILL);

        this.mBackRectF.left = outsizeBorderWidth;
        this.mBackRectF.top = outsizeBorderWidth;
        this.mBackRectF.right = width - outsizeBorderWidth;
        this.mBackRectF.bottom = height -outsizeBorderWidth;
        //画出背景
        canvas.drawRoundRect(this.mBackRectF, round, round, fill);


        //画出进度
        fill.setColor(mBeforeColor);

        float progressWidth = (currentProgress / maxProgress) * (width- outsizeBorderWidth);
        this.mForeRectF.left = outsizeBorderWidth;
        this.mForeRectF.top = outsizeBorderWidth;
        this.mForeRectF.right = progressWidth;
        this.mForeRectF.bottom = height - outsizeBorderWidth;

        fill.setXfermode(this.xfermode);
        if(this.mIsRound) {
            canvas.drawRoundRect(this.mForeRectF, round, round, fill);
        }else {
            canvas.drawRect(this.mForeRectF, fill);
        }
        fill.setXfermode(null);

        this.paint.setAntiAlias(true);
        this.paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.paint.setDither(true);

        canvas.restoreToCount(save1);

        if(mIsShowProgress) {

            //save as new layer
            int save2 = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);

            mTextPaint.setStyle(Style.FILL);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setDither(true);

            mTextPaint.setColor(mTextColor);

            String text = (int) currentProgress + "%";
            float textSize = mTextPaint.measureText(text);

            Paint.FontMetricsInt font = mTextPaint.getFontMetricsInt();
            float centerY = height / 2.0f;
            float baseLineX = (width - textSize) * 0.5f;
            float baseLineY = centerY - (font.bottom - font.top) / 2 - font.top;

            canvas.drawText(text, baseLineX, baseLineY, mTextPaint);

            if (progressWidth > baseLineX) {

                mTextPaint.setColor(mOverTextColor);

                this.mTextRectF.left = (width - textSize) * 0.5f;
                this.mTextRectF.top = outsizeBorderWidth;
                this.mTextRectF.right = progressWidth;
                this.mTextRectF.bottom = height - outsizeBorderWidth;

                mTextPaint.setXfermode(this.xfermode);
                canvas.drawRoundRect(this.mTextRectF, round, round, mTextPaint);
                mTextPaint.setXfermode(null);
            }

            canvas.restoreToCount(save2);
        }
    }

    public void setMaxProgress(int maxProgress){
        this.maxProgress = maxProgress;
    }

    public void setCurrentProgress(int currentProgress){
        this.currentProgress = currentProgress;
        this.invalidate();
    }

    public void setBackColor(int color) {
        this.mBackColor = color;
    }

    public void setBeforeColor(int color) {
        this.mBeforeColor = color;
    }
    public void setLineColor(int color) {
        this.mLineColor = color;
    }

}
