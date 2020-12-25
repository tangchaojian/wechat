package com.tcj.sunshine.ui.viewgroup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;

import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.ui.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by Stefan Lau on 2020/1/8.
 */
public class AnimateRelativeLayout extends RelativeLayout {

    private Context context;
    private RectF mLayer;
    private Paint paint;
    private Bitmap snowIcon;//雪花icon
    private int width;//控件宽
    private int height;//控件高
    private boolean lock = false;
    private boolean stop = false;
    private int type = -1; //默认没动画
    private long animTime = 5000;//默认5秒

    private int alpha = 255;//透明度

    //雪花
    private ArrayList<Snow> snows = new ArrayList<>();
    private Date mLastCreateDate = new Date();//上一次创建雪花的时间
    private SnowHandler snowHandler;
    //烟花
    private static final int MAX_FIREWORK_NUM = 5;
    private ArrayList<Firework> fireworks;
    private FireworkHandler fireworkHandler;

    //花瓣
    private static final int MAX_FLOWER_NUM = 15;
    private ArrayList<Flower> flowers;
    private Bitmap bitmap;


    public AnimateRelativeLayout(@NonNull Context context) {
        super(context);
        this.initUI(context);
    }

    public AnimateRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context);
    }

    public AnimateRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AnimateRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context);
    }

    private void initUI(Context context) {
        this.context = context;
        this.mLayer = new RectF();
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setColor(Color.WHITE);
        this.paint.setStrokeWidth(1);

    }

    private void setAnimate(int animType) {
        this.type = animType;
        if (type == 0) {
            this.snowIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_snow);
            this.snowHandler = new SnowHandler();
        } else if (type == 1) {
            fireworks = new ArrayList<>();
            this.fireworkHandler = new FireworkHandler();
            this.addFirework(3);
            postDelayed(() -> this.addFirework(2), 50);
            ViewCompat.postInvalidateOnAnimation(this);
        } else if(type == 2){
            this.flowers = new ArrayList<>();
            this.bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_flower);
            this.stop = false;
            this.addFlower(MAX_FLOWER_NUM, true);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public AnimateRelativeLayout setType(int type, long animTime) {
        this.animTime = animTime;
        if (this.type != type) {
            if (snowHandler != null) snowHandler.removeCallbacksAndMessages(null);
            if (fireworkHandler != null) fireworkHandler.removeCallbacksAndMessages(null);
            setAnimate(type);
        }
        return this;
    }

    public void start() {
        this.lock = true;
        this.stop = false;
        this.invalidate();
    }


    public void stop() {
        this.stop = true;
        if (fireworkHandler != null) fireworkHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;

        //显示背景图（当前不在主线程）
        if (this.width != 0 && this.height != 0) {
            updateAreaRegion(w, h);
//            postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    start();
//                    setAnimate(2);
//                }
//            }, 3000);

        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.saveLayer(mLayer, null, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        if (lock) {

            if (stop) {
                alpha -= 5;
                if (alpha < 0) alpha = 0;
                paint.setAlpha(alpha);
            } else {
                paint.setAlpha(255);
            }

            if (alpha == 0) {
                //停止
                this.lock = false;
                if (snowHandler != null) snowHandler.removeCallbacksAndMessages(null);
                if (fireworkHandler != null) fireworkHandler.removeCallbacksAndMessages(null);
            }

            if (type == 0) {
                //下雪
                this.drawSnow(canvas);
            } else if (type == 1) {
                //烟花
                this.drawFirework(canvas);
            } else {
                //花瓣
                this.drawFlower(canvas);
            }

        }
        canvas.restore();
    }

    private void updateAreaRegion(int w, int h) {
        this.mLayer.set(0, 0, w, h);
    }


    //================================雪花========================================

    /**
     * 构建一个雪花类
     */
    public class Snow {

        public int radius;
        public int x;
        public int y;
        public int color;

        public Snow(int radius) {
            this.x = 0;
            this.y = 0;
            this.radius = radius;
            this.color = Color.WHITE;
        }

        void draw(Canvas canvas, Paint paint, Bitmap snowIcon) {
            canvas.save();
            canvas.translate(this.x, this.y);
            paint.setColor(this.color);
            paint.setStyle(Paint.Style.FILL);

            //x, y, radius, start_angle, end_angle, anti-clockwise
//            canvas.drawCircle(0, 0, this.radius, paint);
            int width = snowIcon.getWidth();
            float scale = 2 * radius * 1.0f / width;

            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            canvas.drawBitmap(snowIcon, matrix, paint);
            canvas.restore();
        }
    }


    /**
     * 移动雪花
     */
    private void moveSnow(Canvas canvas, Snow snow, Iterator<Snow> iterator) {
        snow.y += snow.radius / 3;
        if (snow.y > height) {
            //如果该雪球出了边界，则移除这个雪球
            iterator.remove();
        } else {
            snow.draw(canvas, paint, snowIcon);
        }
    }

    private void createSnow() {

        if (stop) return;

        Date now = new Date();
        if (now.getTime() - mLastCreateDate.getTime() > snows.size() - now.getMinutes() && snows.size() < 1000) {
            int radius = (int) (Math.random() * 10) + 5;
            Snow snow = new Snow(radius);
            snow.x = (int) (Math.random() * width) + 1;
            snows.add(snow);
            mLastCreateDate = now;
        }
    }

    private void drawSnow(Canvas canvas) {
        createSnow();
        Iterator<Snow> iterator = this.snows.iterator();
        while (iterator.hasNext()) {
            Snow snow = iterator.next();
            moveSnow(canvas, snow, iterator);
        }

        if (snows.isEmpty()) {
            if (snowHandler != null) snowHandler.removeCallbacksAndMessages(null);
        }

        //一秒钟60帧
        snowHandler.sendEmptyMessageDelayed(1, 1 * 1000 / 60);
    }


    private class SnowHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    invalidate();
                    break;
            }
        }
    }


    //===================================烟花========================================

    public class Firework {
        private final PVector gravity = new PVector(0f, 0.2f);
        ArrayList<Particle> particles;    // An arraylist for all the particles

        private String[] colors = {
                "61-168-223",//淡蓝色
                "169-0-95",//洋红
                "225-192-16",//黄色
                "159-0-154",//紫色
                "0-127-255",//天蓝色
                "158-37-28",//砖红色
                "255-24-139",//桃红色
                "253-195-81",//金色
                "123-183-69",//绿色
                "190-245-60"//草绿色
        };

        int minHeight;
        float x;
        float y;
        int red;
        int green;
        int blue;

        public boolean isGoTo = false;
        public boolean isExplode = false;
        private int color;
        private int radius;

        Firework(Context context, float x, float y) {
            String rgb = colors[(int)(Math.random() * colors.length)];
            String strs [] = rgb.split("-");
            this.red = Integer.parseInt(strs[0]);
            this.green = Integer.parseInt(strs[1]);
            this.blue = Integer.parseInt(strs[2]);
            this.color = Color.argb(255, red, green, blue);
            this.radius = ScreenUtils.dip2px( 5);
            this.particles = new ArrayList<>();   // Initialize the arraylist

            this.x = x;
            this.y = y;

            this.minHeight = (int)(Math.random() *  (ScreenUtils.getScreenHeight() * 3 / 5f )) + ScreenUtils.dip2px( 40);
        }

        boolean done() {
            return (isExplode && particles.isEmpty());
        }

        boolean firstExplode = false;


        boolean explode() {
            return this.isGoTo;
        }

        private void update(Canvas canvas, Paint paint){
            this.y -= 15;
            if(this.y <= minHeight) {
                this.isGoTo = true;
                return;
            }
            paint.setColor(color);
            paint.setStrokeWidth(radius);
            canvas.drawCircle(x, y, radius, paint);
        }

        void run(Canvas canvas, Paint paint) {
            if (!isExplode) {//炸裂前
                update(canvas, paint);
                if (explode()) {
                    //到达位置，准备爆炸
                    int fragments = (int) PContext.random(80, 100);//碎片个数
                    for (int i = 0; i < fragments; i++) {
                        PVector location = new PVector(x, y, 0);
                        particles.add(new Particle(location, red, green, blue, radius));    // Add "num" amount of particles to the arraylist
                    }
                    isExplode = true;
                    firstExplode = true;
                }
            } else {//炸裂后
                if (firstExplode) {//屏幕一闪
                    firstExplode = false;
//                canvas.drawColor(Color.HSVToColor(new float[]{hu, 0.6f, 0.6f}));
                }

                for (int i = particles.size() - 1; i >= 0; i--) {
                    Particle p = particles.get(i);
                    p.applyForce(gravity);
                    p.update();
                    p.display(canvas, paint);
                    if (p.isDead()) {
                        particles.remove(i);
                    }
                }
            }

        }

    }

    //粒子
    class Particle {

        PVector location;
        PVector velocity;
        PVector acceleration;
        float life;
        boolean seed = false;
        int red;
        int green;
        int blue;

        float radius;

        Particle(PVector loc, int red, int green, int blue, int radius) {
            this.red = red;
            this.green = green;
            this.blue = blue;

            acceleration = new PVector(0, 0);
            PVector vector = new PVector();
            velocity = vector.random2D();
            velocity.mult(PContext.random(4, 8));
            location = loc.get();
            life = 255.0f;
            this.radius = radius;

        }

        void applyForce(PVector force) {
            acceleration.add(force);
        }


        boolean explode() {
            if (seed && velocity.y > 0) {
                life = 0;
                return true;
            }
            return false;
        }

        // Method to update location
        void update() {
            velocity.add(acceleration);
            location.add(velocity);
//        LogUtils.i("sunshine-app", "location.y->" + location.y);
//        LogUtils.i("sunshine-app", "location.y->" + location.y);
            if (!seed) {//未爆炸前生命不会减少
                //爆炸开之后
                life -= 2.5;
                velocity.mult(0.95f);
            }
            acceleration.mult(0);
        }

        // Method to display
        void display(Canvas canvas, Paint paint) {
            paint.setColor(RGBTOColor(red, green, blue, (int) life));
            float r;
            if (seed) {
                r = radius;
                paint.setStrokeWidth(radius);
            } else {
                r = radius *.3f;
            }
            canvas.drawCircle(location.x, location.y, r, paint);
        }

        // Is the particle still useful?
        boolean isDead() {
            return life <= 0;
        }

        private int RGBTOColor(int red, int green, int blue, int alpha) {
            return Color.argb(alpha, red, green, blue);
        }
    }


    public static class PContext {
        private static final float PI = (float) Math.PI;
        private static final float TWO_PI     = PI * 2.0f;

        /**
         * Return a random number in the range [0, howbig).
         * <P>
         * The number returned will range from zero up to
         * (but not including) 'howbig'.
         */
        public static float random(float howbig) {
            // for some reason (rounding error?) Math.random() * 3
            // can sometimes return '3' (once in ~30 million tries)
            // so a check was added to avoid the inclusion of 'howbig'

            // avoid an infinite loop
            if (howbig == 0) return 0;

            float value = 0;
            Random random = new Random();
            do {
                //value = (float)Math.random() * howbig;
                value = random.nextFloat() * howbig;
            } while (value == howbig);
            return value;
        }


        /**
         * Return a random number in the range [howsmall, howbig).
         * <P>
         * The number returned will range from 'howsmall' up to
         * (but not including 'howbig'.
         * <P>
         * If howsmall is >= howbig, howsmall will be returned,
         * meaning that random(5, 5) will return 5 (useful)
         * and random(7, 4) will return 7 (not useful.. better idea?)
         */
        public static float random(float howsmall, float howbig) {
            if (howsmall >= howbig) return howsmall;
            float diff = howbig - howsmall;
            return random(diff) + howsmall;
        }


        public static float abs(float n) {
            return (n < 0) ? -n : n;
        }

        public static int abs(int n) {
            return (n < 0) ? -n : n;
        }

        public static float log(float a) {
            return (float)Math.log(a);
        }

        public static int max(int a, int b) {
            return (a > b) ? a : b;
        }

        public static float max(float a, float b) {
            return (a > b) ? a : b;
        }

        public static int max(int a, int b, int c) {
            return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
        }

        public static float max(float a, float b, float c) {
            return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
        }


        /**
         * Find the maximum value in an array.
         * Throws an ArrayIndexOutOfBoundsException if the array is length 0.
         * @param list the source array
         * @return The maximum value
         */
        public static int max(int[] list) {
            if (list.length == 0) {
                throw new ArrayIndexOutOfBoundsException();
            }
            int max = list[0];
            for (int i = 1; i < list.length; i++) {
                if (list[i] > max) max = list[i];
            }
            return max;
        }

        /**
         * Find the maximum value in an array.
         * Throws an ArrayIndexOutOfBoundsException if the array is length 0.
         * @param list the source array
         * @return The maximum value
         */
        public static float max(float[] list) {
            if (list.length == 0) {
                throw new ArrayIndexOutOfBoundsException();
            }
            float max = list[0];
            for (int i = 1; i < list.length; i++) {
                if (list[i] > max) max = list[i];
            }
            return max;
        }


        public static int min(int a, int b) {
            return (a < b) ? a : b;
        }

        public static float min(float a, float b) {
            return (a < b) ? a : b;
        }


        public static int min(int a, int b, int c) {
            return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
        }

        public static float min(float a, float b, float c) {
            return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
        }


        /**
         * Find the minimum value in an array.
         * Throws an ArrayIndexOutOfBoundsException if the array is length 0.
         * @param list the source array
         * @return The minimum value
         */
        public static int min(int[] list) {
            if (list.length == 0) {
                throw new ArrayIndexOutOfBoundsException();
            }
            int min = list[0];
            for (int i = 1; i < list.length; i++) {
                if (list[i] < min) min = list[i];
            }
            return min;
        }
        /**
         * Find the minimum value in an array.
         * Throws an ArrayIndexOutOfBoundsException if the array is length 0.
         * @param list the source array
         * @return The minimum value
         */
        public static float min(float[] list) {
            if (list.length == 0) {
                throw new ArrayIndexOutOfBoundsException();
            }
            float min = list[0];
            for (int i = 1; i < list.length; i++) {
                if (list[i] < min) min = list[i];
            }
            return min;
        }


        public static float sin(float angle) {
            return (float)Math.sin(angle);
        }

        public static float cos(float angle) {
            return (float)Math.cos(angle);
        }

        public static int round(float what) {
            return (int) Math.round(what);
        }



        public static final float lerp(float start, float stop, float amt) {
            return start + (stop-start) * amt;
        }

        /**
         * Convenience function to map a variable from one coordinate space
         * to another. Equivalent to unlerp() followed by lerp().
         */
        public static float map(float value,
                                      float istart, float istop,
                                      float ostart, float ostop) {
            return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
        }
    }


    public class PVector implements Serializable {

        /**
         * Generated 2010-09-14 by jdf
         */
        private static final long serialVersionUID = -6717872085945400694L;

        /**
         * ( begin auto-generated from PVector_x.xml )
         *
         * The x component of the vector. This field (variable) can be used to both
         * get and set the value (see above example.)
         *
         * ( end auto-generated )
         *
         * @webref pvector:field
         * @usage web_application
         * @brief The x component of the vector
         */
        public float x;

        /**
         * ( begin auto-generated from PVector_y.xml )
         *
         * The y component of the vector. This field (variable) can be used to both
         * get and set the value (see above example.)
         *
         * ( end auto-generated )
         *
         * @webref pvector:field
         * @usage web_application
         * @brief The y component of the vector
         */
        public float y;

        /**
         * ( begin auto-generated from PVector_z.xml )
         *
         * The z component of the vector. This field (variable) can be used to both
         * get and set the value (see above example.)
         *
         * ( end auto-generated )
         *
         * @webref pvector:field
         * @usage web_application
         * @brief The z component of the vector
         */
        public float z;

        /** Array so that this can be temporarily used in an array context */
        transient protected float[] array;

        /**
         * Constructor for an empty vector: x, y, and z are set to 0.
         */
        public PVector() {
        }


        /**
         * Constructor for a 3D vector.
         *
         * @param  x the x coordinate.
         * @param  y the y coordinate.
         * @param  z the z coordinate.
         */
        public PVector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }


        /**
         * Constructor for a 2D vector: z coordinate is set to 0.
         */
        public PVector(float x, float y) {
            this.x = x;
            this.y = y;
            this.z = 0;
        }

        /**
         * ( begin auto-generated from PVector_set.xml )
         *
         * Sets the x, y, and z component of the vector using two or three separate
         * variables, the data from a PVector, or the values from a float array.
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @param x the x component of the vector
         * @param y the y component of the vector
         * @param z the z component of the vector
         * @brief Set the x, y, and z component of the vector
         */
        public void set(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         *
         * @webref pvector:method
         * @param x the x component of the vector
         * @param y the y component of the vector
         * @brief Set the x, y components of the vector
         */
        public void set(float x, float y) {
            this.x = x;
            this.y = y;
        }

        /**
         * @param v any variable of type PVector
         */
        public void set(PVector v) {
            x = v.x;
            y = v.y;
            z = v.z;
        }


        /**
         * Set the x, y (and maybe z) coordinates using a float[] array as the source.
         * @param source array to copy from
         */
        public void set(float[] source) {
            if (source.length >= 2) {
                x = source[0];
                y = source[1];
            }
            if (source.length >= 3) {
                z = source[2];
            }
        }


        /**
         * ( begin auto-generated from PVector_random2D.xml )
         *
         * Make a new 2D unit vector with a random direction.  If you pass in "this"
         * as an argument, it will use the PApplet's random number generator.  You can
         * also pass in a target PVector to fill.
         *
         * @webref pvector:method
         * @usage web_application
         * @return the random PVector
         * @brief Make a new 2D unit vector with a random direction.
         * @see PVector#random3D()
         */
        public PVector random2D() {
            return random2D(null,null);
        }

        /**
         * Make a new 2D unit vector with a random direction
         * using Processing's current random number generator
         * @param parent current PApplet instance
         * @return the random PVector
         */
        public PVector random2D(PContext parent) {
            return random2D(null,parent);
        }

        /**
         * Set a 2D vector to a random unit vector with a random direction
         * @param target the target vector (if null, a new vector will be created)
         * @return the random PVector
         */
        public PVector random2D(PVector target) {
            return random2D(target,null);
        }

        /**
         * Make a new 2D unit vector with a random direction
         * @return the random PVector
         */
        public PVector random2D(PVector target, PContext parent) {
            if (parent == null) return fromAngle((float)(Math.random()*Math.PI*2),target);
            else                return fromAngle(parent.random(PContext.TWO_PI),target);
        }

        /**
         * ( begin auto-generated from PVector_random3D.xml )
         *
         * Make a new 3D unit vector with a random direction.  If you pass in "this"
         * as an argument, it will use the PApplet's random number generator.  You can
         * also pass in a target PVector to fill.
         *
         * @webref pvector:method
         * @usage web_application
         * @return the random PVector
         * @brief Make a new 3D unit vector with a random direction.
         * @see PVector#random2D()
         */
        public PVector random3D() {
            return random3D(null,null);
        }

        /**
         * Make a new 3D unit vector with a random direction
         * using Processing's current random number generator
         * @param parent current PApplet instance
         * @return the random PVector
         */
        public PVector random3D(PContext parent) {
            return random3D(null,parent);
        }

        /**
         * Set a 3D vector to a random unit vector with a random direction
         * @param target the target vector (if null, a new vector will be created)
         * @return the random PVector
         */
        public PVector random3D(PVector target) {
            return random3D(target,null);
        }

        /**
         * Make a new 3D unit vector with a random direction
         * @return the random PVector
         */
        public PVector random3D(PVector target, PContext parent) {
            float angle;
            float vz;
            if (parent == null) {
                angle = (float) (Math.random()*Math.PI*2);
                vz    = (float) (Math.random()*2-1);
            } else {
                angle = parent.random(PContext.TWO_PI);
                vz    = parent.random(-1,1);
            }
            float vx = (float) (Math.sqrt(1-vz*vz)*Math.cos(angle));
            float vy = (float) (Math.sqrt(1-vz*vz)*Math.sin(angle));
            if (target == null) {
                target = new PVector(vx, vy, vz);
                //target.normalize(); // Should be unnecessary
            } else {
                target.set(vx,vy,vz);
            }
            return target;
        }

        /**
         * ( begin auto-generated from PVector_sub.xml )
         *
         * Make a new 2D unit vector from an angle.
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @brief Make a new 2D unit vector from an angle
         * @param angle the angle
         * @return the new unit PVector
         */
        public PVector fromAngle(float angle) {
            return fromAngle(angle,null);
        }


        /**
         * Make a new 2D unit vector from an angle
         *
         * @param target the target vector (if null, a new vector will be created)
         * @return the PVector
         */
        public PVector fromAngle(float angle, PVector target) {
            if (target == null) {
                target = new PVector((float)Math.cos(angle),(float)Math.sin(angle),0);
            } else {
                target.set((float)Math.cos(angle),(float)Math.sin(angle),0);
            }
            return target;
        }

        /**
         * ( begin auto-generated from PVector_get.xml )
         *
         * Gets a copy of the vector, returns a PVector object.
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @brief Get a copy of the vector
         */
        public PVector get() {
            return new PVector(x, y, z);
        }

        /**
         * @param target
         */
        public float[] get(float[] target) {
            if (target == null) {
                return new float[] { x, y, z };
            }
            if (target.length >= 2) {
                target[0] = x;
                target[1] = y;
            }
            if (target.length >= 3) {
                target[2] = z;
            }
            return target;
        }


        /**
         * ( begin auto-generated from PVector_mag.xml )
         *
         * Calculates the magnitude (length) of the vector and returns the result
         * as a float (this is simply the equation <em>sqrt(x*x + y*y + z*z)</em>.)
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @brief Calculate the magnitude of the vector
         * @return magnitude (length) of the vector
         * @see PVector#magSq()
         */
        public float mag() {
            return (float) Math.sqrt(x*x + y*y + z*z);
        }

        /**
         * ( begin auto-generated from PVector_mag.xml )
         *
         * Calculates the squared magnitude of the vector and returns the result
         * as a float (this is simply the equation <em>(x*x + y*y + z*z)</em>.)
         * Faster if the real length is not required in the
         * case of comparing vectors, etc.
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @brief Calculate the magnitude of the vector, squared
         * @return squared magnitude of the vector
         * @see PVector#mag()
         */
        public float magSq() {
            return (x*x + y*y + z*z);
        }

        /**
         * ( begin auto-generated from PVector_add.xml )
         *
         * Adds x, y, and z components to a vector, adds one vector to another, or
         * adds two independent vectors together. The version of the method that
         * adds two vectors together is a static method and returns a PVector, the
         * others have no return value -- they act directly on the vector. See the
         * examples for more context.
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @param v the vector to be added
         * @brief Adds x, y, and z components to a vector, one vector to another, or two independent vectors
         */
        public void add(PVector v) {
            x += v.x;
            y += v.y;
            z += v.z;
        }

        /**
         * @param x x component of the vector
         * @param y y component of the vector
         * @param z z component of the vector
         */
        public void add(float x, float y, float z) {
            this.x += x;
            this.y += y;
            this.z += z;
        }


        /**
         * Add two vectors
         * @param v1 a vector
         * @param v2 another vector
         */
        public PVector add(PVector v1, PVector v2) {
            return add(v1, v2, null);
        }


        /**
         * Add two vectors into a target vector
         * @param target the target vector (if null, a new vector will be created)
         */
        public PVector add(PVector v1, PVector v2, PVector target) {
            if (target == null) {
                target = new PVector(v1.x + v2.x,v1.y + v2.y, v1.z + v2.z);
            } else {
                target.set(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
            }
            return target;
        }


        /**
         * ( begin auto-generated from PVector_sub.xml )
         *
         * Subtracts x, y, and z components from a vector, subtracts one vector
         * from another, or subtracts two independent vectors. The version of the
         * method that subtracts two vectors is a static method and returns a
         * PVector, the others have no return value -- they act directly on the
         * vector. See the examples for more context.
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @param v any variable of type PVector
         * @brief Subtract x, y, and z components from a vector, one vector from another, or two independent vectors
         */
        public void sub(PVector v) {
            x -= v.x;
            y -= v.y;
            z -= v.z;
        }

        /**
         * @param x the x component of the vector
         * @param y the y component of the vector
         * @param z the z component of the vector
         */
        public void sub(float x, float y, float z) {
            this.x -= x;
            this.y -= y;
            this.z -= z;
        }


        /**
         * Subtract one vector from another
         * @param v1 the x, y, and z components of a PVector object
         * @param v2 the x, y, and z components of a PVector object
         */
        public PVector sub(PVector v1, PVector v2) {
            return sub(v1, v2, null);
        }

        /**
         * Subtract one vector from another and store in another vector
         * @param v1 the x, y, and z components of a PVector object
         * @param v2 the x, y, and z components of a PVector object
         * @param target PVector in which to store the result
         */
        public PVector sub(PVector v1, PVector v2, PVector target) {
            if (target == null) {
                target = new PVector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
            } else {
                target.set(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
            }
            return target;
        }


        /**
         * ( begin auto-generated from PVector_mult.xml )
         *
         * Multiplies a vector by a scalar or multiplies one vector by another.
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @brief Multiply a vector by a scalar
         * @param n the number to multiply with the vector
         */
        public void mult(float n) {
            x *= n;
            y *= n;
            z *= n;
        }


        /**
         * @param v the vector to multiply by the scalar
         */
        public PVector mult(PVector v, float n) {
            return mult(v, n, null);
        }


        /**
         * Multiply a vector by a scalar, and write the result into a target PVector.
         * @param target PVector in which to store the result
         */
        public PVector mult(PVector v, float n, PVector target) {
            if (target == null) {
                target = new PVector(v.x*n, v.y*n, v.z*n);
            } else {
                target.set(v.x*n, v.y*n, v.z*n);
            }
            return target;
        }



        /**
         * ( begin auto-generated from PVector_div.xml )
         *
         * Divides a vector by a scalar or divides one vector by another.
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @brief Divide a vector by a scalar
         * @param n the number by which to divide the vector
         */
        public void div(float n) {
            x /= n;
            y /= n;
            z /= n;
        }


        /**
         * Divide a vector by a scalar and return the result in a new vector.
         * @param v the vector to divide by the scalar
         * @return a new vector that is v1 / n
         */
        public PVector div(PVector v, float n) {
            return div(v, n, null);
        }

        /**
         * Divide a vector by a scalar and store the result in another vector.
         * @param target PVector in which to store the result
         */
        public PVector div(PVector v, float n, PVector target) {
            if (target == null) {
                target = new PVector(v.x/n, v.y/n, v.z/n);
            } else {
                target.set(v.x/n, v.y/n, v.z/n);
            }
            return target;
        }


        /**
         * ( begin auto-generated from PVector_dist.xml )
         *
         * Calculates the Euclidean distance between two points (considering a
         * point as a vector object).
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @param v the x, y, and z coordinates of a PVector
         * @brief Calculate the distance between two points
         */
        public float dist(PVector v) {
            float dx = x - v.x;
            float dy = y - v.y;
            float dz = z - v.z;
            return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        }


        /**
         * @param v1 any variable of type PVector
         * @param v2 any variable of type PVector
         * @return the Euclidean distance between v1 and v2
         */
        public float dist(PVector v1, PVector v2) {
            float dx = v1.x - v2.x;
            float dy = v1.y - v2.y;
            float dz = v1.z - v2.z;
            return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        }


        /**
         * ( begin auto-generated from PVector_dot.xml )
         *
         * Calculates the dot product of two vectors.
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @param v any variable of type PVector
         * @return the dot product
         * @brief Calculate the dot product of two vectors
         */
        public float dot(PVector v) {
            return x*v.x + y*v.y + z*v.z;
        }

        /**
         * @param x x component of the vector
         * @param y y component of the vector
         * @param z z component of the vector
         */
        public float dot(float x, float y, float z) {
            return this.x*x + this.y*y + this.z*z;
        }

        /**
         * @param v1 any variable of type PVector
         * @param v2 any variable of type PVector
         */
        public float dot(PVector v1, PVector v2) {
            return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
        }


        /**
         * ( begin auto-generated from PVector_cross.xml )
         *
         * Calculates and returns a vector composed of the cross product between
         * two vectors.
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @param v the vector to calculate the cross product
         * @brief Calculate and return the cross product
         */
        public PVector cross(PVector v) {
            return cross(v, null);
        }


        /**
         * @param v any variable of type PVector
         * @param target PVector to store the result
         */
        public PVector cross(PVector v, PVector target) {
            float crossX = y * v.z - v.y * z;
            float crossY = z * v.x - v.z * x;
            float crossZ = x * v.y - v.x * y;

            if (target == null) {
                target = new PVector(crossX, crossY, crossZ);
            } else {
                target.set(crossX, crossY, crossZ);
            }
            return target;
        }

        /**
         * @param v1 any variable of type PVector
         * @param v2 any variable of type PVector
         * @param target PVector to store the result
         */
        public PVector cross(PVector v1, PVector v2, PVector target) {
            float crossX = v1.y * v2.z - v2.y * v1.z;
            float crossY = v1.z * v2.x - v2.z * v1.x;
            float crossZ = v1.x * v2.y - v2.x * v1.y;

            if (target == null) {
                target = new PVector(crossX, crossY, crossZ);
            } else {
                target.set(crossX, crossY, crossZ);
            }
            return target;
        }


        /**
         * ( begin auto-generated from PVector_normalize.xml )
         *
         * Normalize the vector to length 1 (make it a unit vector).
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @brief Normalize the vector to a length of 1
         */
        public void normalize() {
            float m = mag();
            if (m != 0 && m != 1) {
                div(m);
            }
        }


        /**
         * @param target Set to null to create a new vector
         * @return a new vector (if target was null), or target
         */
        public PVector normalize(PVector target) {
            if (target == null) {
                target = new PVector();
            }
            float m = mag();
            if (m > 0) {
                target.set(x/m, y/m, z/m);
            } else {
                target.set(x, y, z);
            }
            return target;
        }


        /**
         * ( begin auto-generated from PVector_limit.xml )
         *
         * Limit the magnitude of this vector to the value used for the <b>max</b> parameter.
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @param max the maximum magnitude for the vector
         * @brief Limit the magnitude of the vector
         */
        public void limit(float max) {
            if (magSq() > max*max) {
                normalize();
                mult(max);
            }
        }

        /**
         * ( begin auto-generated from PVector_setMag.xml )
         *
         * Set the magnitude of this vector to the value used for the <b>len</b> parameter.
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @param len the new length for this vector
         * @brief Set the magnitude of the vector
         */
        public void setMag(float len) {
            normalize();
            mult(len);
        }

        /**
         * Sets the magnitude of this vector, storing the result in another vector.
         * @param target Set to null to create a new vector
         * @param len the new length for the new vector
         * @return a new vector (if target was null), or target
         */
        public PVector setMag(PVector target, float len) {
            target = normalize(target);
            target.mult(len);
            return target;
        }

        /**
         * ( begin auto-generated from PVector_setMag.xml )
         *
         * Calculate the angle of rotation for this vector (only 2D vectors)
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @return the angle of rotation
         * @brief Calculate the angle of rotation for this vector
         */
        public float heading() {
            float angle = (float) Math.atan2(-y, x);
            return -1*angle;
        }


        @Deprecated
        public float heading2D() {
            return heading();
        }


        /**
         * ( begin auto-generated from PVector_rotate.xml )
         *
         * Rotate the vector by an angle (only 2D vectors), magnitude remains the same
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @brief Rotate the vector by an angle (2D only)
         * @param theta the angle of rotation
         */
        public void rotate(float theta) {
            float xTemp = x;
            // Might need to check for rounding errors like with angleBetween function?
            x = x*PContext.cos(theta) - y*PContext.sin(theta);
            y = xTemp*PContext.sin(theta) + y*PContext.cos(theta);
        }


        /**
         * ( begin auto-generated from PVector_rotate.xml )
         *
         * Linear interpolate the vector to another vector
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @brief Linear interpolate the vector to another vector
         * @param v the vector to lerp to
         * @param amt  The amount of interpolation; some value between 0.0 (old vector) and 1.0 (new vector). 0.1 is very near the new vector. 0.5 is halfway in between.
         */
        public void lerp(PVector v, float amt) {
            x = PContext.lerp(x,v.x,amt);
            y = PContext.lerp(y,v.y,amt);
            z = PContext.lerp(z,v.z,amt);
        }

        /**
         * Linear interpolate between two vectors (returns a new PVector object)
         * @param v1 the vector to start from
         * @param v2 the vector to lerp to
         */
        public PVector lerp(PVector v1, PVector v2, float amt) {
            PVector v = v1.get();
            v.lerp(v2, amt);
            return v;
        }

        /**
         * Linear interpolate the vector to x,y,z values
         * @param x the x component to lerp to
         * @param y the y component to lerp to
         * @param z the z component to lerp to
         */
        public void lerp(float x, float y, float z, float amt) {
            this.x = PContext.lerp(this.x,x,amt);
            this.y = PContext.lerp(this.y,y,amt);
            this.z = PContext.lerp(this.z,z,amt);
        }

        /**
         * ( begin auto-generated from PVector_angleBetween.xml )
         *
         * Calculates and returns the angle (in radians) between two vectors.
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage web_application
         * @param v1 the x, y, and z components of a PVector
         * @param v2 the x, y, and z components of a PVector
         * @brief Calculate and return the angle between two vectors
         */
        public float angleBetween(PVector v1, PVector v2) {

            // We get NaN if we pass in a zero vector which can cause problems
            // Zero seems like a reasonable angle between a (0,0) vector and something else
            if (v1.x == 0 && v1.y == 0) return 0.0f;
            if (v2.x == 0 && v2.y == 0) return 0.0f;

            double dot = v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
            double v1mag = Math.sqrt(v1.x * v1.x + v1.y * v1.y + v1.z * v1.z);
            double v2mag = Math.sqrt(v2.x * v2.x + v2.y * v2.y + v2.z * v2.z);
            // This should be a number between -1 and 1, since it's "normalized"
            double amt = dot / (v1mag * v2mag);
            // But if it's not due to rounding error, then we need to fix it
            // http://code.google.com/p/processing/issues/detail?id=340
            // Otherwise if outside the range, acos() will return NaN
            // http://www.cppreference.com/wiki/c/math/acos
            if (amt <= -1) {
                return PContext.PI;
            } else if (amt >= 1) {
                // http://code.google.com/p/processing/issues/detail?id=435
                return 0;
            }
            return (float) Math.acos(amt);
        }


        @Override
        public String toString() {
            return "[ " + x + ", " + y + ", " + z + " ]";
        }


        /**
         * ( begin auto-generated from PVector_array.xml )
         *
         * Return a representation of this vector as a float array. This is only
         * for temporary use. If used in any other fashion, the contents should be
         * copied by using the <b>PVector.get()</b> method to copy into your own array.
         *
         * ( end auto-generated )
         *
         * @webref pvector:method
         * @usage: web_application
         * @brief Return a representation of the vector as a float array
         */
        public float[] array() {
            if (array == null) {
                array = new float[3];
            }
            array[0] = x;
            array[1] = y;
            array[2] = z;
            return array;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PVector))
                return false;
            final PVector p = (PVector) obj;
            return x == p.x && y == p.y && z == p.z;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 31 * result + Float.floatToIntBits(x);
            result = 31 * result + Float.floatToIntBits(y);
            result = 31 * result + Float.floatToIntBits(z);
            return result;
        }
    }



    private void addFirework(int num) {
        if (stop) return;

        if (fireworks.size() < MAX_FIREWORK_NUM) {
            int size = num - fireworks.size();
            for (int i = 0; i < size; i++) {
                int x = ScreenUtils.dip2px(20) + (int) (Math.random() * (ScreenUtils.getScreenWidth() - ScreenUtils.dip2px(40)));
                int y = ScreenUtils.getScreenHeight();
                fireworks.add(new Firework(context, x, y));
            }
        }

    }

    //烟花特效
    private void drawFirework(Canvas canvas) {
        int count = 0;
        for (int i = fireworks.size() - 1; i >= 0; i--) {
            Firework f = fireworks.get(i);
            f.run(canvas, paint);
            if (f.done()) {
                fireworks.remove(i);
                count++;
            }
        }

        if (!fireworks.isEmpty()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }

        if (count > 0) {
            Message msg = new Message();
            msg.what = 1;
            msg.arg1 = count;
            fireworkHandler.sendMessageDelayed(msg, 300);
        } else {
            Message msg = new Message();
            msg.what = 1;
            msg.arg1 = 5;
            fireworkHandler.sendMessageDelayed(msg, 300);
        }
    }

    private class FireworkHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    int count = msg.arg1;
                    addFirework(count);
                    break;
            }
        }
    }


    //====================================花瓣==================================


    /**
     * 花瓣
     */
    public class Flower {

        float x;
        float y;
        int maxHeight;
        int maxWidth;
        public boolean isOut = false;//出界
        private Bitmap bitmap;
        private int direction = 0;//0:向左， 1:向右
        private float rotation = 0;

        Flower(Context context, Bitmap origin, float x, float y) {
            this.x = x;
            this.y = y;

            int dstWidth = ScreenUtils.dip2px( 80f);
            int dstHeight = (int)((origin.getHeight() * 1.0f / origin.getWidth()) * dstWidth);

            float ratioX = dstWidth * 1.0f / origin.getWidth();
            float ratioY = dstHeight * 1.0f / origin.getHeight();
            float randomRatio = PContext.random(0.5f, 1.8f);
            float sx = ratioX * randomRatio;
            float sy = ratioY * randomRatio;

            this.rotation = PContext.random(0, 360);

            Matrix matrix = new Matrix();
            matrix.setScale(sx, sy);
            this.bitmap = Bitmap.createBitmap(origin, 0, 0, origin.getWidth(), origin.getHeight(), matrix, false);
            this.direction = Math.random() < 0.5f ? 0 : 1;
            this.maxHeight = ScreenUtils.getScreenHeight();
            this.maxWidth = ScreenUtils.getScreenWidth();
        }

        boolean done() {
            return isOut;
        }


        private void update(Canvas canvas, Paint paint){
            this.y += 3;
            if(direction == 0) {
                //向左
                this.x += 1;
            }else {
                //向右
                this.x += (-1);
            }


            if(this.y >= maxHeight || this.x <= -bitmap.getWidth() || this.x >= maxWidth) {
                this.isOut = true;
                return;
            }

            rotation += 0.5f;
            Matrix matrix = new Matrix();
            float centerX = bitmap.getWidth() / 2;
            float centerY = bitmap.getHeight() / 2;
            matrix.postRotate(rotation, centerX, centerY);
            matrix.postTranslate(this.x, this.y);

            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawBitmap(bitmap, matrix, paint);
        }

        void run(Canvas canvas, Paint paint) {
            update(canvas, paint);
        }

    }


    //花瓣特效
    //花瓣特效
    private void addFlower(int num, boolean randomY) {
        if (stop) return;

        for (int i = 0; i < num; i++) {
            int x = ScreenUtils.dip2px(20) + (int) (Math.random() * (ScreenUtils.getScreenWidth() - ScreenUtils.dip2px( 40)));
            int y = 0;
            if (randomY) {
                y = (int) (Math.random() * ScreenUtils.getScreenHeight() / 2);
            }
            flowers.add(new Flower(context, bitmap, x, y));
        }

    }

    private void addFlower(int num) {
        addFlower(num, false);

    }

    protected void drawFlower(Canvas canvas) {
        int count = 0;
        for (int i = flowers.size() - 1; i >= 0; i--) {
            Flower f = flowers.get(i);
            f.run(canvas, paint);
            if (f.done()) {
                flowers.remove(i);
                count++;
            }
        }
        if (!flowers.isEmpty()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }

        if (count > 0) {
            addFlower(count);
        }
    }


}
