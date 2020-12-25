package com.tcj.sunshine.ui.viewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.ui.R;

public class UICropFrameLayout extends FrameLayout {

    public float[] radii = new float[8];   // top-left, top-right, bottom-right, bottom-left
    public Path mClipPath;                 // 剪裁区域路径
    public Paint mPaint;                   // 画笔
    public Region mAreaRegion;             // 内容区域
    public RectF mLayer;                   // 画布图层大小

    private int radius;

    private int leftTopRadius;
    private int leftBottomRadius;
    private int rightTopRadius;
    private int rightBottomRadius;

    private int shape;
    private boolean isShowborder;//是否显示边
    private int borderWidth;//边的宽
    private int borderColor;


    public UICropFrameLayout(Context context) {
        super(context);
        this.initUI(context, null);
    }

    public UICropFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context, attrs);
    }

    public UICropFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UICropFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context, attrs);
    }


    private void initUI(Context context, AttributeSet attrs){
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CropViewStyle);

            int radiusId = typedArray.getResourceId(R.styleable.CropViewStyle_crop_radius, 0);
            if(radiusId > 0){
                this.radius = typedArray.getResources().getDimensionPixelSize(radiusId);
            }else{
                this.radius = typedArray.getDimensionPixelSize(R.styleable.CropViewStyle_crop_radius, 0);
            }

            int leftTopRadiusId = typedArray.getResourceId(R.styleable.CropViewStyle_crop_left_top_radius, 0);
            if(leftTopRadiusId > 0){
                this.leftTopRadius = typedArray.getResources().getDimensionPixelSize(leftTopRadiusId);
            }else{
                this.leftTopRadius = typedArray.getDimensionPixelSize(R.styleable.CropViewStyle_crop_left_top_radius, 0);
            }

            int leftBottomRadiusId = typedArray.getResourceId(R.styleable.CropViewStyle_crop_left_bottom_radius, 0);
            if(leftBottomRadiusId > 0){
                this.leftBottomRadius = typedArray.getResources().getDimensionPixelSize(leftBottomRadiusId);
            }else{
                this.leftBottomRadius = typedArray.getDimensionPixelSize(R.styleable.CropViewStyle_crop_left_bottom_radius, 0);
            }

            int rightTopRadiusId = typedArray.getResourceId(R.styleable.CropViewStyle_crop_right_top_radius, 0);
            if(rightTopRadiusId > 0){
                this.rightTopRadius = typedArray.getResources().getDimensionPixelSize(rightTopRadiusId);
            }else{
                this.rightTopRadius = typedArray.getDimensionPixelSize(R.styleable.CropViewStyle_crop_right_top_radius, 0);
            }


            int rightBottomRadiusId = typedArray.getResourceId(R.styleable.CropViewStyle_crop_right_bottom_radius, 0);
            if(rightBottomRadiusId > 0){
                this.rightBottomRadius = typedArray.getResources().getDimensionPixelSize(rightBottomRadiusId);
            }else{
                this.rightBottomRadius = typedArray.getDimensionPixelSize(R.styleable.CropViewStyle_crop_right_bottom_radius, 0);
            }

            if(this.leftTopRadius == 0)this.leftTopRadius = this.radius;
            if(this.leftBottomRadius == 0)this.leftBottomRadius = this.radius;
            if(this.rightTopRadius == 0)this.rightTopRadius = this.radius;
            if(this.rightBottomRadius == 0)this.rightBottomRadius = this.radius;

            int isShowborderId = typedArray.getResourceId(R.styleable.CropViewStyle_crop_show_border, 0);
            if(isShowborderId > 0){
                this.isShowborder = typedArray.getResources().getBoolean(isShowborderId);
            }else{
                this.isShowborder = typedArray.getBoolean(R.styleable.CropViewStyle_crop_show_border, false);
            }

            int shapeId = typedArray.getResourceId(R.styleable.CropViewStyle_crop_shape, 0);
            int value = 0;
            if(shapeId > 0){
                value = typedArray.getResources().getInteger(shapeId);
            }else{
                value = typedArray.getInteger(R.styleable.CropViewStyle_crop_shape, 0);
            }

            switch (value) {
                case 0:
                case 1:
                    shape = GradientDrawable.RECTANGLE;
                    break;
                case 2:
                    shape = GradientDrawable.OVAL;
                    break;
            }

            if(this.isShowborder) {
                int borderColorId = typedArray.getResourceId(R.styleable.CropViewStyle_crop_border_color, 0);
                if(borderColorId > 0){
                    this.borderColor = ContextCompat.getColor(context, borderColorId);
                }else{
                    this.borderColor = typedArray.getColor(R.styleable.CropViewStyle_crop_border_color, 0);
                }

                int borderWidthId = typedArray.getResourceId(R.styleable.CropViewStyle_crop_border_width, 0);
                if(borderWidthId > 0) {
                    this.borderWidth = typedArray.getResources().getDimensionPixelSize(borderWidthId);
                }else{
                    this.borderWidth = typedArray.getDimensionPixelSize(R.styleable.CropViewStyle_crop_border_width, 0);
                }
            }

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

        LogUtils.i("sunshine-ui", "leftTopRadius->" +leftTopRadius);

        //图层
        this.mLayer = new RectF();
        //路径
        this.mClipPath = new Path();
        //面积
        this.mAreaRegion = new Region();
        //画笔
        this.mPaint = new Paint();
        this.mPaint.setColor(borderColor);
        this.mPaint.setAntiAlias(true);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //更新有效面积
        this.updateAreaRegion(w, h);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.setDrawFilter(new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        canvas.saveLayer(mLayer, null, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        this.onClipDraw(canvas);
        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        canvas.save();
        canvas.clipPath(mClipPath);
        super.onDraw(canvas);
        canvas.restore();
    }


    /**
     * 裁剪之后，除有效面积之外，不能获得点击事件
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN && !this.mAreaRegion.contains((int) ev.getX(), (int) ev.getY())) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 更新有效面积
     * @param w
     * @param h
     */
    private void updateAreaRegion(int w, int h) {
        this.mLayer.set(0, 0, w, h);
        RectF rect = new RectF(0, 0, w, h);
        this.mClipPath.reset();
        if(shape == GradientDrawable.OVAL) {
            float diameter = w >= h ? h : w;//直径
            float radius = diameter / 2;
            PointF center = new PointF(w / 2, h / 2);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                mClipPath.addCircle(center.x, center.y, radius, Path.Direction.CW);

                mClipPath.moveTo(0, 0);  // 通过空操作让Path区域占满画布
                mClipPath.moveTo(w, h);
            } else {
                float y = h / 2 - radius;
                mClipPath.moveTo(rect.left, y);
                mClipPath.addCircle(center.x, y + radius, radius, Path.Direction.CW);
            }
        }else {
            mClipPath.addRoundRect(rect, radii, Path.Direction.CW);
        }

        Region clip = new Region((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
        mAreaRegion.setPath(mClipPath, clip);
    }


    /**
     * 裁剪
     * @param canvas
     */
    private void onClipDraw(Canvas canvas){

        if (isShowborder) {
            // 支持半透明描边，将与描边区域重叠的内容裁剪掉
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(borderWidth);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mClipPath, mPaint);
            // 绘制描边
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            mPaint.setColor(borderColor);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mClipPath, mPaint);
        }

        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawPath(mClipPath, mPaint);
        } else {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            final Path path = new Path();
            path.addRect(0, 0, (int) mLayer.width(), (int) mLayer.height(), Path.Direction.CW);
            path.op(mClipPath, Path.Op.DIFFERENCE);
            canvas.drawPath(path, mPaint);
        }
    }
}
