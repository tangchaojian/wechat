package com.aliyun.svideo.recorder.view.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.aliyun.svideo.record.R;
import com.aliyun.svideo.recorder.view.effects.paster.AlivcPasterChooseView;
import com.aliyun.svideo.recorder.view.effects.paster.PasterSelectListener;
import com.aliyun.svideosdk.common.struct.form.PreviewPasterForm;

import java.util.ArrayList;
import java.util.List;

public class GIfEffectChooser extends BasePageChooser {
    private PasterSelectListener pasterSelectListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //适配有底部导航栏的手机，在full的style下会盖住部分视图的bug
        // setStyle(DialogFragment.STYLE_NORMAL, R.style.QUDemoFullFitStyle);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.QUDemoFullStyle);

    }

    @Override
    public List<Fragment> createPagerFragmentList() {
        List<Fragment> fragments = new ArrayList<>();

        AlivcPasterChooseView pasterChooseView = new AlivcPasterChooseView();
        //贴纸
        pasterChooseView.setTabTitle(getResources().getString(R.string.alivc_base_stickers));
        pasterChooseView.setPasterSelectListener(new PasterSelectListener() {
            @Override
            public void onPasterSelected(PreviewPasterForm pasterForm) {
                if (pasterSelectListener != null) {
                    pasterSelectListener.onPasterSelected(pasterForm);
                }
            }

            @Override
            public void onSelectPasterDownloadFinish(String path) {
                if (pasterSelectListener != null) {
                    pasterSelectListener.onSelectPasterDownloadFinish(path);
                }
            }
        });
        fragments.add(pasterChooseView);
        return fragments;
    }

    public void setPasterSelectListener(PasterSelectListener pasterSelectListener) {
        this.pasterSelectListener = pasterSelectListener;
    }
}
