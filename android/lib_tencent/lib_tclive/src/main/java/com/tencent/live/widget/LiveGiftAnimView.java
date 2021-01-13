package com.tencent.live.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.tencent.live.R;

import androidx.annotation.RequiresApi;

public class LiveGiftAnimView extends RelativeLayout {

    private LayoutInflater inflater;

    public LiveGiftAnimView(Context context) {
        super(context);
        this.initUI(context);
    }

    public LiveGiftAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context);
    }

    public LiveGiftAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LiveGiftAnimView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context);
    }

    private void initUI(Context context){
        this.inflater = LayoutInflater.from(context);
        View childView = this.inflater.inflate(R.layout.view_live_gift_item, null);
        this.addView(childView);
    }
}
