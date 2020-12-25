package com.tcj.sunshine.ui.viewgroup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.StringUtils;
import com.tcj.sunshine.ui.R;

import org.xmlpull.v1.XmlPullParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NewApi")
public class TimeView extends FrameLayout {

    private Context context;
    private LayoutInflater inflater;
    private View mTimeView;
    private boolean showDay = false;

    private TextView mTvDays1;
    private TextView mTvDays2;
    private TextView mTvDays3;

    private TextView mTvHours1;
    private TextView mTvHours2;
    private TextView mTvHours3;

    private TextView mTvMinutes1;
    private TextView mTvMinutes2;
    private TextView mTvMinutes3;

    private TextView mTvSeconds1;
    private TextView mTvSeconds2;

    private Timer timer;

    private long startTime;
    private long endTime;
    private long nowTime;

    public static final int STATUS_NO_START = 0;//还未开始
    public static final int STATUS_START = 1;//开始了
    public static final int STATUS_END = 2;//已经结束

    private int status_none_color = -1;//还没有开始或者结束的字体颜色,如果没有，则没有颜色变化

    private List<TextView> texts = new ArrayList<>();
    private Map<String, Integer> colors = new HashMap<>();

    private int status = STATUS_NO_START;//当前状态

    private OnTimeListener listener;

    public TimeView(Context context) {
        super(context);
        this.init(context, null);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr,
                    int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        try {
            if (attrs != null) {
                TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeView);

                int mTimeViewId = typedArray.getResourceId(R.styleable.TimeView_time_content_view, 0);
                if (mTimeViewId > 0) {
                    XmlPullParser mTimeViewParser = typedArray.getResources().getLayout(mTimeViewId);
                    this.mTimeView = this.inflater.inflate(mTimeViewParser, null);
                }

                int mTimeShowDay = typedArray.getResourceId(R.styleable.TimeView_time_show_day, 0);
                if(mTimeShowDay > 0){
                    this.showDay = typedArray.getResources().getBoolean(mTimeShowDay);
                }else{
                    this.showDay = typedArray.getBoolean(R.styleable.TimeView_time_show_day, false);
                }

                int mTimeStatusNoneColor = typedArray.getResourceId(R.styleable.TimeView_time_status_none_color, 0);
                if(mTimeStatusNoneColor > 0){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        this.status_none_color =  ContextCompat.getColor(context, mTimeStatusNoneColor);
                    } else {
                        this.status_none_color =  typedArray.getResources().getColor(mTimeStatusNoneColor);
                    }

                    this.status_none_color = typedArray.getResources().getColor(mTimeStatusNoneColor);
                }else{
                    this.status_none_color = typedArray.getColor(R.styleable.TimeView_time_status_none_color, -1);
                }

                typedArray.recycle();
            }

            if (this.mTimeView == null) {
                this.mTimeView = this.inflater.inflate(R.layout.view_default_time, null);
            }

            this.addView(this.mTimeView);

            this.mTvDays1 = (TextView) this.mTimeView.findViewById(R.id.mTvDays1);
            this.mTvDays2 = (TextView) this.mTimeView.findViewById(R.id.mTvDays2);
            this.mTvDays3 = (TextView) this.mTimeView.findViewById(R.id.mTvDays3);

            this.mTvHours1 = (TextView) this.mTimeView.findViewById(R.id.mTvHours1);
            this.mTvHours2 = (TextView) this.mTimeView.findViewById(R.id.mTvHours2);
            this.mTvHours3 = (TextView) this.mTimeView.findViewById(R.id.mTvHours3);

            this.mTvMinutes1 = (TextView) this.mTimeView.findViewById(R.id.mTvMinutes1);
            this.mTvMinutes2 = (TextView) this.mTimeView.findViewById(R.id.mTvMinutes2);
            this.mTvMinutes3 = (TextView) this.mTimeView.findViewById(R.id.mTvMinutes3);

            this.mTvSeconds1 = (TextView) this.mTimeView.findViewById(R.id.mTvSeconds1);
            this.mTvSeconds2 = (TextView) this.mTimeView.findViewById(R.id.mTvSeconds2);

            this.texts.add(mTvDays1);
            this.texts.add(mTvDays2);
            this.texts.add(mTvDays3);
            this.texts.add(mTvHours1);
            this.texts.add(mTvHours2);
            this.texts.add(mTvHours3);
            this.texts.add(mTvMinutes1);
            this.texts.add(mTvMinutes2);
            this.texts.add(mTvMinutes3);
            this.texts.add(mTvSeconds1);
            this.texts.add(mTvSeconds2);


