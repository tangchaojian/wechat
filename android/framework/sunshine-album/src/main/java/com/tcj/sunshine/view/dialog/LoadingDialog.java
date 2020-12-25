package com.tcj.sunshine.view.dialog;

import android.content.Context;
import android.view.Gravity;

import com.tcj.sunshine.album.R;
import com.tcj.sunshine.ui.dialog.UIDialog;

/**
 * Created by Stefan Lau on 2019/11/26.
 */
public class LoadingDialog {

    private UIDialog dialog;

    public LoadingDialog(Context context) {
        this.dialog = new UIDialog.Builder(context)
                .setContentViewId(R.layout.dialog_sunshine_loading)
                .setGravity(Gravity.CENTER)
                .build();

    }

    public void show(){
        if(this.dialog != null)this.dialog.show();
    }

    public void cancel(){
        if(this.dialog != null)this.dialog.cancel();
    }
}
