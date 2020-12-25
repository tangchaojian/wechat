package com.tcj.sunshine.ui.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.tcj.sunshine.ui.R;

public class RoundImageView extends AppCompatImageView {

    private float[] radii = new float[8];   // top-left, top-right, bottom-right, bottom-left
    private Path mClipPath;                 // 剪裁区域路径
    private RectF rect;

    private int radius;
    private int leftTopRadius;
    private int leftBottomRadius;
    private int rightTopRadius;
    private int rightBottomRadius;

    public RoundImageView(Context context) {
        super(context);
        this.initUI(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageViewStyle);

            int radiusId = typedArray.getResourceId(R.styleable.RoundImageViewStyle_round_radius, 0);
            if (radiusId > 0) {
                this.radius = typedArray.getResources().getDimensionPixelSize(radiusId);
            } else {
                this.radius = typedArray.getDimensionPixelSize(R.styleable.RoundImageViewStyle_round_radius, 0);
            }

            int leftTopRadiusId = typedArray.getResourceId(R.styleable.RoundImageViewStyle_round_left_top_radius, 0);
            if (leftTopRadiusId > 0) {
                this.leftTopRadius = typedArray.getResources().getDimensionPixelSize(leftTopRadiusId);
            } else {
                this.leftTopRadius = typedArray.getDimensionPixelSize(R.styleable.RoundImageViewStyle_round_left_top_radius, 0);
            }

            int leftBottomRadiusId = typedArray.getResourceId(R.styleable.RoundImageViewStyle_round_left_bottom_radius, 0);
            if (leftBottomRadiusId > 0) {
                this.leftBottomRadius = typedArray.getResources().getDimensionPixelSize(leftBottomRadiusId);
            } else {
                this.leftBottomRadius = typedArray.getDimensionPixelSize(R.styleable.RoundImageViewStyle_round_left_bottom_radius, 0);
            }

            int rightTopRadiusId = typedArray.getResourceId(R.styleable.RoundImageViewStyle_round_right_top_radius, 0);
            if (rightTopRadiusId > 0) {
                this.rightTopRadius = typedArray.getResources().getDimensionPixelSize(rightTopRadiusId);
            } else {
                this.rightTopRadius = typedArray.getDimensionPixelSize(R.styleable.RoundImageViewStyle_round_right_top_radius, 0);
            }

            int rightBottomRadiusId = typedArray.getResourceId(R.styleable.RoundImageViewStyle_round_right_bottom_radius, 0);
            if (rightBottomRadiusId > 0) {
                this.rightBottomRadius = typedArray.getResources().getDimensionPixelSize(rightBottomRadiusId);
            } else {
                this.rightBottomRadius = typedArray.getDimensionPixelSize(R.styleable.RoundImageViewStyle_round_right_bottom_radius, 0);
            }

            if (this.leftTopRadius == 0) this.leftTopRadius = this.radius;
            if (this.leftBottomRadius == 0) this.leftBottomRadius = this.radius;
            if (this.rightTopRadius == 0) this.rightTopRadius = this.radius;
            if (this.rightBottomRadius == 0) this.rightBottomRadius = this.radius;

            typedArray.recycle();
        }

        this.radii[0] = leftTopRadius;
        this.radii[1] = leftTopRadius;

        this.radii[2] = rightTopRadius;
        this.radii[3] = rightTopRadius;

        this.radii[4] = rightBottomRadius;
        this.radii[5] = rightBottomRadius;

        this.radii[6] = leftBottomRadius;
        this.radii[7] = leftBottomRadius;

        this.mClipPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(this.rect == null && getWidth() > 0 && getHeight() > 0){
            this.rect = new RectF(0, 0, getWidth(), getHeight());
        }
        mClipPath.reset();
        if(this.rect != null) {
            this.mClipPath.addRoundRect(rect, radii, Path.Direction.CW);
            canvas.clipPath(mClipPath);//设置可显示的区域，canvas四个角会被剪裁掉
        }
        super.onDraw(canvas);
    }

    public void setRadius(int width) {
        for (int i = 0; i < radii.length; i++) {
            radii[i] = width;
        }
        invalidate();
    }
}