            this.colors.put("" + mTvDays1.hashCode(), mTvDays1.getCurrentTextColor());
            this.colors.put("" + mTvDays2.hashCode(), mTvDays2.getCurrentTextColor());
            this.colors.put("" + mTvDays3.hashCode(), mTvDays3.getCurrentTextColor());
            this.colors.put("" + mTvHours1.hashCode(), mTvHours1.getCurrentTextColor());
            this.colors.put("" + mTvHours2.hashCode(), mTvHours2.getCurrentTextColor());
            this.colors.put("" + mTvHours3.hashCode(), mTvHours3.getCurrentTextColor());
            this.colors.put("" + mTvMinutes1.hashCode(), mTvMinutes1.getCurrentTextColor());
            this.colors.put("" + mTvMinutes2.hashCode(), mTvMinutes2.getCurrentTextColor());
            this.colors.put("" + mTvMinutes3.hashCode(), mTvMinutes3.getCurrentTextColor());
            this.colors.put("" + mTvSeconds1.hashCode(), mTvSeconds1.getCurrentTextColor());
            this.colors.put("" + mTvSeconds2.hashCode(), mTvSeconds2.getCurrentTextColor());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param format 时间格式
     */
    public void setTime(String startTime, String endTime, String nowTime, String format) {
        try {
            SimpleDateFormat mDateFormat = new SimpleDateFormat(format);
            Date mStartDate = mDateFormat.parse(startTime);
            Date mEndDate = mDateFormat.parse(endTime);
            Date mNowTime = mDateFormat.parse(nowTime);
            this.setTime(mStartDate.getTime(), mEndDate.getTime(), mNowTime.getTime());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public void setTime(long startTime, long endTime, long nowTime) {
        try {
            this.startTime = startTime;
            this.endTime = endTime;
            this.nowTime = nowTime;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void start(){
        try {

            long target = 0;
            if(nowTime < startTime){
                target = startTime;
                this.status = STATUS_NO_START;//还未开始

                if(status_none_color != -1){
                    this.setTextColor(this.status_none_color);
                }

            }else if(nowTime >= startTime && nowTime < endTime){
                target = endTime;
                this.status = STATUS_START;//开始了,但还未结束
                if(status_none_color != -1){
                    this.setTextColor(-1);
                }
            }else{
                target = 0;
                this.timer.purge();
                this.timer.cancel();
                this.timer = new Timer();
                this.status = STATUS_END;//结束了

                if(status_none_color != -1){
                    this.setTextColor(this.status_none_color);
                }
            }

            if (target != 0) {
                if (this.timer == null) {
                    this.timer = new Timer();
                } else {
                    this.timer.purge();
                    this.timer.cancel();
                    this.timer = new Timer();
                }
                timer.schedule(new MTimeTask(nowTime, target), 0, 1000);
            }

            if(listener != null){
                listener.onTimeChange(this.status);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopTime() {
        if (this.timer != null) {
            this.timer.purge();
            this.timer.cancel();
            this.timer = null;
        }
    }

    public void setTextColor(int color){
        for (TextView mTextView : texts){
            if(color == -1){
                mTextView.setTextColor(colors.get("" + mTextView.hashCode()));
            }else{
                mTextView.setTextColor(color);
            }
        }
    }


    public int getStatus() {
        return status;
    }

    public void setOnTimeListener(OnTimeListener listener) {
        this.listener = listener;
    }

    private class MTimeTask extends TimerTask {

        private long current;
        private long nowTime = 0;
        private long target = 0;


        public MTimeTask(long nowTime, long target) {
            this.target = target;
            this.nowTime = nowTime;
            this.current = nowTime;
        }

        @Override
        public void run() {
            mTimeView.post(new Runnable() {

                @Override
                public void run() {
                    long end = target;
                    long value = end - current;
                    if (value > 0) {
                        long mToDay = 24 * 60 * 60 * 1000;
                        long mToHours = 60 * 60 * 1000;
                        long mToMinutes = 60 * 1000;
                        long mToSeconds = 1000;

                        int day = (int) Math.floor(value / mToDay);
                        value = (long) (value - mToDay * day);
                        int h = (int) Math.floor(value / mToHours);
                        value = (long) (value - mToHours * h);
                        int m = (int) Math.floor(value / mToMinutes);
                        value = (long) (value - mToMinutes * m);
                        int s = (int) Math.round(value / mToSeconds);

                        String mDays = StringUtils.padded(2, day);
                        String mHours = StringUtils.padded(2, h);
                        String mMinutes = StringUtils.padded(2, m);
                        String mSeconds = StringUtils.padded(2, s);

                        if(day > 0){
                            mTvDays1.setText(mDays.substring(0,1));
                            mTvDays2.setText(mDays.substring(1,2));

                            mTvDays1.setVisibility(View.VISIBLE);
                            mTvDays2.setVisibility(View.VISIBLE);
                            mTvDays3.setVisibility(View.VISIBLE);
                        }else{
                            mTvDays1.setVisibility(View.GONE);
                            mTvDays2.setVisibility(View.GONE);
                            mTvDays3.setVisibility(View.GONE);
                        }

                        mTvHours1.setText(mHours.substring(0,1));
                        mTvHours2.setText(mHours.substring(1,2));
                        mTvMinutes1.setText(mMinutes.substring(0,1));
                        mTvMinutes2.setText(mMinutes.substring(1,2));
                        mTvSeconds1.setText(mSeconds.substring(0,1));
                        mTvSeconds2.setText(mSeconds.substring(1,2));
                        current += 1000;
                    } else {

                        if(target == startTime){
                            //计时完成，下一个状态,活动开始
                            nowTime = startTime;
                        }else if(target == endTime){
                            nowTime = endTime;
                        }

                        setTime(startTime, endTime, nowTime);
                        start();
                    }
                }
            });
        }
    }



    public interface OnTimeListener {

        /**
         * @param status
         * STATUS_NO_START = 0;//还未开始
         * STATUS_START = 1;//开始了
         * STATUS_END = 2;//已经结束
         */
        public void onTimeChange(int status);
    }
}
