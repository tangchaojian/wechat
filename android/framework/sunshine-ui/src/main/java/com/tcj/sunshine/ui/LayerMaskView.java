package com.tcj.sunshine.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

/**
 * 图层蒙板,用于引导页
 */
public class LayerMaskView extends View {

    private Context context;
    private Paint paint;
    private int width;
    private int height;

    private int bgColor;
    private List<Layer> list = new ArrayList<>();
    private List<Layer> bitmaps = new ArrayList<>();

    private PorterDuffXfermode xfermode;
    private boolean lock = false;

    public LayerMaskView(Context context) {
        super(context);
        this.initUI(context);
    }

    public LayerMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context);
    }

    public LayerMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LayerMaskView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context);
    }

    private void initUI(Context context){
        this.context = context;
        this.setClickable(true);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setColor(Color.WHITE);
        this.paint.setStrokeWidth(1);
        this.xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(lock) {
            this.drawLayer(canvas);
        }
    }

    private void drawLayer(Canvas canvas) {

        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        int save = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        this.paint.setColor(this.bgColor);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        //画出背景
        canvas.drawRect(new Rect(0, 0, width, height), paint);

        if(!list.isEmpty()) {
            this.paint.setXfermode(this.xfermode);

            for (Layer layer : list) {
                if(layer.type == 0) {
                    //画矩形，或者圆角矩形
                    if(layer.radius > 0) {
                        //圆角矩形
                        RectF rect = new RectF(layer.x, layer.y, layer.x + layer.width, layer.y + layer.height);
                        canvas.drawRoundRect(rect, layer.radius, layer.radius, this.paint);
                    }else {
                        //矩形
                        RectF rect = new RectF(layer.x, layer.y, layer.x + layer.width, layer.y + layer.height);
                        canvas.drawRect(rect, this.paint);
                    }
                }else if(layer.type == 1) {
                    //圆形
                    canvas.drawCircle(layer.x, layer.y, layer.radius, this.paint);
                }
            }

            this.paint.setXfermode(null);

            if(!bitmaps.isEmpty()) {
                for (Layer layer : bitmaps) {
                    if(layer.bitmap == null)continue;

                    Rect src = new Rect(0, 0, layer.bitmap.getWidth(), layer.bitmap.getHeight());
                    RectF dst = new RectF(layer.x, layer.y, layer.x + layer.width, layer.y + layer.height);
                    canvas.drawBitmap(layer.bitmap, src, dst, this.paint);
                }
            }

            canvas.restoreToCount(save);
        }
    }


    public void clear(){
        this.list.clear();
        this.bitmaps.clear();
    }

    /**
     * 画圆
     * @param radius
     * @param x
     * @param y
     */
    public void drawCircle(int radius, int x , int y) {
        Layer layer = new Layer();
        layer.type = 1;
        layer.radius = radius;
        layer.x = x;
        layer.y = y;
        this.list.add(layer);
    }


    /**
     * 画矩形
     * @param width
     * @param height
     * @param x
     * @param y
     */
    public void drawRect(int width, int height, int x, int y){
        Layer layer = new Layer();
        layer.type = 0;
        layer.width = width;
        layer.height = height;
        layer.x = x;
        layer.y = y;
        this.list.add(layer);
    }


    /**
     * 画圆角矩形
     * @param width
     * @param height
     * @param radius
     * @param x
     * @param y
     */
    public void drawRoundRect(int width, int height, int radius, int x, int y){
        Layer layer = new Layer();
        layer.type = 0;
        layer.width = width;
        layer.height = height;
        layer.radius = radius;
        layer.x = x;
        layer.y = y;
        this.list.add(layer);
    }


    /**
     * 画bitmap
     * @param bitmap
     * @param width
     * @param height
     * @param x
     * @param y

     */
    public void drawBitmap(Bitmap bitmap, int width, int height, int x, int y) {
        Layer layer = new Layer();
        layer.type = 2;
        layer.width = width;
        layer.height = height;
        layer.bitmap = bitmap;
        layer.x = x;
        layer.y = y;
        this.bitmaps.add(layer);
    }

    /**
     * 开始画
     */
    public void startDraw(){
        this.lock = true;
        this.invalidate();
    }

    /**
     * 画背景
     * @param bgColor
     */
    public void drawBackground(@ColorInt int bgColor) {
        this.bgColor = bgColor;
    }


    private class Layer {

        private int type;//0：矩形、圆角矩形（椭圆）;   1:圆形;
        private int width;
        private int height;
        private int radius;//半径
        private int x;
        private int y;
        private Bitmap bitmap;
    }


}
