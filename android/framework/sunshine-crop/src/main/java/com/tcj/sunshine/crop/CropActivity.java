package com.tcj.sunshine.crop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tcj.sunshine.crop.callback.BitmapCropCallback;
import com.tcj.sunshine.crop.model.AspectRatio;
import com.tcj.sunshine.crop.view.CropImageView;
import com.tcj.sunshine.crop.view.CropView;
import com.tcj.sunshine.crop.view.GestureCropImageView;
import com.tcj.sunshine.crop.view.OverlayView;
import com.tcj.sunshine.crop.view.TransformImageView;
import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.ui.UIToolbar;
import com.tcj.sunshine.ui.dialog.UIDialog;

import java.util.ArrayList;

/**
 * 裁剪Activity
 */
public class CropActivity extends AppCompatActivity {

    private UIToolbar mToolBar;
    private Button mBtnFinish;
    private CropView mCropView;
    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;
    //裁剪
    private View mLayoutCrop;
    private ImageView mIvCrop;
    private TextView mTvCrop;

    //缩放
    private View mLayoutScale;
    private ImageView mIvScale;
    private TextView mTvScale;

    //旋转
    private View mLayoutRotate;
    private ImageView mIvRotate;
    private TextView mTvRotate;

    private LayoutInflater inflater;

    private Uri source;//需要裁剪的图片的uri
    private Uri target;//裁剪之后的保存图片uri
    private int outputMaxWidth;//裁剪输出最大宽
    private int outputMaxHeight;//裁剪输最大高
    private ArrayList<AspectRatio> aspectRatioList = new ArrayList<>();//支持的裁剪比例
    private int mode;//功能模式
    private String format;//输出格式

    private UIDialog mCropDialog;
    private UIDialog mScaleDialog;
    private UIDialog mRotateDialog;

