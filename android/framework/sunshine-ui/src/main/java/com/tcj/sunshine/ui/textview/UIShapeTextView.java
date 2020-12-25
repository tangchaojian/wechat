package com.tcj.sunshine.ui.textview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.tcj.sunshine.ui.R;


/**
 * 自定义TextView，可以显示各种背景，圆角，形状
 */
public class UIShapeTextView extends AppCompatTextView {

    private Context context;
    private int colorNormal;
    private int colorPressed;
    private int colorUnable;

    private int colorLineNormal;
    private int colorLinePressed;
    private int colorLineUnable;

    private int colorTextNormal;
    private int colorTextPressed;
    private int colorTextUnable;

    private int radius;
    private int leftTopRadius;
    private int leftBottomRadius;
    private int rightTopRadius;
    private int rightBottomRadius;

    private boolean isShowborder;
    private int lineWidth;
    private int shape = GradientDrawable.RECTANGLE;

    public UIShapeTextView(Context context) {
        super(context);
        this.initUI(context, null);
    }

    public UIShapeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context, attrs);
    }

    public UIShapeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs){
        this.context = context;
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShapeViewStyle);
            int colorNormalId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_normal_color, 0);
            if(colorNormalId > 0){
                this.colorNormal = ContextCompat.getColor(context, colorNormalId);
            }else{
                this.colorNormal = typedArray.getColor(R.styleable.ShapeViewStyle_shape_normal_color, 0x0);
            }

            int colorPressedId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_pressed_color, 0);
            if(colorPressedId > 0){
                this.colorPressed = ContextCompat.getColor(context, colorPressedId);
            }else{
                this.colorPressed = typedArray.getColor(R.styleable.ShapeViewStyle_shape_pressed_color, 0x0);
            }

            int colorUnableId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_unable_color, 0);
            if(colorUnableId > 0){
                this.colorUnable = ContextCompat.getColor(context, colorUnableId);
            }else{
                this.colorUnable = typedArray.getColor(R.styleable.ShapeViewStyle_shape_unable_color, 0x0);
            }

            int radiusId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_radius, 0);
            if(radiusId > 0){
                this.radius = typedArray.getResources().getDimensionPixelSize(radiusId);
            }else{
                this.radius = typedArray.getDimensionPixelSize(R.styleable.ShapeViewStyle_shape_radius, 0);
            }

            int leftTopRadiusId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_left_top_radius, 0);
            if(leftTopRadiusId > 0){
                this.leftTopRadius = typedArray.getResources().getDimensionPixelSize(leftTopRadiusId);
            }else{
                this.leftTopRadius = typedArray.getDimensionPixelSize(R.styleable.ShapeViewStyle_shape_left_top_radius, 0);
            }

            int leftBottomRadiusId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_left_bottom_radius, 0);
            if(leftBottomRadiusId > 0){
                this.leftBottomRadius = typedArray.getResources().getDimensionPixelSize(leftBottomRadiusId);
            }else{
                this.leftBottomRadius = typedArray.getDimensionPixelSize(R.styleable.ShapeViewStyle_shape_left_bottom_radius, 0);
            }

            int rightTopRadiusId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_right_top_radius, 0);
            if(rightTopRadiusId > 0){
                this.rightTopRadius = typedArray.getResources().getDimensionPixelSize(rightTopRadiusId);
            }else{
                this.rightTopRadius = typedArray.getDimensionPixelSize(R.styleable.ShapeViewStyle_shape_right_top_radius, 0);
            }


            int rightBottomRadiusId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_right_bottom_radius, 0);
            if(rightBottomRadiusId > 0){
                this.rightBottomRadius = typedArray.getResources().getDimensionPixelSize(rightBottomRadiusId);
            }else{
                this.rightBottomRadius = typedArray.getDimensionPixelSize(R.styleable.ShapeViewStyle_shape_right_bottom_radius, 0);
            }

            if(this.leftTopRadius == 0)this.leftTopRadius = this.radius;
            if(this.leftBottomRadius == 0)this.leftBottomRadius = this.radius;
            if(this.rightTopRadius == 0)this.rightTopRadius = this.radius;
            if(this.rightBottomRadius == 0)this.rightBottomRadius = this.radius;

            int isShowborderId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_show_border, 0);
            if(isShowborderId > 0){
                this.isShowborder = typedArray.getResources().getBoolean(isShowborderId);
            }else{
                this.isShowborder = typedArray.getBoolean(R.styleable.ShapeViewStyle_shape_show_border, false);
            }

            int shapeId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape, 0);
            int value = 0;
            if(shapeId > 0){
                value = typedArray.getResources().getInteger(shapeId);
            }else{
                value = typedArray.getInteger(R.styleable.ShapeViewStyle_shape, 0);
            }

            switch (value) {
                case 0:
                case 3:
                    shape = GradientDrawable.RECTANGLE;
                    break;
                case 1:
                    shape = GradientDrawable.LINE;
                    break;
                case 2:
                    shape = GradientDrawable.OVAL;
                    break;
                case 4:
                    shape = GradientDrawable.RING;
                    break;
            }

            if(this.isShowborder) {
                int colorLineNormalId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_normal_line_color, 0);
                if(colorLineNormalId > 0){
                    this.colorLineNormal = ContextCompat.getColor(context, colorLineNormalId);
                }else{
                    this.colorLineNormal = typedArray.getColor(R.styleable.ShapeViewStyle_shape_normal_line_color, 0);
                }

                int colorLinePressedId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_pressed_line_color, 0);
                if(colorLinePressedId > 0){
                    this.colorLinePressed = ContextCompat.getColor(context, colorLinePressedId);
                }else{
                    this.colorLinePressed = typedArray.getColor(R.styleable.ShapeViewStyle_shape_pressed_line_color, 0);
                }

                int colorLineUnableId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_unable_line_color, 0);
                if(colorLineUnableId > 0){
                    this.colorLineUnable = ContextCompat.getColor(context, colorLineUnableId);
                }else{
                    this.colorLineUnable = typedArray.getColor(R.styleable.ShapeViewStyle_shape_unable_line_color, 0);
                }

                int lineWidthId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_line_width, 0);
                if(lineWidthId > 0) {
                    this.lineWidth = typedArray.getResources().getDimensionPixelSize(lineWidthId);
                }else{
                    this.lineWidth = typedArray.getDimensionPixelSize(R.styleable.ShapeViewStyle_shape_line_width, 0);
                }
            }

            //文字颜色变化
            int colorTextNormalId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_normal_text_color, 0);
            if(colorTextNormalId > 0){
                this.colorTextNormal = ContextCompat.getColor(context, colorTextNormalId);
            }else{
                this.colorTextNormal = typedArray.getColor(R.styleable.ShapeViewStyle_shape_normal_text_color, 0);
            }

            int colorTextPressedId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_pressed_text_color, 0);
            if(colorTextPressedId > 0){
                this.colorTextPressed = ContextCompat.getColor(context, colorTextPressedId);
            }else{
                this.colorTextPressed = typedArray.getColor(R.styleable.ShapeViewStyle_shape_pressed_text_color, 0);
            }

            int colorTextUnableId = typedArray.getResourceId(R.styleable.ShapeViewStyle_shape_unable_text_color, 0);
            if(colorTextUnableId > 0){
                this.colorTextUnable = ContextCompat.getColor(context, colorTextUnableId);
            }else{
                this.colorTextUnable = typedArray.getColor(R.styleable.ShapeViewStyle_shape_unable_text_color, 0);
            }


            typedArray.recycle();

            /**
             * setCornerRadii
             * 1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
             */
            GradientDrawable shapeNormal = new GradientDrawable();
            shapeNormal.setShape(shape);

            shapeNormal.setCornerRadii(new float[]{
                    leftTopRadius, leftTopRadius,
                    rightTopRadius, rightTopRadius,
                    rightBottomRadius, rightBottomRadius,
                    leftBottomRadius, leftBottomRadius
            });



            GradientDrawable shapePressed = new GradientDrawable();
            shapePressed.setShape(shape);
            shapePressed.setCornerRadii(new float[]{
                    leftTopRadius, leftTopRadius,
                    rightTopRadius, rightTopRadius,
                    rightBottomRadius, rightBottomRadius,
                    leftBottomRadius, leftBottomRadius
            });

            GradientDrawable shapeUnable = new GradientDrawable();
            shapeUnable.setShape(shape);
            shapeUnable.setCornerRadii(new float[]{
                    leftTopRadius, leftTopRadius,
                    rightTopRadius, rightTopRadius,
                    rightBottomRadius, rightBottomRadius,
                    leftBottomRadius, leftBottomRadius
            });


            GradientDrawable shapeChecked = new GradientDrawable();
            shapeChecked.setShape(shape);
            shapeChecked.setCornerRadii(new float[]{
                    leftTopRadius, leftTopRadius,
                    rightTopRadius, rightTopRadius,
                    rightBottomRadius, rightBottomRadius,
                    leftBottomRadius, leftBottomRadius
            });


            if(colorPressed == 0)colorPressed = colorNormal;
            if(colorUnable == 0)colorUnable = colorNormal;

            shapeNormal.setColor(colorNormal);
            shapePressed.setColor(colorPressed);
            shapeUnable.setColor(colorUnable);

            if(isShowborder) {

                if(colorLinePressed == 0)colorLinePressed = colorLineNormal;
                if(colorLineUnable == 0)colorLineUnable = colorLineNormal;

                shapeNormal.setStroke(lineWidth, colorLineNormal);
                shapePressed.setStroke(lineWidth, colorLinePressed);
                shapeUnable.setStroke(lineWidth, colorLineUnable);
            }

            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(new int[]{android.R.attr.state_pressed}, shapePressed);
            drawable.addState(new int[]{-android.R.attr.state_enabled}, shapeUnable);

            drawable.addState(new int[]{}, shapeNormal);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                this.setBackground(drawable);
            }else {
                this.setBackgroundDrawable(drawable);
            }

            if(colorTextNormal != 0 || colorTextPressed != 0 || colorTextUnable != 0) {

                if(colorTextPressed == 0) colorTextPressed = colorTextNormal;
                if(colorTextPressed == 0) colorTextUnable = colorTextNormal;

                int[] colors = new int[] {colorTextPressed, colorTextUnable, colorTextNormal};
                int[][] states = new int[3][];
                states[0] = new int[] { android.R.attr.state_pressed};
                states[1] = new int[] { -android.R.attr.state_enabled};
                states[2] = new int[] {};
                ColorStateList colorList = new ColorStateList(states, colors);
                this.setTextColor(colorList);
            }
        }

    }
}
