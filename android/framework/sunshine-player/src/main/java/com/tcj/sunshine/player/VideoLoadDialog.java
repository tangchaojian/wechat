package com.tcj.sunshine.player;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.ui.dialog.UIDialog;

public class VideoLoadDialog {

    private Context context;
    private UIDialog dialog;
    private ImageView mIvAnim;
    private TextView mTvMessage;

    public VideoLoadDialog(Context context) {
        this.context = context;
        this.initUI();
    }

    private void initUI(){
        this.dialog = new UIDialog.Builder(context)
                .setContentViewId(R.layout.view_video_player_load)
                .setWidth(ScreenUtils.getScreenWidth())
                .setHeight(ScreenUtils.dip2px(70f))
                .setGravity(Gravity.TOP)
                .setStyleId(R.style.ThemeStyleUIDailog_Transparent)
                .build();

        this.mIvAnim = dialog.contentView.findViewById(R.id.mIvAnim);
        this.mTvMessage = dialog.contentView.findViewById(R.id.mTvMessage);
    }

    public void setMessage(String text) {
        if(this.mTvMessage != null) {
            this.mTvMessage.setText(text);
        }
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener cancelListener){
        dialog.setOnCancelListener(cancelListener);
    }

    public boolean isShowing(){
        if(dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

    public void show(){
        if(dialog != null && !dialog.isShowing()){

            if(this.mIvAnim != null) {
                AnimationDrawable animationDrawable = (AnimationDrawable) this.mIvAnim.getDrawable();
                animationDrawable.start();
            }

            dialog.setGravity(Gravity.CENTER);
            dialog.show();
        }
    }

    public void cancel(){
        if(dialog != null && dialog.isShowing()){

            if(this.mIvAnim != null) {
                AnimationDrawable animationDrawable = (AnimationDrawable) this.mIvAnim.getDrawable();
                animationDrawable.start();
            }

            dialog.dismiss();
        }
    }
}
