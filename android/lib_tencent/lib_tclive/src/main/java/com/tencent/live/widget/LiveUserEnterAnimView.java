package com.tencent.live.widget;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.live.R;
import com.tencent.live.common.msg.TCChatEntity;
import com.tencent.live.common.msg.TCGiftRewardEntity;
import com.tencent.live.common.msg.TCUserEnterEntity;

import androidx.annotation.RequiresApi;


/**
 * 用户进入动画
 */
public class LiveUserEnterAnimView extends RelativeLayout {

    private LayoutInflater inflater;
    private TextView    mTvNickName;

    public LiveUserEnterAnimView(Context context) {
        super(context);
        this.initUI(context);
    }

    public LiveUserEnterAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context);
    }

    public LiveUserEnterAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LiveUserEnterAnimView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context);
    }

    private void initUI(Context context){
        this.inflater = LayoutInflater.from(context);
        View childView = this.inflater.inflate(R.layout.view_live_user_enter_item, null);

        this.mTvNickName = childView.findViewById(R.id.tv_nick_name);

        this.addView(childView);
    }

    public void updateView(TCUserEnterEntity item){
        if(item == null)return;

        String nickname = item.getSenderName();
        if(TextUtils.isEmpty(nickname)) {
            nickname = item.getMobile();
        }

        if(TextUtils.isEmpty(nickname)) {
            nickname = item.getUserid();
        }

        this.mTvNickName.setText(nickname);
    }
}