    private OnViewClickListener mClickListener = new OnViewClickListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_crop);
        this.initUI();
    }

    private void initUI(){
        this.mToolBar = this.findViewById(R.id.mToolBar);
        this.mBtnFinish = this.findViewById(R.id.mBtnFinish);
        this.mCropView = this.findViewById(R.id.mCropView);

        this.mLayoutCrop = this.findViewById(R.id.mLayoutCrop);
        this.mIvCrop = this.findViewById(R.id.mIvCrop);
        this.mTvCrop = this.findViewById(R.id.mTvCrop);

        this.mLayoutScale = this.findViewById(R.id.mLayoutScale);
        this.mIvScale = this.findViewById(R.id.mIvScale);
        this.mTvScale = this.findViewById(R.id.mTvScale);

        this.mLayoutRotate = this.findViewById(R.id.mLayoutRotate);
        this.mIvRotate = this.findViewById(R.id.mIvRotate);
        this.mTvRotate = this.findViewById(R.id.mTvRotate);

        this.mGestureCropImageView = mCropView.getCropImageView();
        this.mOverlayView = mCropView.getOverlayView();

        this.inflater = LayoutInflater.from(this);

        try {

            Bundle bundle = this.getIntent().getExtras();
            this.source = bundle.getParcelable(Crop.CROP_SOURCE_URI);
            this.target = bundle.getParcelable(Crop.CROP_TARGET_URI);
            this.outputMaxWidth = bundle.getInt(Crop.CROP_OUTPUT_MAX_WIDTH);
            this.outputMaxHeight = bundle.getInt(Crop.CROP_OUTPUT_MAX_HEIGHT);
            this.aspectRatioList = bundle.getParcelableArrayList(Crop.CROP_ASPECT_RATIO_ARRAY);
            this.mode = bundle.getInt(Crop.CROP_MODE);
            this.format = bundle.getString(Crop.CROP_OUTPUT_FORMAT);

            this.mGestureCropImageView.setImageUri(source, target);
            this.mGestureCropImageView.setMaxResultImageSizeX(this.outputMaxWidth);
            this.mGestureCropImageView.setMaxResultImageSizeY(this.outputMaxHeight);


            if(this.mode == Crop.MODE_CROP) {
                //只可以裁剪
                this.mGestureCropImageView.setScaleEnabled(false);
                this.mGestureCropImageView.setRotateEnabled(false);
                this.mLayoutScale.setVisibility(View.GONE);
                this.mLayoutRotate.setVisibility(View.GONE);
            }else if(this.mode == Crop.MODE_CROP_SCALE) {
                //可以裁剪，缩放，不能旋转
                this.mGestureCropImageView.setScaleEnabled(true);
                this.mGestureCropImageView.setRotateEnabled(false);
                this.mLayoutScale.setVisibility(View.VISIBLE);
                this.mTvScale.setText("缩放开");
                this.mIvScale.setImageResource(R.drawable.icon_scale_white);
                this.mLayoutRotate.setVisibility(View.GONE);
            }else if(this.mode == Crop.MODE_CROP_ROTATE) {
                //可以裁剪，旋转，不能缩放
                this.mGestureCropImageView.setScaleEnabled(false);
                this.mGestureCropImageView.setRotateEnabled(true);
                this.mLayoutScale.setVisibility(View.GONE);
                this.mLayoutRotate.setVisibility(View.VISIBLE);
                this.mTvRotate.setText("旋转开");
                this.mIvRotate.setImageResource(R.drawable.icon_rotate_white);
            }else {
                //都可以
                this.mGestureCropImageView.setScaleEnabled(true);
                this.mGestureCropImageView.setRotateEnabled(true);
                this.mLayoutScale.setVisibility(View.VISIBLE);
                this.mTvScale.setText("缩放开");
                this.mIvScale.setImageResource(R.drawable.icon_scale_white);
                this.mLayoutRotate.setVisibility(View.VISIBLE);

                this.mGestureCropImageView.setRotateEnabled(false);
                this.mTvRotate.setText("旋转关");
                this.mIvRotate.setImageResource(R.drawable.icon_forbid_rotate_white);
            }

            if(aspectRatioList == null || aspectRatioList.isEmpty()){
                //原始比例
                this.mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
                this.mTvCrop.setText("原始比例");
            }else {
                if(aspectRatioList != null && aspectRatioList.size() > 1) {
                    View mCropDialogView = this.inflater.inflate(R.layout.dialog_crop, null);
                    LinearLayout mCropListView = mCropDialogView.findViewById(R.id.mCropListView);
                    for (int i = 0; i < aspectRatioList.size(); i++) {
                        AspectRatio entity = aspectRatioList.get(i);
                        View itemView = this.inflater.inflate(R.layout.adapter_crop_item, null);
                        TextView mTvCropRatio = itemView.findViewById(R.id.mTvCropRatio);
                        View mLineView = itemView.findViewById(R.id.mLineView);
                        mLineView.setVisibility(i == aspectRatioList.size() -1 ? View.GONE : View.VISIBLE);
                        mCropListView.addView(itemView);

                        if(entity.getAspectRatioX() == Crop.SIZE_ORIGINAL && entity.getAspectRatioY() == Crop.SIZE_ORIGINAL) {
                            //原始比例
                            mTvCropRatio.setText("原始比例");
                        }else {
                            //固定比例
                            mTvCropRatio.setText((int)entity.getAspectRatioX() + ":" + (int)entity.getAspectRatioY());
                        }
                        mTvCropRatio.setTag(entity);
                        mTvCropRatio.setOnClickListener(mClickListener);
                    }

                    int height = aspectRatioList.size() * (ScreenUtils.dip2px(40f) + 1) -1;
                    int width = ScreenUtils.dip2px(100f);
                    this.mCropDialog = new UIDialog.Builder(this)
                            .setContentView(mCropDialogView)
                            .setWidth(width)
                            .setHeight(height)
                            .setStyleId(R.style.ThemeStyleUIDailog_Transparent)
                            .setGravity(Gravity.LEFT|Gravity.TOP)
                            .build();
                }

                AspectRatio firstRatio = aspectRatioList.get(0);

                if(firstRatio.getAspectRatioX() == Crop.SIZE_ORIGINAL && firstRatio.getAspectRatioY() == Crop.SIZE_ORIGINAL) {
                    //原始比例
                    if(mTvCrop != null) {
                        mTvCrop.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
                                mTvCrop.setText("原始比例");
                            }
                        }, 50);
                    }

                }else {
                    //固定比例
                    if(mTvCrop != null) {
                        mTvCrop.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mOverlayView.setTargetAspectRatio(firstRatio.getAspectRatioX() / firstRatio.getAspectRatioY());
                                mTvCrop.setText((int)firstRatio.getAspectRatioX() + ":" + (int)firstRatio.getAspectRatioY());
                            }
                        }, 50);
                    }

                }

            }

            if(this.mode == Crop.MODE_CROP_SCALE || this.mode == Crop.MODE_ALL) {
                int width = ScreenUtils.dip2px(100f);
                int height = ScreenUtils.dip2px(3 * 40f) + 2;
                this.mScaleDialog = new UIDialog.Builder(this)
                        .setContentViewId(R.layout.dialog_scale)
                        .setWidth(width)
                        .setHeight(height)
                        .setStyleId(R.style.ThemeStyleUIDailog_Transparent)
                        .setGravity(Gravity.LEFT|Gravity.TOP)
                        .setClickListener(new UIDialog.OnDialogClickListener() {
                            @Override
                            public void onClick(UIDialog dialog, View view) {
                                dialog.cancel();
                                if(view.getId() == R.id.mLayoutOpenScale) {
                                    mGestureCropImageView.setScaleEnabled(true);
                                    mTvScale.setText("缩放开");
                                    mIvScale.setImageResource(R.drawable.icon_scale_white);
                                }else if(view.getId() == R.id.mLayoutCloseScale) {
                                    mGestureCropImageView.setScaleEnabled(false);
                                    mTvScale.setText("缩放关");
                                    mIvScale.setImageResource(R.drawable.icon_forbid_scale_white);
                                }else if(view.getId() == R.id.mLayoutResetScale) {
                                    resetScale();
                                }
                            }
                        })
                        .setClickViewIds(R.id.mLayoutOpenScale, R.id.mLayoutCloseScale, R.id.mLayoutResetScale)
                        .build();
            }

            if(this.mode == Crop.MODE_CROP_ROTATE || this.mode == Crop.MODE_ALL) {
                int width = ScreenUtils.dip2px(100f);
                int height = ScreenUtils.dip2px(3 * 40f) + 2;
                this.mRotateDialog = new UIDialog.Builder(this)
                        .setContentViewId(R.layout.dialog_rotate)
                        .setWidth(width)
                        .setHeight(height)
                        .setStyleId(R.style.ThemeStyleUIDailog_Transparent)
                        .setGravity(Gravity.LEFT|Gravity.TOP)
                        .setClickListener(new UIDialog.OnDialogClickListener() {
                            @Override
                            public void onClick(UIDialog dialog, View view) {
                                dialog.cancel();
                                if(view.getId() == R.id.mLayoutOpenRotate) {
                                    mGestureCropImageView.setRotateEnabled(true);
                                    mTvRotate.setText("旋转开");
                                    mIvRotate.setImageResource(R.drawable.icon_rotate_white);
                                }else if(view.getId() == R.id.mLayoutCloseRotate) {
                                    mGestureCropImageView.setRotateEnabled(false);
                                    mTvRotate.setText("旋转关");
                                    mIvRotate.setImageResource(R.drawable.icon_forbid_rotate_white);
                                }else if(view.getId() == R.id.mLayoutResetRotate) {
                                    resetRotate();
                                }
                            }
                        })
                        .setClickViewIds(R.id.mLayoutOpenRotate, R.id.mLayoutCloseRotate, R.id.mLayoutResetRotate)
                        .build();
            }



        }catch (Exception e) {
            e.printStackTrace();
        }

        this.mToolBar.mBackView.setOnClickListener(mClickListener);
        this.mBtnFinish.setOnClickListener(mClickListener);
        this.mLayoutCrop.setOnClickListener(mClickListener);
        this.mLayoutScale.setOnClickListener(mClickListener);
        this.mLayoutRotate.setOnClickListener(mClickListener);
        this.mGestureCropImageView.setTransformImageListener(new OnTransformImageListener());

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.mGestureCropImageView != null) {
            this.mGestureCropImageView.cancelAllAnimations();
        }
    }

    /**
     * 重置旋转
     */
    private void resetRotate() {
        mGestureCropImageView.postRotate(-mGestureCropImageView.getCurrentAngle());
        mGestureCropImageView.setImageToWrapCropBounds();
    }


    /**
     * 设置旋转角度
     * @param angle
     */
    private void setRotateByAngle(int angle) {
        mGestureCropImageView.postRotate(angle);
        mGestureCropImageView.setImageToWrapCropBounds();
    }


    /**
     * 重置缩放
     */
    private void resetScale(){
        float deltaScale = mGestureCropImageView.getMinScale() / mGestureCropImageView.getCurrentScale();
        mGestureCropImageView.postScale(deltaScale);
        mGestureCropImageView.setImageToWrapCropBounds();
    }

    /**
     * 裁剪
     */
    private void crop(){

        Bitmap.CompressFormat mCompressFormat;
        if("png".equals(format.toLowerCase())) {
            mCompressFormat = Bitmap.CompressFormat.PNG;
        }else {
            mCompressFormat = Bitmap.CompressFormat.JPEG;
        }


        this.mGestureCropImageView.cropAndSaveImage(mCompressFormat, 90, new BitmapCropCallback() {

            @Override
            public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                Intent data = new Intent();
                data.putExtra(Crop.CROP_TARGET_URI,resultUri);
                data.putExtra(Crop.CROP_OUTPUT_IMAGE_WIDTH, imageWidth);
                data.putExtra(Crop.CROP_OUTPUT_IMAGE_HEIGHT, imageHeight);
                setResult(Activity.RESULT_OK, data);
                finish();
            }

            @Override
            public void onCropFailure(@NonNull Throwable t) {
                //裁剪失败
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private class OnViewClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            v.postDelayed(()-> v.setEnabled(true),500);

            if(v.getId() == R.id.mBackView) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }else if(v.getId() == R.id.mBtnFinish){
                crop();
            }else if(v.getId() == R.id.mLayoutCrop) {

                if(mCropDialog != null && !mCropDialog.isShowing()) {
                    int[] loc = new int[2];
                    v.getLocationOnScreen(loc);
                    int locX = loc[0] - ((mCropDialog.width - ScreenUtils.dip2px(50f)) / 2);
                    int locY = loc[1] - mCropDialog.height - ScreenUtils.getStatusBarHeight() - ScreenUtils.dip2px(10f);
                    mCropDialog.show(locX, locY);
                }
            }else if(v.getId() == R.id.mTvCropRatio) {
                mCropDialog.cancel();
                AspectRatio entity = (AspectRatio) v.getTag();
                if(entity.getAspectRatioX() == Crop.SIZE_ORIGINAL && entity.getAspectRatioY() == Crop.SIZE_ORIGINAL) {
                    //原始比例
                    mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
                    mTvCrop.setText("原始比例");
                }else {
                    //固定比例
                    mGestureCropImageView.setTargetAspectRatio(entity.getAspectRatioX() / entity.getAspectRatioY());
                    mTvCrop.setText((int)entity.getAspectRatioX() + ":" + (int)entity.getAspectRatioY());
                }
                resetScale();
            }else if(v.getId() == R.id.mLayoutScale) {
                if(mScaleDialog != null && !mScaleDialog.isShowing()) {
                    int[] loc = new int[2];
                    v.getLocationOnScreen(loc);
                    int locX = loc[0] - ((mScaleDialog.width - ScreenUtils.dip2px(50f)) / 2);
                    int locY = loc[1] - mScaleDialog.height - ScreenUtils.getStatusBarHeight() - ScreenUtils.dip2px(10f);
                    mScaleDialog.show(locX, locY);
                }
            }else if(v.getId() == R.id.mLayoutRotate) {
                if(mRotateDialog != null && !mRotateDialog.isShowing()) {
                    int[] loc = new int[2];
                    v.getLocationOnScreen(loc);
                    int locX = loc[0] - ((mRotateDialog.width - ScreenUtils.dip2px(50f)) / 2);
                    int locY = loc[1] - mRotateDialog.height - ScreenUtils.getStatusBarHeight() - ScreenUtils.dip2px(10f);
                    mRotateDialog.show(locX, locY);
                }
            }
        }
    }

    //手势回调监听
    private class OnTransformImageListener implements TransformImageView.TransformImageListener {

        @Override
        public void onRotate(float currentAngle) {
            //旋转
        }

        @Override
        public void onScale(float currentScale) {
            //缩放
        }

        @Override
        public void onLoadComplete() {
            mCropView.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
            finish();
        }

    };

}