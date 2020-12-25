package com.tcj.sunshine.player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Region;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.tcj.sunshine.lib.video.CoverEntity;
import com.tcj.sunshine.tools.ActivityUtils;
import com.tcj.sunshine.tools.DateUtils;
import com.tcj.sunshine.tools.GlideUtils;
import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.tools.StringUtils;
import com.tcj.sunshine.tools.VideoUtils;
import com.tcj.sunshine.ui.textview.MarqueeTextView;

import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnBufferingUpdateListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnSeekCompleteListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnVideoSizeChangedListener;
import tv.danmaku.ijk.media.player.ISurfaceTextureHolder;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * IJK视频播放器
 */
public class VideoPlayer extends FrameLayout implements BaseVideoPlayer{

    private Context context;

    //由ijkplayer提供，用于播放视频，需要给他传入一个surfaceView
    private IjkMediaPlayer mMediaPlayer = null;//播放器
    private ImageView mIvCover;//封面图
    private SurfaceView mSurfaceView;//显示画面的SurfaceView
    private ProgressBar mProgressBar;//播放进度条

    private View mTitleView;
    private View mLayoutBack;
    private MarqueeTextView mTvTitle;

    private ViewGroup mHandleView;//操作视频的view
    private View mLayoutHandle;//mIvHandle体积太小，监听mLayoutHandle的点击事件
    private ImageView mIvHandle;//暂停/播放/重播
    private TextView mTvNotice;
    private View mBottomToolsView;//底部工具view
    private TextView mTvTime;//显示当前播放时长和总时长
    private VideoSeekBar mSeekBar;//拖拽快进/快退
    private View mLayoutWindow;//全屏窗，小窗
    private ImageView mIvWindow;//全屏窗，小窗

    private View mLoadView;//加载窗口
    private ImageView mIvAnim;//加载Image


    private AudioManager mAudioManager;//音频管理
    private AudioFocusHelper mAudioFocusHelper;//音频焦点

    //视频文件地址
    private String mPath;

    private String title;

    //视频请求header
    private Map<String, String> mHeader;

    //是否支持硬解码
    private boolean mEnableMediaCodec = false;

    //是否循环播放
    private boolean isLooping = false;

    //是否自动播放
    private boolean isAutoPlay = true;

    private boolean isPrepared = false;

    private boolean isTouchEnable = true;//是否允许touch_down显示HandleView

    private boolean isShowProgressBarVisible = true;//是否显示底部进度条

    private @DrawableRes int mPlayIconResId = R.mipmap.icon_video_player_play;
    private @DrawableRes int mPauseIconResId = R.mipmap.icon_video_player_pause;
    private @DrawableRes int mRePlayIconResId = R.mipmap.icon_video_player_replay;

    private int mProgress;
    private int mMaxProgress;

    private int mRealVideoWidth = 0;//SurfaceView的宽
    private int mRealVideoHeight = 0;//SurfaceView的高

    private int mMeasuredWidth = 0;//VideoPlayer的宽
    private int mMeasuredHeight = 0;//VideoPlayer的高

    private Region mSeekBarRegion;//SeekBar拖拽的onTouch面积

    private VideoMode mVideoMode = VideoMode.MODE_CENTER_INSIDE;//默认模式:按比例缩放，不超出parent布局,居中显示


    //监听接口
    private VideoPreparedListener onPreparedListener = new VideoPreparedListener();
    private VideoSeekCompleteListener onSeekCompleteListener = new VideoSeekCompleteListener();
    private VideoSizeChangedListener onVideoSizeChangedListener = new VideoSizeChangedListener();
    private VideoInfoListener onInfoListener = new VideoInfoListener();
    private VideoBufferingUpdateListener onBufferingUpdateListener = new VideoBufferingUpdateListener();
    private VideoCompletionListener onCompletionListener = new VideoCompletionListener();
    private VideoErrorListener onErrorListener = new VideoErrorListener();

    //接口
    private OnSwitchWindowListener onSwitchWindowListener;//小屏/全屏窗口切换回调
    private OnProgressListener onProgressListener;//进度接口回调

    //进度线程
    private ProgressThread mProgressThread;
    private VideoPlayerHandler mVideoPlayerHandler;

    private long mCurrentTimeMs;//当前时间
    private long mCountTimeMs;//当前时间
    private int mCurrentProgress;//当前进度
    private static final int MAX_PROGRESS = 100;//最大进度
    private boolean hasError = false;

