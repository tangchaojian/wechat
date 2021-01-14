package com.tencent.live.widget;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcj.sunshine.tools.GlideUtils;
import com.tencent.live.R;
import com.tencent.live.common.msg.TCGiftRewardEntity;

import androidx.annotation.RequiresApi;

/**
 * 礼物打赏动画
 */
public class LiveGiftAnimView extends RelativeLayout {

    private LayoutInflater inflater;
    private ImageView   mIvHeader;
    private TextView    mTvNickName;
    private TextView   mTvGiftName;
    private ImageView   mIvGift;
    private TextView    mTvGiftPrice;
    private TextView    mTvGiftNum;

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

        this.mIvHeader = childView.findViewById(R.id.iv_header);
        this.mTvNickName = childView.findViewById(R.id.tv_nick_name);
        this.mTvGiftName = childView.findViewById(R.id.tv_gift_name);
        this.mIvGift = childView.findViewById(R.id.iv_gift);
        this.mTvGiftPrice = childView.findViewById(R.id.tv_gift_price);
        this.mTvGiftNum = childView.findViewById(R.id.tv_gift_num);

        this.addView(childView);
    }

    public void updateView(TCGiftRewardEntity item){
        if(item == null)return;

        String nickname = item.getSenderName();
        if (TextUtils.isEmpty(nickname)) {
            nickname = item.getUserid();
        }

        this.mTvNickName.setText(nickname);
        this.mTvGiftName.setText(String.format("送%s", item.getGiftName()));
        GlideUtils.loadImage(item.getGiftImgUrl(), mIvGift);
        this.mTvGiftPrice.setText(item.getGiftPrice());
        this.mTvGiftNum.setText(String.format("x%s", item.getGiftNum()));
    }
}
