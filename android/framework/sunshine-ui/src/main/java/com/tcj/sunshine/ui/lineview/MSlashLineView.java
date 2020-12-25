package com.tcj.sunshine.ui.lineview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import android.util.AttributeSet;
import android.view.View;

import com.tcj.sunshine.ui.R;


/**
 * 斜线控件
 */
public class MSlashLineView extends View{

    private Context context;
    private Paint paint;
    private int mLineColor = -1;//线条颜色
    private int mLeftLineColor = -1;//左边线条颜色
    private int mRightLineColor = -1;//右边线条颜色
    private float mLineWidth = 1;//线条宽
    private int type = 0;//0:反斜杠"\",1:斜杠"/"

    public MSlashLineView(Context context) {
        super(context);
        this.initUI(context, null);
    }

    public MSlashLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context, attrs);
    }

    public MSlashLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context, attrs);
    }

    @SuppressLint("NewApi")
    public MSlashLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    /**
     *    <declare-styleable name="MSlashLineViewStyle">
     *         <attr name="slash_left_line_color" format="reference|color"></attr>
     *         <attr name="slash_right_line_color" format="reference|color"></attr>
     *         <attr name="slash_line_color" format="reference|color"></attr>
     *         <attr name="slash_line_width" format="reference|color"></attr>
     *         <attr name="slash_line_type">
     *             <enum name="leftTop_to_rightBottom" value="0" />
     *             <enum name="rightTop_to_leftBottom" value="1" />
     *         </attr>
     *     </declare-styleable>
     */
    private void initUI(Context context, AttributeSet attrs) {

        this.context = context;
        if(attrs != null){
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MSlashLineViewStyle);
            int mLineColorId = array.getResourceId(R.styleable.MSlashLineViewStyle_slash_line_color, 0);
            if(mLineColorId > 0){
                this.mLineColor = array.getResources().getColor(mLineColorId);
            }else{
                this.mLineColor = array.getColor(R.styleable.MSlashLineViewStyle_slash_line_color, -1);
            }

            int mLeftLineColorId = array.getResourceId(R.styleable.MSlashLineViewStyle_slash_left_line_color, 0);
            if(mLeftLineColorId > 0){
                this.mLeftLineColor = array.getResources().getColor(mLeftLineColorId);
            }else{
                this.mLeftLineColor = array.getColor(R.styleable.MSlashLineViewStyle_slash_left_line_color, -1);
            }

            int mRightLineColorId = array.getResourceId(R.styleable.MSlashLineViewStyle_slash_right_line_color, 0);
            if(mRightLineColorId > 0){
                this.mRightLineColor = array.getResources().getColor(mRightLineColorId);
            }else{
                this.mRightLineColor = array.getColor(R.styleable.MSlashLineViewStyle_slash_right_line_color, -1);
            }

            int mLineWidthId = array.getResourceId(R.styleable.MSlashLineViewStyle_slash_line_width, 0);
            if(mLineWidthId > 0){
                this.mLineWidth = array.getResources().getDimension(mLineWidthId);
            }else{
                this.mLineWidth = array.getDimension(R.styleable.MSlashLineViewStyle_slash_line_width, 1);
            }

            int mLineTypeId = array.getResourceId(R.styleable.MSlashLineViewStyle_slash_line_type, -1);
            if(mLineTypeId > 0){
                this.type = array.getResources().getInteger(mLineTypeId);
            }else{
                this.type = array.getInteger(R.styleable.MSlashLineViewStyle_slash_line_type, 0);
            }

            array.recycle();
        }

        if(mLineColor == -1) {
            mLineColor = Color.BLACK;
        }

        if(mLeftLineColor == -1) mLeftLineColor = mLineColor;
        if(mRightLineColor == -1) mRightLineColor = mLineColor;

        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(mLineWidth);
        this.paint.setColor(mLineColor);
        this.paint.setAntiAlias(true);
        this.paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = this.getMeasuredWidth();
        int height = this.getMeasuredHeight();

        Point point1;
        Point point2;
        Point point3;
        if(type == 0) {
            point1 = new Point(0, 0);//第一个点
            point2 = new Point((int)(0.5f * width), (int)(0.5f * height));//第二个点
            point3 = new Point(width, height);//第三个点
        }else {
            point1 = new Point(0, height);//第一个点
            point2 = new Point((int)(0.5f * width), (int)(0.5f * height));//第二个点
            point3 = new Point(width, 0);//第三个点
        }


        this.paint.setColor(mLineColor);// 左边线条颜色
        canvas.drawLine(point1.x, point1.y, point2.x, point2.y, paint);// 画线

        this.paint.setColor(mRightLineColor);//右边线条颜色
        canvas.drawLine(point2.x, point2.y, point3.x, point3.y, paint);// 画线
    }

    public void setLeftLineColor(@ColorRes int color){
        this.mLeftLineColor = ResourcesCompat.getColor(this.getContext().getResources(), color, null);
        this.postInvalidate();
    }

    public void setRightColor(@ColorRes int color){
        this.mRightLineColor = ResourcesCompat.getColor(this.getContext().getResources(), color, null);
        this.postInvalidate();
    }
}