    private static final int STATUS_NONE = 0;           //初始化
    private static final int STATUS_READY_PLAY = 1;     //开始播放
    private static final int STATUS_PLAY = 2;           //播放中
    private static final int STATUS_PAUSE = 3;          //播放暂停
    private static final int STATUS_STOP = 4;           //播放停止
    private static final int STATUS_END = 5;            //播放结束
    private static final int STATUS_ERROR = 6;          //有错误

    private int status = STATUS_NONE;

    public static final int MODE_WINDOW_NORMAL = 0;//正常播放
    public static final int MODE_WINDOW_FULL_SCREEN = 1;//全屏播放
    private int mWindowMode = MODE_WINDOW_NORMAL;

    private boolean isSeekBarTouch = false;//是否正在触摸SeekBar控件

    public VideoPlayer(@NonNull Context context) {
        super(context);
        this.initUI(context, null);
    }

    public VideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context, attrs);
    }

    public VideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context, attrs);
    }


    //初始化
    private void initUI(Context context, AttributeSet attrs) {
        this.context = context;

        //创建一个SurfaceView对象
        this.setBackgroundColor(Color.BLACK);


        this.mSurfaceView = new SurfaceView(context);
        this.mIvCover = new ImageView(context);

        this.mSurfaceView.getHolder().addCallback(new SurfaceHolderCallback());
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER);
        this.addView(mSurfaceView, 0, params);

        this.mIvCover.setVisibility(View.GONE);
        LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER);
        this.addView(this.mIvCover, 1, params2);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_video_player_progress_bar, this,true);
        mProgressBar = this.findViewById(R.id.mProgressBar);
        mProgressBar.setProgress(0);
        mProgressBar.setMax(MAX_PROGRESS);
        mProgressBar.setVisibility(View.GONE);

        this.mMaxProgress = MAX_PROGRESS;
        this.mProgress = 0;

        inflater.inflate(R.layout.view_video_player_handle, this, true);
        this.mHandleView = this.findViewById(R.id.mHandlerView);
        this.mHandleView.setVisibility(View.GONE);

        this.mTitleView = this.findViewById(R.id.mTitleView);
        this.mLayoutBack = this.findViewById(R.id.mLayoutBack);
        this.mTvTitle = this.findViewById(R.id.mTvTitle);

        if(title != null) this.mTvTitle.setText(title);

        this.mLayoutHandle = this.findViewById(R.id.mLayoutHandle);
        this.mIvHandle = this.findViewById(R.id.mIvHandle);
        this.mTvNotice = this.findViewById(R.id.mTvNotice);
        this.mBottomToolsView = this.findViewById(R.id.mBottomToolsView);
        this.mTvTime = this.findViewById(R.id.mTvTime);
        this.mSeekBar = this.findViewById(R.id.mSeekBar);
        this.mLayoutWindow = this.findViewById(R.id.mLayoutWindow);
        this.mIvWindow = this.findViewById(R.id.mIvWindow);

        inflater.inflate(R.layout.view_video_player_load, this, true);
        this.mLoadView = this.findViewById(R.id.mLoadView);
        this.mIvAnim = this.findViewById(R.id.mIvAnim);
        this.mLoadView.setVisibility(View.GONE);

        this.mVideoPlayerHandler = new VideoPlayerHandler();

        this.mAudioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        this.mAudioFocusHelper = new AudioFocusHelper();

        this.mTvTitle.requestFocus();
        this.mTvTitle.setSelected(true);

        this.mLayoutBack.setOnClickListener(new OnViewClickListener());
        this.mLayoutHandle.setOnClickListener(new OnViewClickListener());
        this.mLayoutWindow.setOnClickListener(new OnViewClickListener());
        this.mSeekBar.setOnSeekBarTouchListener(new OnSeekBarTouchListener());
    }


    //创建一个新的IjkMediaPlayer
    private IjkMediaPlayer createIJKMediaPlayer() {
        IjkMediaPlayer mMediaPlayer = new IjkMediaPlayer();

        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek"); //快速seekto
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 100 * 1024);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);

        //设置声音
        mMediaPlayer.setVolume(1.0f, 1.0f);
        //是否设置硬解码
        this.setEnableMediaCodec(mMediaPlayer, mEnableMediaCodec);

        //设置监听
        mMediaPlayer.setOnPreparedListener(onPreparedListener);
        mMediaPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
        mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        mMediaPlayer.setOnInfoListener(onInfoListener);
        mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        mMediaPlayer.setOnCompletionListener(onCompletionListener);
        mMediaPlayer.setOnErrorListener(onErrorListener);

        return mMediaPlayer;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        if(mRealVideoWidth == 0 || mRealVideoHeight == 0)return;

        if(this.mMeasuredWidth != measuredWidth && this.mMeasuredHeight != measuredHeight) {
            changeSurfaceSize(measuredWidth, measuredHeight);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(isTouchEnable && mHandleView.getVisibility() == View.GONE && status != STATUS_NONE && !isSeekBarTouch) {
                    mVideoPlayerHandler.sendEmptyMessage(1);
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    //设置是否开启硬解码
    private void setEnableMediaCodec(IjkMediaPlayer mMediaPlayer, boolean isEnable) {
        int value = isEnable ? 1 : 0;
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", value);//开启硬解码
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", value);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", value);
    }


    /**
     * 切屏时，改变SurfaceView的大小
     */
    private void changeSurfaceSize(int measuredWidth, int measuredHeight){
//        LogUtils.i("sunshine-player", "measuredWidth->" + measuredWidth);
//        LogUtils.i("sunshine-player", "measuredHeight->" + measuredHeight);

        int[] size = null;
        switch (mVideoMode) {
            case MODE_CENTER_CROP:
                size = this.getModeCenterCropSize(measuredWidth, measuredHeight);
                break;
            case MODE_CENTER_INSIDE:
                default:
                size = this.getModeCenterInsideSize(measuredWidth, measuredHeight);
                break;
        }

        int finalWidth = size[0];
        int finalHeight = size[1];

        FrameLayout.LayoutParams params = (LayoutParams) mSurfaceView.getLayoutParams();
        params.width = finalWidth;
        params.height = finalHeight;
        mSurfaceView.requestLayout();

        FrameLayout.LayoutParams params2 = (LayoutParams) mIvCover.getLayoutParams();
        params2.width = finalWidth;
        params2.height = finalHeight;
        mIvCover.requestLayout();
    }

    private int[] getModeCenterInsideSize(int measuredWidth, int measuredHeight){
        int finalWidth;
        int finalHeight;
        float ratio; //输出目标的宽高或高宽比例

        this.mMeasuredWidth = measuredWidth;
        this.mMeasuredHeight = measuredHeight;

        if(measuredWidth >= measuredHeight) {
            //宽大于高
            ratio = (float)mRealVideoHeight / (float)mRealVideoWidth;
            finalWidth = measuredWidth;
            finalHeight = (int)(finalWidth * ratio);
            if(finalHeight > measuredHeight) {
                ratio = (float)measuredHeight / (float) finalHeight;
                finalWidth = (int)(finalWidth * ratio);
                finalHeight = measuredHeight;
            }
        }else {
            ratio = (float) mRealVideoWidth / (float)mRealVideoHeight;
            finalHeight = measuredHeight;
            finalWidth = (int)(finalHeight * ratio);
            if(finalWidth > measuredWidth) {
                ratio = (float)measuredWidth / (float)finalWidth;
                finalHeight = (int)(finalHeight * ratio);
                finalWidth = measuredWidth;
            }
        }

        int[] size = new int[2];
        size[0] = finalWidth;
        size[1] = finalHeight;
        return size;
    }


    private int[] getModeCenterCropSize(int measuredWidth, int measuredHeight){

        float scale = Math.max(measuredWidth * 1.0f / mRealVideoWidth, measuredHeight * 1.0f / mRealVideoHeight);
        int finalWidth = (int)(scale * mRealVideoWidth);
        int finalHeight = (int)(scale * mRealVideoHeight);
        int[] size = new int[2];
        size[0] = finalWidth;
        size[1] = finalHeight;
        return size;
    }

    /**
     * 跟新进度
     */
    private void updateProgress(long mCurrentTimeMs){
        if(mMediaPlayer != null && !mProgressThread.stop){

            String curentTimeStr = DateUtils.formatTime(mCurrentTimeMs);
            String countTimeStr = DateUtils.formatTime(mCountTimeMs);
            int progress = (int) ((mCurrentTimeMs * 1.0f / mCountTimeMs) * MAX_PROGRESS) ;
            if(progress == 100) {
                mProgressThread.stopThread();
            }
            if(mCurrentProgress != progress){
                mCurrentProgress = progress;
                Bundle data =new Bundle();
                data.putString("curentTimeStr", curentTimeStr);
                data.putString("countTimeStr", countTimeStr);
                data.putInt("progress", progress);
                Message msg = new Message();
                msg.setData(data);
                mVideoPlayerHandler.sendMessage(msg);
            }

        }
    }

    /**
     * 设置状态
     * @param status
     */
    private void setStatus(int status){
        int oldStatus = this.status;
        this.status = status;
        this.mTvNotice.setVisibility(View.GONE);
        switch (this.status) {
            case STATUS_NONE:
                if (mProgressThread == null) {
                    mProgressThread = new ProgressThread();
                    mProgressThread.start();
                }

                if(mProgressThread.stop){
                    mProgressThread.restartThread();
                }

                this.mHandleView.setVisibility(View.GONE);
                this.mBottomToolsView.setVisibility(View.VISIBLE);
                this.mProgressBar.setProgress(0);
                this.mSeekBar.setProgress(0);
                mProgress = 0;
                if(onProgressListener != null)onProgressListener.onProgress(VideoPlayer.this,0, "00:00", DateUtils.formatTime(this.getDuration()));
                break;
            case STATUS_READY_PLAY:
                this.mIvHandle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIvHandle.setImageResource(mPlayIconResId);
                        mHandleView.setVisibility(View.VISIBLE);
                        mBottomToolsView.setVisibility(View.GONE);
                    }
                }, 500);

                break;
            case STATUS_PLAY:
                this.mIvCover.setVisibility(View.GONE);
                this.mIvHandle.setImageResource(mPauseIconResId);
                this.mHandleView.setVisibility(View.GONE);
                this.mBottomToolsView.setVisibility(View.VISIBLE);

                if (mProgressThread != null && (oldStatus == STATUS_PAUSE || oldStatus == STATUS_STOP || oldStatus == STATUS_END || oldStatus == STATUS_ERROR)) {
                    mProgressThread.restartThread();
                }
                break;
            case STATUS_PAUSE:
                this.mHandleView.setVisibility(View.GONE);
                this.mBottomToolsView.setVisibility(View.VISIBLE);
                this.mIvHandle.setImageResource(mPlayIconResId);
                break;
            case STATUS_STOP:
                this.mHandleView.setVisibility(View.VISIBLE);
                this.mBottomToolsView.setVisibility(View.GONE);
                this.mIvHandle.setImageResource(mPlayIconResId);
                this.mProgressBar.setProgress(0);
                this.mSeekBar.setProgress(0);
                mProgress = 0;
                if(onProgressListener != null)onProgressListener.onProgress(VideoPlayer.this,0, "00:00", DateUtils.formatTime(this.getDuration()));
                break;
            case STATUS_END:
                this.mHandleView.setVisibility(View.GONE);
                this.mBottomToolsView.setVisibility(View.VISIBLE);
                this.mIvHandle.setImageResource(mRePlayIconResId);
                this.mTvNotice.setText("重播");
                this.mTvNotice.setVisibility(View.VISIBLE);
                break;
            case STATUS_ERROR:
                this.mHandleView.setVisibility(View.GONE);
                this.mBottomToolsView.setVisibility(View.VISIBLE);
                this.mIvHandle.setImageResource(mRePlayIconResId);
                this.mTvNotice.setText("播放错误");
                this.mTvNotice.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showAnimView() {
        this.mLoadView.setVisibility(View.VISIBLE);
        AnimationDrawable animationDrawable = (AnimationDrawable) this.mIvAnim.getDrawable();
        animationDrawable.start();
    }

    private void hideAnimView() {
        this.mLoadView.postDelayed(()-> this.mLoadView.setVisibility(View.GONE),500);
    }

    /**
     * 设置是否硬解码
     * @param isEnable
     */
    public void setEnableMediaCodec(boolean isEnable) {
        this.mEnableMediaCodec = isEnable;
        this.setEnableMediaCodec(mMediaPlayer, isEnable);
    }

    //设置播放地址
    @Override
    public void setPath(String path) {
        setPath(path, null);
    }

    @Override
    public void setPath(String path, Map<String, String> header) {
        mPath = path;
        mHeader = header;
        mIvCover.setVisibility(View.VISIBLE);
        String url = VideoUtils.getCacheCoverImagePath(mPath);
        LogUtils.i("sunshine-player", "封面图[" + url + "]");
        if(StringUtils.isNoEmpty(url)) {
            GlideUtils.loadImage(url, mIvCover);
        }else {
            VideoUtils.getFirstFrameImage(this.mPath, new VideoUtils.OnVideoCallBack() {
                @Override
                public void callback(CoverEntity cover) {

                    if (cover != null && cover.bitmaps != null && !cover.bitmaps.isEmpty()) {
                        mIvCover.setImageBitmap(cover.bitmaps.get(0));
                    }
                }
            });
        }
    }

    @Override
    public String getPath() {
        return mPath;
    }



    /**
     * 开始加载视频
     */
    @Override
    public void start(){
        try {

            this.showAnimView();

            if (this.mMediaPlayer != null) {
                this.mMediaPlayer.stop();
                this.mMediaPlayer.release();
            }
            this.hasError = false;
            this.mMediaPlayer = this.createIJKMediaPlayer();
            this.mMediaPlayer.setDisplay(mSurfaceView.getHolder());
            this.mMediaPlayer.setDataSource(context, Uri.parse(mPath), mHeader);
            this.mMediaPlayer.prepareAsync();
            setStatus(STATUS_NONE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 继续播放
     */
    @Override
    public void play() {
        if (mMediaPlayer != null) {
            this.hasError = false;
            mMediaPlayer.start();
            mAudioFocusHelper.requestFocus();
            this.setStatus(STATUS_PLAY);
        }
    }

    /**
     * 重新播放
     */
    @Override
    public void restart() {
        try {
            if (mMediaPlayer != null) {
                this.hasError = false;
                mMediaPlayer.seekTo(0);
                start();
            }
        } catch (Exception e) {

        }
    }


    /**
     * 释放播放器资源
     */
    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioFocusHelper.abandonFocus();

            if(mProgressThread != null)mProgressThread.destoryThread();
        }
    }

    /**
     * 暂停播放
     */
    @Override
    public void pause() {
        if (mMediaPlayer != null) {

            this.mMediaPlayer.pause();
            this.mAudioFocusHelper.abandonFocus();

            if(mProgressThread != null)mProgressThread.stopThread();

            this.setStatus(STATUS_PAUSE);
        }
    }


    /**
     * 停止播放
     */
    @Override
    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mAudioFocusHelper.abandonFocus();
            if(mProgressThread != null)mProgressThread.stopThread();

            this.setStatus(STATUS_STOP);//结束播放
            setHandleViewVisible(true, false);
        }
    }

    /**
     * 重置
     */
    @Override
    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mAudioFocusHelper.abandonFocus();
            setStatus(STATUS_NONE);
        }
    }

    /**
     * 是否循环播放
     * @param isLooping
     */
    @Override
    public void setLooping(boolean isLooping) {
        this.isLooping = isLooping;
    }

    /**
     * 改变播放进度
     * @param progress
     */
    @Override
    public void seekTo(int progress){

        showAnimView();
        if (mMediaPlayer != null) {
            float percent = progress * 1.0f / 100;
            mCurrentTimeMs = (long)(percent * mCountTimeMs);
            LogUtils.i("sunshine-player", "mCurrentTimeMs->" + mCurrentTimeMs);
            mMediaPlayer.seekTo(mCurrentTimeMs);
        }
        updateProgress(mCurrentTimeMs);
        if(mCurrentTimeMs != mCountTimeMs) {
            play();
        }
    }


    /**
     * 视频总时长
     * @return
     */
    @Override
    public long getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    /**
     * 当前播放时长
     * @return
     */
    @Override
    public long getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }


    /**
     * 是否正在播放
     * @return
     */
    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }


    /**
     * 设置是否全屏
     * @param isFull
     */
    @Override
    public void setFullScreen(boolean isFull){
        if(isFull) {
            this.mWindowMode = MODE_WINDOW_FULL_SCREEN;
            if(this.mTitleView != null)this.mTitleView.setVisibility(View.VISIBLE);
            Activity activity = ActivityUtils.getActivityByView(this);
            if(activity != null) {
                //如果是activity，则旋转屏幕，成为横屏
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }else {
            this.mWindowMode = MODE_WINDOW_NORMAL;
            if(this.mTitleView != null)this.mTitleView.setVisibility(View.GONE);
            Activity activity = ActivityUtils.getActivityByView(this);
            if(activity != null) {
                //如果是activity，则旋转屏幕，成为竖屏
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

        if(onSwitchWindowListener != null)onSwitchWindowListener.onSwitchWindow(this, this.mWindowMode);
    }


    /**
     * 设置窗口切换监听
     * @param mListener
     */
    @Override
    public void setOnSwitchWindowListener(OnSwitchWindowListener mListener) {
        this.onSwitchWindowListener = mListener;
    }

    /**
     * 设置播放进度监听
     * @param mListener
     */
    @Override
    public void setOnProgressListener(OnProgressListener mListener) {
        this.onProgressListener = mListener;
    }

    /**
     * 是否自动播放
     */
    @Override
    public void setAutoPlay(boolean isAutoPlay){
        this.isAutoPlay = isAutoPlay;
    }

    /**
     * 是否可以准备播放了
     * @return
     */
    @Override
    public boolean isPrepared(){
        return isPrepared;
    }
    /**
     * VideoPlayer是否添加touch事件
     * @param enable ture :触摸会显示mHandleView, false 不会显示,默认会显示
     */
    public void setTouchEnable(boolean enable){
        this.isTouchEnable = enable;
    }

    /**
     * 当前进度
     * @return
     */
    @Override
    public int getProgress() {
        return this.mProgress;
    }


    /**
     * 最大进度
     * @return
     */
    @Override
    public int getMaxProgress() {
        return mMaxProgress;
    }

    @Override
    public void setPlayIcon(int resId) {
        this.mPlayIconResId = resId;
    }

    @Override
    public void setPauseIcon(int resId) {
        this.mPauseIconResId = resId;
    }

    @Override
    public void setRePlayIcon(int resId) {
        this.mRePlayIconResId = resId;
    }

    @Override
    public void setHandleIconSize(int width, int height) {
        int min = ScreenUtils.dip2px(20f);
        if(width > min && height > min) {
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) mLayoutHandle.getLayoutParams();
            params1.width = width + min;
            params1.height = height + min;
            mLayoutHandle.requestLayout();

            FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) mIvHandle.getLayoutParams();
            params2.width = width;
            params2.height = height;
            mIvHandle.requestLayout();
        }
    }

    @Override
    public void setFullScreenIconVisible(boolean isVisible) {
        if(isVisible) {
            mIvWindow.setVisibility(View.VISIBLE);
        }else {
            mIvWindow.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置进度条是否可见
     * @param isVisible
     */
    public void setProgressBarVisible(boolean isVisible) {
        this.isShowProgressBarVisible = isVisible;
        this.mProgressBar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title){
        this.title = title;
        if(this.mTvTitle != null) this.mTvTitle.setText(title);
    }

    public void setTitleView(View view) {
        if(view == null)return;

        if(this.mTitleView != null && this.mTitleView.getParent() != null) {
            this.mHandleView.removeView(mTitleView);
        }

        this.mTitleView = view;
        this.mHandleView.addView(view);
        this.mTitleView.setVisibility(View.GONE);
    }


    //===============================播放器监听回调======================================================

    private class OnViewClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            v.postDelayed(()-> v.setEnabled(true), 200);

            if(v.getId() == R.id.mLayoutBack) {
                mIvWindow.setImageResource(R.mipmap.icon_video_player_max_window);
                setFullScreen(false);
            }else if (v.getId() == R.id.mLayoutHandle) {
                if(status == STATUS_READY_PLAY) {
                    //视频可以准备播放
                    play();
                    //点击2s之后，隐藏
                    setHandleViewVisible(false);
                }else if(status == STATUS_PLAY) {
                    //视频正在播放，暂停
                    pause();
                    setHandleViewVisible(true);
                    return;
                }else if(status == STATUS_PAUSE) {
                    //视频暂停了，播放
                    play();
                    //点击2s之后，隐藏
                    setHandleViewVisible(false);
                }else if(status == STATUS_STOP) {
                    //视频停止了，重新播放
                    showAnimView();
                    reset();
                    start();
                    //点击2s之后，隐藏
                    setHandleViewVisible(false);
                }else if(status == STATUS_END) {
                    //视频播放结束了，重新播放
                    restart();
                    //点击2s之后，隐藏
                    setHandleViewVisible(false);
                }else if(status == STATUS_ERROR) {
                    showAnimView();
                    reset();
                    start();
                    //点击2s之后，隐藏
                    setHandleViewVisible(false);
                }
            }else if(v.getId() == R.id.mLayoutWindow) {
                if(mWindowMode == MODE_WINDOW_NORMAL) {
                    mIvWindow.setImageResource(R.mipmap.icon_video_player_min_window);
                    setFullScreen(true);
                }else {
                    mIvWindow.setImageResource(R.mipmap.icon_video_player_max_window);
                    setFullScreen(false);
                }
            }
        }
    }

    /**
     * 是否显示mHandleView
     * @param isVisible 是否显示
     */
    private void setHandleViewVisible(boolean isVisible){
        //默认延迟
        this.setHandleViewVisible(isVisible, true);
    }
    /**
     * 是否显示mHandleView
     * @param isVisible 是否显示
     * @param isDelayedHide 是否延迟隐藏
     */
    private void setHandleViewVisible(boolean isVisible, boolean isDelayedHide){
        if(isVisible) {
            if(isDelayedHide) {
                mVideoPlayerHandler.sendEmptyMessage(1);
            }else {
                mVideoPlayerHandler.removeMessages(1);
                mVideoPlayerHandler.removeMessages(2);
                mVideoPlayerHandler.sendEmptyMessage(3);
            }
        }else {
            mVideoPlayerHandler.removeMessages(2);
            mVideoPlayerHandler.sendEmptyMessage(2);
        }
    }

    /**
     * SurfaceHolder的回调监听
     */
    private class SurfaceHolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (mMediaPlayer != null) {
                IMediaPlayer mediaPlayer = mMediaPlayer;
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        && (mediaPlayer instanceof ISurfaceTextureHolder)) {

                    ISurfaceTextureHolder textureHolder = (ISurfaceTextureHolder) mediaPlayer;
                    textureHolder.setSurfaceTexture(null);
                }
                mMediaPlayer.setDisplay(holder);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }


    /**
     * 音频焦点改变监听
     */
    private class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
        boolean startRequested = false;
        boolean pausedForLoss = false;
        int currentFocus = 0;

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (currentFocus == focusChange) {
                return;
            }

            currentFocus = focusChange;
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN://获得焦点
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT://暂时获得焦点
                    if (startRequested || pausedForLoss) {
                        play();
                        startRequested = false;
                        pausedForLoss = false;
                    }
                    if (mMediaPlayer != null){
                        //恢复音量
                        mMediaPlayer.setVolume(1.0f, 1.0f);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS://焦点丢失
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://焦点暂时丢失
                    if (isPlaying()) {
                        pausedForLoss = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://此时需降低音量
                    if (mMediaPlayer != null && isPlaying()) {
                        mMediaPlayer.setVolume(0.1f, 0.1f);
                    }
                    break;
            }
        }

        boolean requestFocus() {
            if (currentFocus == AudioManager.AUDIOFOCUS_GAIN) {
                return true;
            }

            if (mAudioManager == null) {
                return false;
            }

            int status = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status) {
                currentFocus = AudioManager.AUDIOFOCUS_GAIN;
                return true;
            }

            startRequested = true;
            return false;
        }

        boolean abandonFocus() {

            if (mAudioManager == null) {
                return false;
            }

            startRequested = false;
            int status = mAudioManager.abandonAudioFocus(this);
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status;
        }
    }


    /**
     * 视频准备播放监听
     */
    private class VideoPreparedListener implements  OnPreparedListener {

        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            if(isShowProgressBarVisible) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            hideAnimView();
            mCountTimeMs = iMediaPlayer.getDuration();
            isPrepared = true;
            if(isAutoPlay) {
                play();
            }else {
                //只有第一次不是自动播放，以后都是自动播放
                isAutoPlay = true;
                //准备播放
                setStatus(STATUS_READY_PLAY);
            }
        }
    };

    /**
     * 视频尺寸回调监听
     */
    private class VideoSizeChangedListener  implements OnVideoSizeChangedListener {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            int videoWidth = iMediaPlayer.getVideoWidth();
            int videoHeight = iMediaPlayer.getVideoHeight();
            if (videoWidth != 0 && videoHeight != 0) {
                mRealVideoWidth = videoWidth;
                mRealVideoHeight = videoHeight;
                mSurfaceView.getHolder().setFixedSize(videoWidth, videoHeight);
            }

            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();

            changeSurfaceSize(measuredWidth, measuredHeight);
        }
    }

    /**
     * 视频快进，快退监听
     */
    private class VideoSeekCompleteListener implements OnSeekCompleteListener {

        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            hideAnimView();
        }
    }

    private class VideoInfoListener implements OnInfoListener {

        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            return false;
        }
    }

    //缓存监听
    private class VideoBufferingUpdateListener implements OnBufferingUpdateListener {

        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int progress) {
            mSeekBar.setSecondaryProgress(progress);
        }
    }

    //播放完成监听
    private class VideoCompletionListener implements OnCompletionListener {

        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            //播放完成
            if(!hasError) {
                //没有错误
                if(isLooping) {
                    mCurrentTimeMs = mCountTimeMs;
                    updateProgress(mCurrentTimeMs);
                    setStatus(STATUS_END);
                    restart();
                    setHandleViewVisible(false);
                }else {
                    LogUtils.i("sunshine-player", "播放完成");
                    mCurrentTimeMs = mCountTimeMs;
                    updateProgress(mCurrentTimeMs);
                    setStatus(STATUS_END);
                    //显示HandleView，不延迟显示
                    setHandleViewVisible(true, false);
                }

            }else {
                LogUtils.i("sunshine-player", "播放错误");
                //有错误,不能继续播放，只能重新播放
                setStatus(STATUS_ERROR);
                setHandleViewVisible(true, false);
            }
        }
    }

    private class VideoErrorListener implements OnErrorListener {

        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            hasError = true;//有错误
            return false;
        }
    }

    private class OnSeekBarTouchListener implements VideoSeekBar.OnSeekBarTouchListener {

        @Override
        public void onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isSeekBarTouch = true;
                    setHandleViewVisible(true, false);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    isSeekBarTouch = false;
                    setHandleViewVisible(true);
                    //触摸结束后，要重新更新一下进度
                    int progress = mSeekBar.getProgress();
                    seekTo(progress);
                    break;
            }
        }
    }

    /**
     * 用来获取视频播放进度的线程
     */
    private class ProgressThread extends Thread {
        private boolean stop = false;
        private Object locked = new Object();

        @Override
        public void run() {
            super.run();
            try {

                synchronized (locked) {
                    while (true) {
                        mCurrentTimeMs = mMediaPlayer.getCurrentPosition();
                        updateProgress(mCurrentTimeMs);
                        sleep(100);

                        if(stop) {
                            locked.wait();
                        }

                    }
                }


            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void restartThread(){

            try {
                if(stop) {
                    synchronized (locked) {
                        this.stop = false;
                        locked.notify();
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void stopThread(){
            try {
                this.stop = true;
            }catch (Exception e) {

            }
        }

        public void destoryThread(){
            try {
                this.stop = true;
                this.interrupt();
            }catch (Exception e) {

            }
        }
    }



    private class VideoPlayerHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if(msg.getData() != null) {
                        Bundle data = msg.getData();
                        String curentTimeStr = data.getString("curentTimeStr");
                        String countTimeStr = data.getString("countTimeStr");
                        int progress = data.getInt("progress");

//                        LogUtils.i("sunshine-player", "progress->" + progress);
                        mTvTime.setText(curentTimeStr + " / " + countTimeStr);

                        if(!isSeekBarTouch) {
                            mProgressBar.setProgress(progress);
                            mSeekBar.setProgress(progress);
                            mProgress = progress;
                            if(onProgressListener != null)onProgressListener.onProgress(VideoPlayer.this,progress, curentTimeStr, countTimeStr);
                        }
                    }
                    break;
                case 1:
                    if(mHandleView != null)mHandleView.setVisibility(View.VISIBLE);
                    if(mProgressBar != null)mProgressBar.setVisibility(View.GONE);
                    removeMessages(2);
                    sendEmptyMessageDelayed(2, 2000);
                    break;
                case 2:
                    if(mHandleView != null)mHandleView.setVisibility(View.GONE);
                    if(isShowProgressBarVisible) {
                        if (mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
                    }
                    break;
                case 3:
                    if(mHandleView != null)mHandleView.setVisibility(View.VISIBLE);
                    if(mProgressBar != null)mProgressBar.setVisibility(View.GONE);
                    break;
            }

        }
    }


    /**
     * 大小窗口切换监听回调接口
     */
    public interface OnSwitchWindowListener {


        /**
         * MODE_WINDOW_NORMAL = 0;//正常播放
         * MODE_WINDOW_FULL_SCREEN = 0;//全屏播放
         */
        void onSwitchWindow(VideoPlayer mVideoPlayer, int mode);
    }

    /**
     * 进度接口回调
     */
    public interface OnProgressListener {

        /**
         *
         * @param mVideoPlayer
         * @param progress 当前进度
         * @param currentTime 当前时间
         * @param countTime 总时间
         */
        void onProgress(VideoPlayer mVideoPlayer, int progress, String currentTime, String countTime);
    }


}