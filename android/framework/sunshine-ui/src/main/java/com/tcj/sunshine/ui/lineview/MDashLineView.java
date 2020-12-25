package com.tcj.sunshine.ui.lineview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.ui.R;


/**
 * 虚线控件
 */
@SuppressLint("NewApi")
public class MDashLineView  extends View {
	
	private Context context;
	private Paint paint;
	private Path path;
	private int mLineColor;
	private float mLineWidth = 1;
	private float mDashGap;
	private int orientation = 0;
	
	private PathEffect mPathEffect; 
	
	private int width = 0;
	private int height = 0;	
          
    public MDashLineView(Context context) {
		super(context);
		this.init(context, null);
	}
    public MDashLineView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	this.init(context, attrs);
    }
    public MDashLineView(Context context, AttributeSet attrs, int defStyleAttr) {
    	super(context, attrs, defStyleAttr);
    	this.init(context, attrs);
    }
    public MDashLineView(Context context, AttributeSet attrs, int defStyleAttr,
    		int defStyleRes) {
    	super(context, attrs, defStyleAttr, defStyleRes);
    	this.init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs){
    	this.context = context;
    	this.paint = new Paint();
    	if(attrs != null){
    		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MDashLineViewStyle);
    		int mLineColorId = array.getResourceId(R.styleable.MDashLineViewStyle_dash_line_color, 0);
    		if(mLineColorId > 0){
    			this.mLineColor = array.getResources().getColor(mLineColorId);
    		}else{
    			this.mLineColor = array.getColor(R.styleable.MDashLineViewStyle_dash_line_color, 0xFFC0C0C0);
    		}
    		
    		int mDashGapId = array.getResourceId(R.styleable.MDashLineViewStyle_dash_line_gap, 0);
    		if(mDashGapId > 0){
    			this.mDashGap = array.getResources().getDimension(mDashGapId);
    		}else{
    			this.mDashGap = array.getDimension(R.styleable.MDashLineViewStyle_dash_line_gap, 0);
    		}
    		
    		int mLineWidthId = array.getResourceId(R.styleable.MDashLineViewStyle_dash_line_width, 0);
    		if(mLineWidthId > 0){
    			this.mLineWidth = array.getResources().getDimension(mLineWidthId);
    		}else{
    			this.mLineWidth = array.getDimension(R.styleable.MDashLineViewStyle_dash_line_width, 1);
    		}
    		
    		int mOrientationId = array.getResourceId(R.styleable.MDashLineViewStyle_dash_line_orientation, 0);
    		if(mOrientationId > 0){
    			this.orientation = array.getResources().getInteger(mOrientationId);
    		}else{
    			this.orientation = array.getInteger(R.styleable.MDashLineViewStyle_dash_line_orientation, 0);
    		}
    		
    		array.recycle();
    	}
    	
    	this.paint = new Paint();
    	this.paint.setStyle(Paint.Style.STROKE);
    	this.paint.setStrokeWidth(mLineWidth);
    	paint.setColor(mLineColor);
    	this.path = new Path();
    	this.mPathEffect = new DashPathEffect(new float[]{mDashGap,mDashGap,mDashGap,mDashGap},1);
    	this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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
			this.height = ScreenUtils.dip2px(15);
		}else{
			this.height = heightSpecSize;
		}
		
		this.setMeasuredDimension(width, height);
	}
    
	@Override      
    protected void onDraw(Canvas canvas) {      
        // TODO Auto-generated method stub      
        super.onDraw(canvas);
        if(paint != null && mPathEffect != null && path != null){
	        if(orientation == 0){
	        	//横向
	        	path.moveTo(0, 0);      
	            path.lineTo(width,0);
	        }else{
	        	path.moveTo(0, 0);      
	            path.lineTo(0, height);
	        }
	        paint.setPathEffect(mPathEffect);      
	        canvas.drawPath(path, paint);
        }
    }   
}  
