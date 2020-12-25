package com.tcj.sunshine.player;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.tcj.sunshine.tools.DateUtils;
import com.tcj.sunshine.tools.DrawableUtils;
import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.ScreenUtils;

import java.util.Map;

/**
 * 仿抖音视频播放器
 */
public class DouYinVideoPlayer extends FrameLayout implements BaseVideoPlayer {

    private DouYinPlayer player;//播放器

    private View mDouYinTimeLayout;//拖拽修改播放进度view
    private TextView mTvCurrentTime;//当前播放时间
    private TextView mTvCountTime;//总时间
    private VideoSeekBar mDouYinSeekBar;//拖拽进度view

    private DouYinHandler handler = new DouYinHandler();

    private String countTime = "";

    private boolean isUpdateSeekBar = true;

    public DouYinVideoPlayer(@NonNull Context context) {
        super(context);
        this.initUI(context);
    }

    public DouYinVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context);
    }

    public DouYinVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DouYinVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context);
    }

    private void initUI(Context context) {

        //实例化一个播放器
        this.player = new DouYinPlayer(context);

        //计算宽高
        int width = ScreenUtils.getScreenWidth();
        int height = width * 16 / 9;
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(width, height);
        params1.gravity = Gravity.TOP;
        //添加播放器
        this.addView(this.player, params1);

        LayoutInflater inflater = LayoutInflater.from(context);

        inflater.inflate(R.layout.view_video_player_douyin_seekbar, this, true);
        this.mDouYinTimeLayout = this.findViewById(R.id.mDouYinTimeLayout);//时间Layout
        this.mTvCurrentTime = this.findViewById(R.id.mTvCurrentTime);//当前播放时间
        this.mTvCountTime = this.findViewById(R.id.mTvCountTime);//总时间
        this.mDouYinSeekBar = this.findViewById(R.id.mDouYinSeekBar);//拖拽进度view
        this.mDouYinSeekBar.setProgress(0);
        this.mDouYinSeekBar.setMax(player.getMaxProgress());

        mDouYinSeekBar.setThumb(DrawableUtils.getDrawable(R.drawable.transparent));

        FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams)mDouYinSeekBar.getLayoutParams();
        params2.bottomMargin = ScreenUtils.dip2px((14 - 2) / 2) * -1;
        mDouYinSeekBar.requestLayout();

        this.mDouYinTimeLayout.setVisibility(View.GONE);

        this.setOnProgressListener(new VideoPlayer.OnProgressListener() {
            @Override
            public void onProgress(VideoPlayer mVideoPlayer, int progress, String currentTime, String countTime) {
//                LogUtils.i("sunshine-ui", "进度->" + progress);
                if(isUpdateSeekBar) {
                    updateProgress(progress, currentTime, countTime);
                }
            }
        });

        this.mDouYinSeekBar.setOnSeekBarTouchListener(new VideoSeekBar.OnSeekBarTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                LogUtils.i("sunshine-ui", "mDouYinSeekBar->onTouchEvent");
                FrameLayout.LayoutParams params;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDouYinSeekBar.setThumb(DrawableUtils.getDrawable(R.drawable.shape_video_player_douyin_seekbar_circle_thumb));
                        params = (FrameLayout.LayoutParams)mDouYinSeekBar.getLayoutParams();
                        params.bottomMargin = 0;
                        mDouYinSeekBar.requestLayout();
                        mDouYinTimeLayout.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isUpdateSeekBar = false;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                        isUpdateSeekBar = true;
                        mDouYinTimeLayout.setVisibility(View.GONE);
                        mDouYinSeekBar.setThumb(DrawableUtils.getDrawable(R.drawable.transparent));
                        params = (FrameLayout.LayoutParams)mDouYinSeekBar.getLayoutParams();
                        params.bottomMargin = ScreenUtils.dip2px((14 - 2) / 2) * -1;
                        mDouYinSeekBar.requestLayout();

                        int progress = mDouYinSeekBar.getProgress();
                        seekTo(progress);
                        break;
                }
            }
        });

        this.mDouYinSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float percent = progress * 1.0f / 100;
                long mCurrentTimeMs = (long)(percent * getDuration());
                String currentTime = DateUtils.formatTime(mCurrentTimeMs);

                updateProgress(progress, currentTime, countTime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void setPath(String path) {
        this.player.setPath(path);
    }

    @Override
    public void setPath(String path, Map<String, String> header) {
        this.player.setPath(path, header);
    }

    @Override
    public String getPath() {
        return this.player.getPath();
    }

    @Override
    public void start() {
        this.player.start();
    }

    @Override
    public void play() {
        this.player.play();
    }

    @Override
    public void restart() {
        this.player.restart();
    }

    @Override
    public void release() {
        this.player.release();
    }

    @Override
    public void pause() {
        this.player.pause();
    }

    @Override
    public void stop() {
        this.player.stop();
    }

    @Override
    public void reset() {
        this.player.reset();
    }

    @Override
    public void setLooping(boolean isLooping) {
        this.player.setLooping(isLooping);
    }

    @Override
    public void setAutoPlay(boolean isAutoPlay) {
        this.player.setAutoPlay(isAutoPlay);
    }

    @Override
    public boolean isPrepared() {
        return this.player.isPrepared();
    }

    @Override
    public void seekTo(int progress) {
        this.player.seekTo(progress);
    }

    @Override
    public long getDuration() {
        return this.player.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        return this.player.getCurrentPosition();
    }

    @Override
    public boolean isPlaying() {
        return this.player.isPlaying();
    }

    @Override
    public void setFullScreen(boolean isFull) {
        this.player.setFullScreen(isFull);
    }


    @Override
    public int getProgress() {
        return this.player.getProgress();
    }

    @Override
    public int getMaxProgress() {
        return this.player.getMaxProgress();
    }

    @Override
    public void setOnSwitchWindowListener(VideoPlayer.OnSwitchWindowListener mListener) {
        this.player.setOnSwitchWindowListener(mListener);
    }

    @Override
    public void setPlayIcon(int resId) {
        this.player.setPlayIcon(resId);
    }

    @Override
    public void setPauseIcon(int resId) {
        this.player.setPauseIcon(resId);
    }

    @Override
    public void setRePlayIcon(int resId) {
        this.player.setRePlayIcon(resId);
    }

    @Override
    public void setHandleIconSize(int width, int height) {
        this.player.setHandleIconSize(width, height);
    }

    @Override
    public void setFullScreenIconVisible(boolean isVisible) {
        this.player.setFullScreenIconVisible(isVisible);
    }

    @Override
    public void setOnProgressListener(VideoPlayer.OnProgressListener mListener) {
        this.player.setOnProgressListener(mListener);
    }

    public void setProgressBarVisible(boolean isVisible) {
        if(this.mDouYinSeekBar != null) {
            if(isVisible) {
                this.mDouYinSeekBar.postDelayed(()->this.mDouYinSeekBar.setVisibility(View.VISIBLE), 500);
            }else {
                this.mDouYinSeekBar.setVisibility(View.GONE);
            }

        }
    }

    /**
     * 更新进度
     * @param progress
     * @param currentTime
     * @param countTime
     */
    private void updateProgress(int progress, String currentTime, String countTime) {
        this.countTime = countTime;
        mTvCurrentTime.setText(currentTime);
        mTvCountTime.setText(countTime);
        mDouYinSeekBar.setProgress(progress);
    }

    private class DouYinPlayer extends VideoPlayer {

        public DouYinPlayer(@NonNull Context context) {
            super(context);
            this.initUI();
        }

        public DouYinPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            this.initUI();
        }

        public DouYinPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            this.initUI();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public DouYinPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            this.initUI();
        }

        private void initUI() {
            this.setTouchEnable(false);//不触发触摸事件
            this.setLooping(true);//循环播放
            this.setProgressBarVisible(false);//隐藏进度条
        }
    }

    private class DouYinHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    break;

            }
        }
    }


}
