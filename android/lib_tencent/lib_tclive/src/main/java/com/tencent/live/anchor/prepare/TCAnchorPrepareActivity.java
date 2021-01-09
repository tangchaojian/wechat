package com.tencent.live.anchor.prepare;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.tencent.live.R;
import com.tencent.live.anchor.TCAnchorActivity;
import com.tencent.live.anchor.screen.TCScreenAnchorActivity;
import com.tencent.live.audience.TCCustomSwitch;
import com.tencent.live.common.net.TCHTTPMgr;
import com.tencent.live.common.upload.TCUploadHelper;
import com.tencent.live.common.utils.TCConstants;
import com.tencent.live.common.utils.TCUtils;
import com.tencent.live.login.TCUserMgr;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Module:   TCAnchorPrepareActivity
 * <p>
 * Function: 主播开播设置页面
 * <p>
 * 1. 设置直播封面
 * <p>
 * 2. 设置直播标题
 * <p>
 * 3. 设置个人定位
 * <p>
 * 4. 设置摄像头推流或屏幕录制推流
 * <p>
 * 5. 设置分享到微信、微博、QQ等
 */
public class TCAnchorPrepareActivity extends Activity implements View.OnClickListener, TCUploadHelper.OnUploadListener, TCLocationHelper.OnLocationListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = TCAnchorPrepareActivity.class.getSimpleName();
    private static final int CAPTURE_IMAGE_CAMERA = 100;    // 封面：发起拍照
    private static final int IMAGE_STORE = 200;             // 封面：选择图库
    private static final int CROP_CHOOSE = 10;              // 封面：裁剪

    private TextView                        mTvReturn;      // 返回
    private TextView                        mTvPublish;     // 开始直播
    private TextView                        mTvPicTip;      // 封面提示
    private TextView                        mTvLocation;    // 显示定位的地址
    private TextView                        mTvTitle;       // 直播标题
    private Dialog                          mPicChsDialog;  // 图片选择弹窗
    private ImageView                       mIvCover;       // 图片封面
    private TCCustomSwitch                  mSwitchLocate;  // 发起定位的按钮
    private RadioGroup                      mRGRecordType;  // 推流类型：摄像头推流或屏幕录制推流
    private int                             mRecordType = TCConstants.RECORD_TYPE_CAMERA;   // 默认摄像头推流

    private Uri                              mSourceFileUri, mCropFileUri;      // 封面图源文件的Uri，裁剪过后的Uri
    private boolean                          mUploadingCover = false;           // 当前是否正在上传图片
    private boolean                          mPermission = false;               // 是否已经授权
    private TCUploadHelper                   mUploadHelper;                     // COS 存储封面图的工具类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor_prepare);
        mUploadHelper = new TCUploadHelper(this, this);

        mTvTitle = (TextView) findViewById(R.id.anchor_tv_title);
        mTvReturn = (TextView) findViewById(R.id.anchor_btn_cancel);
        mTvPicTip = (TextView) findViewById(R.id.anchor_pic_tips);
        mTvPublish = (TextView) findViewById(R.id.anchor_btn_publish);
        mIvCover = (ImageView) findViewById(R.id.anchor_btn_cover);
        mTvLocation = (TextView) findViewById(R.id.anchor_tv_location);
        mSwitchLocate = (TCCustomSwitch) findViewById(R.id.anchor_btn_location);
        mRGRecordType = (RadioGroup) findViewById(R.id.anchor_rg_record_type);
        mIvCover.setOnClickListener(this);
        mTvReturn.setOnClickListener(this);
        mTvPublish.setOnClickListener(this);
        mSwitchLocate.setOnClickListener(this);
        mRGRecordType.setOnCheckedChangeListener(this);

        mPermission = checkPublishPermission();
        initPhotoDialog();
        initCover();
    }

    /**
     *     /////////////////////////////////////////////////////////////////////////////////
     *     //
     *     //                      控件初始化相关
     *     //
     *     /////////////////////////////////////////////////////////////////////////////////
     */

    /**
     * 初始化封面图
     */
    private void initCover() {
        String strCover = TCUserMgr.getInstance().getCoverPic();
        if (!TextUtils.isEmpty(strCover)) {
            RequestManager req = Glide.with(this);
            req.load(strCover).into(mIvCover);
            mTvPicTip.setVisibility(View.GONE);
        } else {
            mIvCover.setImageResource(R.drawable.publish_background);
        }
    }


    /**
     * 图片选择对话框
     */
    private void initPhotoDialog() {
        mPicChsDialog = new Dialog(this, R.style.floag_dialog);
        mPicChsDialog.setContentView(R.layout.dialog_pic_choose);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = mPicChsDialog.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.BOTTOM);
        lp.width = (int) (display.getWidth()); //设置宽度

        mPicChsDialog.getWindow().setAttributes(lp);

        TextView camera = (TextView) mPicChsDialog.findViewById(R.id.chos_camera);
        TextView picLib = (TextView) mPicChsDialog.findViewById(R.id.pic_lib);
        TextView cancel = (TextView) mPicChsDialog.findViewById(R.id.anchor_btn_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(CAPTURE_IMAGE_CAMERA);
                mPicChsDialog.dismiss();
            }
        });

        picLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(IMAGE_STORE);
                mPicChsDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicChsDialog.dismiss();
            }
        });
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.anchor_btn_cancel) {
            finish();
        } else if (id == R.id.anchor_btn_publish) {//trim避免空格字符串
            if (TextUtils.isEmpty(mTvTitle.getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), "请输入非空直播标题", Toast.LENGTH_SHORT).show();
            } else if (TCUtils.getCharacterNum(mTvTitle.getText().toString()) > TCConstants.TV_TITLE_MAX_LEN) {
                Toast.makeText(getApplicationContext(), "直播标题过长 ,最大长度为" + TCConstants.TV_TITLE_MAX_LEN / 2, Toast.LENGTH_SHORT).show();
            } else if (mUploadingCover) {
                Toast.makeText(getApplicationContext(), getString(R.string.publish_wait_uploading), Toast.LENGTH_SHORT).show();
            } else if (!TCUtils.isNetworkAvailable(this)) {
                Toast.makeText(getApplicationContext(), "当前网络环境不能发布直播", Toast.LENGTH_SHORT).show();
            } else {
                startPublish();
            }
        } else if (id == R.id.anchor_btn_cover) {
            mPicChsDialog.show();
        } else if (id == R.id.anchor_btn_location) {
            if (mSwitchLocate.getChecked()) {
                mSwitchLocate.setChecked(false, true);
                mTvLocation.setText(R.string.text_live_close_lbs);
            } else {
                mSwitchLocate.setChecked(true, true);
                mTvLocation.setText(R.string.text_live_location);
                // 发起定位
                if (TCLocationHelper.checkLocationPermission(this)) {
                    if (!TCLocationHelper.getMyLocation(this, this)) {
                        mTvLocation.setText(getString(R.string.text_live_lbs_fail));
                        mSwitchLocate.setChecked(false, false);
                    }
                }
            }
        }
    }

    /**
     * 发起推流
     *
     */
    private void startPublish() {
        Intent intent = null;
        if (mRecordType == TCConstants.RECORD_TYPE_SCREEN) {
            //录屏
            intent = new Intent(this, TCScreenAnchorActivity.class);
        } else {
            intent = new Intent(this, TCAnchorActivity.class);
        }

        if (intent != null) {
            intent.putExtra(TCConstants.ROOM_TITLE,
                    TextUtils.isEmpty(mTvTitle.getText().toString()) ? TCUserMgr.getInstance().getNickname() : mTvTitle.getText().toString());
            intent.putExtra(TCConstants.USER_ID, TCUserMgr.getInstance().getUserId());
            intent.putExtra(TCConstants.USER_NICK, TCUserMgr.getInstance().getNickname());
            intent.putExtra(TCConstants.USER_HEADPIC, TCUserMgr.getInstance().getAvatar());
            intent.putExtra(TCConstants.COVER_PIC, TCUserMgr.getInstance().getCoverPic());
            intent.putExtra(TCConstants.USER_LOC,
                    mTvLocation.getText().toString().equals(getString(R.string.text_live_lbs_fail)) ||
                            mTvLocation.getText().toString().equals(getString(R.string.text_live_location)) ?
                            getString(R.string.text_live_close_lbs) : mTvLocation.getText().toString());
            startActivity(intent);
            finish();
        }
    }




    /**
     *     /////////////////////////////////////////////////////////////////////////////////
     *     //
     *     //                      定位相关
     *     //
     *     /////////////////////////////////////////////////////////////////////////////////
     */
    /**
     * 定位结果的回调
     *
     * @param code
     * @param lat1
     * @param long1
     * @param location
     */
    @Override
    public void onLocationChanged(int code, double lat1, double long1, String location) {
        if (mSwitchLocate.getChecked()) {
            if (0 == code) {
                mTvLocation.setText(location);
                setLocation(location);
            } else {
                mTvLocation.setText(getString(R.string.text_live_lbs_fail));
            }
        } else {
            setLocation("");
        }
    }

    /**
     * 上传定位的结果，设置到开播的信息
     *
     * @param location
     */
    private void setLocation(String location) {
        TCUserMgr.getInstance().setLocation(location, new TCHTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {

            }

            @Override
            public void onFailure(int code, final String msg) {
                TCAnchorPrepareActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "设置位置失败 " + msg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }



    /**
     *     /////////////////////////////////////////////////////////////////////////////////
     *     //
     *     //                      封面图相关
     *     //
     *     /////////////////////////////////////////////////////////////////////////////////
     */

    /**
     * 选择封面图：选择 -> 裁剪 -> 上传到COS
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAPTURE_IMAGE_CAMERA:
                    cropPhoto(mSourceFileUri);
                    break;
                case IMAGE_STORE:
                    String path = TCUtils.getPath(this, data.getData());
                    if (null != path) {
                        Log.d(TAG, "cropPhoto->path:" + path);
                        File file = new File(path);

                        Uri uri = null;
                        if (Build.VERSION.SDK_INT >= 24) {//7.0 Android N
                            uri = FileProvider.getUriForFile(TCAnchorPrepareActivity.this, "com.tencent.live.fileprovider", file);
                        } else {//7.0以下
                            uri = Uri.fromFile(file);
                        }

                        cropPhoto(uri);
                    }
                    break;
                case CROP_CHOOSE:
                    mUploadingCover = true;
                    mTvPicTip.setVisibility(View.GONE);
                    // 上传到 COS
                    mUploadHelper.uploadPic(mCropFileUri.getPath());
                    break;

            }
        }

    }

    /**
     * COS 存储上传封面图回调的结果
     *
     * @param code
     * @param url
     */
    @Override
    public void onUploadResult(int code, String url) {
        if (0 == code) {
            TCUserMgr.getInstance().setCoverPic(url, null);
            RequestManager req = Glide.with(this);
            req.load(url).into(mIvCover);
            Toast.makeText(this, "上传封面成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "上传封面失败，错误码 " + code, Toast.LENGTH_SHORT).show();
        }
        mUploadingCover = false;
    }


    /**
     * 裁剪图片
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        mCropFileUri = createCoverUri("_crop");
        Log.i("TCJ", "mCropFileUri->" + mCropFileUri.toString());
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 750);
        intent.putExtra("aspectY", 550);
        intent.putExtra("outputX", 750);
        intent.putExtra("outputY", 550);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropFileUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_CHOOSE);
    }

    /**
     * 创建封面图地址
     *
     * @param type
     * @return
     */
    private Uri createCoverUri(String type) {
        String filename = TCUserMgr.getInstance().getUserId() + type + ".jpg";

        File sdcardDir = getExternalFilesDir(null);

        if (sdcardDir == null) {
            Log.e(TAG, "createCoverUri sdcardDir is null");
            return null;
        }
        String path = sdcardDir + "/xiaozhibo";

        File outputImage = new File(path, filename);
        if (ContextCompat.checkSelfPermission(TCAnchorPrepareActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TCAnchorPrepareActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, TCConstants.WRITE_PERMISSION_REQ_CODE);
            return null;
        }
        try {
            File pathFile = new File(path);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            if (outputImage.exists()) {
                outputImage.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "生成封面失败", Toast.LENGTH_SHORT).show();
        }

        if("_crop".equals(type)) {
            return Uri.fromFile(outputImage);
        }else {
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= 24) {//7.0 Android N
                uri = FileProvider.getUriForFile(TCAnchorPrepareActivity.this, "com.tencent.live.fileprovider", outputImage);
            } else {//7.0以下
                uri = Uri.fromFile(outputImage);
            }
            return uri;
        }


    }

    /**
     * 获取图片资源
     *
     * @param type 类型（本地IMAGE_STORE/拍照CAPTURE_IMAGE_CAMERA）
     */
    private void getPicFrom(int type) {
        if (!mPermission) {
            Toast.makeText(this, getString(R.string.tip_no_permission), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (type) {
            case CAPTURE_IMAGE_CAMERA:
                mSourceFileUri = createCoverUri("");
                if(ContextCompat.checkSelfPermission( TCAnchorPrepareActivity.this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(TCAnchorPrepareActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            TCConstants.CAMERA_PERMISSION_REQ_CODE);
                } else {
                    takePhoto();
                }
                break;
            case IMAGE_STORE:
                mSourceFileUri = createCoverUri("_select");
                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
                intent_album.setType("image/*");
                startActivityForResult(intent_album, IMAGE_STORE);
                break;

        }
    }

    /**
     *  打开摄像头拍照
     *
     */
    private void takePhoto() {
        Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, mSourceFileUri);
        startActivityForResult(intent_photo, CAPTURE_IMAGE_CAMERA);
    }

    /**
     *     /////////////////////////////////////////////////////////////////////////////////
     *     //
     *     //                      动态权限检查相关
     *     //
     *     /////////////////////////////////////////////////////////////////////////////////
     */

    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TCAnchorPrepareActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TCAnchorPrepareActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(TCAnchorPrepareActivity.this,
                        permissions.toArray(new String[0]),
                        TCConstants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }

        return true;
    }

    private boolean checkScrRecordPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case TCConstants.LOCATION_PERMISSION_REQ_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!TCLocationHelper.getMyLocation(this, this)) {
                        mTvLocation.setText(getString(R.string.text_live_lbs_fail));
                        mSwitchLocate.setChecked(false, false);
                    }
                }
                break;
            case TCConstants.WRITE_PERMISSION_REQ_CODE:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                mPermission = true;
                break;
            case TCConstants.CAMERA_PERMISSION_REQ_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.anchor_rb_record_camera) {
            mRecordType = TCConstants.RECORD_TYPE_CAMERA;
        } else if (checkedId == R.id.anchor_rb_record_screen) {
            if (!checkScrRecordPermission()) {
                Toast.makeText(getApplicationContext(), "当前安卓系统版本过低，仅支持5.0及以上系统", Toast.LENGTH_SHORT).show();
                mRGRecordType.check(R.id.anchor_rb_record_camera);
                return;
            }
            try {
                TCUtils.checkFloatWindowPermission(TCAnchorPrepareActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mRecordType = TCConstants.RECORD_TYPE_SCREEN;
        }
    }
}
