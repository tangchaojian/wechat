package com.tcj.sunshine.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * 弧形View
 */
public class ArcDownView extends View {
    private Context context;
    private int mWidth;
    private int mHeight;

    private int mArcHeight; //圆弧的高度
    private int mBgColor;   //背景颜色
    private Paint mPaint;  //画笔

    private Rect rect = new Rect(0, 0, 0, 0);//普通的矩形
    private Path path = new Path();//用来绘制曲面

    public ArcDownView(Context context) {
        this(context, null);
        this.initUI(context, null);
    }

    public ArcDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        this.initUI(context, attrs);
    }

    public ArcDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {
        this.context = context;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcDownViewStyle);
            int mBgColorId = typedArray.getResourceId(R.styleable.ArcDownViewStyle_arc_down_background, 0);
            if (mBgColorId > 0) {
                this.mBgColor = ContextCompat.getColor(context, mBgColorId);
            } else {
                this.mBgColor = typedArray.getColor(R.styleable.ArcDownViewStyle_arc_down_background, 0x0);
            }

            int mArcHeightId = typedArray.getResourceId(R.styleable.ArcDownViewStyle_arc_down_height, 0);
            if(mArcHeightId > 0) {
                this.mArcHeight = typedArray.getResources().getDimensionPixelSize(mArcHeightId);
            }else{
                this.mArcHeight = typedArray.getDimensionPixelSize(R.styleable.ArcDownViewStyle_arc_down_height, 0);
            }
            typedArray.recycle();
        }

        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
    }

    public void setArcHeight(int mArcHeight) {
        this.mArcHeight = mArcHeight;
    }

    public void setBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置成填充
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBgColor);

        //绘制矩形
        rect.set(0, 0, mWidth, mHeight - mArcHeight);
        canvas.drawRect(rect, mPaint);

        //绘制路径
        path.moveTo(0, mHeight - mArcHeight);
        path.quadTo(mWidth >> 1, mHeight, mWidth, mHeight - mArcHeight);
        canvas.drawPath(path, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        }
        setMeasuredDimension(mWidth, mHeight);
    }
}
