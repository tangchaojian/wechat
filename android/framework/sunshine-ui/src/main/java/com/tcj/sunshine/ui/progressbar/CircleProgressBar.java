package com.tcj.sunshine.ui.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;

import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.tcj.sunshine.ui.R;


/**
 * 圆形进度条
 */
public class CircleProgressBar extends View {

    private Context context;
    private Paint paint;
    private int strokeWidth;
    private int progressBarColor;
    private int total;
    private int current;

    public CircleProgressBar(Context context) {
        super(context);
        this.init(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        /**
         * <attr name="circle_progress_bar_width" format="dimension"/>
         *         <attr name="circle_progress_bar_color" format="color"/>
         *         <attr name="circle_progress_bar_current" format="integer"/>
         *         <attr name="circle_progress_bar_total" format="integer"/>
         */

        this.context = context;
        this.strokeWidth = getMeasuredWidth();
        this.progressBarColor = Color.parseColor("#FFD534");
        if(attrs != null) {
            TypedArray mTypeArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, 0, 0);
            this.strokeWidth = mTypeArray.getDimensionPixelSize(R.styleable.CircleProgressBar_circle_progress_bar_width, 10);
            this.progressBarColor = mTypeArray.getColor(R.styleable.CircleProgressBar_circle_progress_bar_color, Color.parseColor("#FFD534"));
            this.current = mTypeArray.getInt(R.styleable.CircleProgressBar_circle_progress_bar_current, 0);
            this.total = mTypeArray.getInt(R.styleable.CircleProgressBar_circle_progress_bar_total, 100);
            mTypeArray.recycle();
        }

        //初始化画笔
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setColor(this.progressBarColor);
        this.paint.setStrokeWidth(this.strokeWidth);
        this.paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();

        RectF oval = new RectF(this.strokeWidth, this.strokeWidth, width - strokeWidth, width - strokeWidth);

        if (current <= 100) {
            float angle = current * 1.0f / total * 360;
            canvas.drawArc(oval, -90, angle, false, this.paint);
        } else {
            canvas.drawArc(oval, -90, 360, false, this.paint);
        }
    }

    public void setCurrent(int current) {
        this.current = current;
        this.invalidate();
    }
}
