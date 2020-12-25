package com.tcj.sunshine.view.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.tcj.sunshine.boxing.loader.IBoxingCrop;
import com.tcj.sunshine.boxing.model.BoxingManager;
import com.tcj.sunshine.boxing.model.config.BoxingCropOption;
import com.tcj.sunshine.crop.Crop;
import com.tcj.sunshine.tools.FileUtils;
import com.tcj.sunshine.tools.ToastUtils;
import com.tcj.sunshine.tools.UriUtils;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class BoxingCropImpl implements IBoxingCrop {
    @Override
    public void onStartCrop(Context context, Fragment fragment, @NonNull BoxingCropOption cropConfig, @NonNull String path, int requestCode) {

        Activity activity = null;
        if(context instanceof Activity) {
            activity = (Activity)context;
        }else if(fragment != null && fragment.getActivity() != null) {
            activity = fragment.getActivity();
        }


        if(activity != null) {
            Uri sourceURI = UriUtils.fileToUri(new File(path));
            Uri targetURI = cropConfig.getDestination();
            int aspectX = (int)cropConfig.getAspectRatioX();
            int aspectY = (int)cropConfig.getAspectRatioY();
            int outputX = cropConfig.getMaxWidth();
            int outputY = cropConfig.getMaxHeight();

            Crop crop = new Crop.Builder(activity)
                    .setSourceURI(sourceURI)
                    .setTargetURI(targetURI)
                    .setOutputMaxSize(outputX, outputY)
                    .addAspectRatio(aspectX,aspectY)
                    .setMode(Crop.MODE_ALL)
                    .setOutputFormat(Crop.FORMAT.JPEG)
                    .build();
            crop.start(requestCode);
        }
    }

    @Override
    public Uri onCropFinish(int resultCode, Intent data) {
        if( data == null) {
            return null;
        }
        if(resultCode == Activity.RESULT_OK){
//            BoxingCropOption cropConfig = BoxingManager.getInstance().getBoxingConfig().getCropOption();
//            Uri saveFileURI = cropConfig.getDestination();//保存的路径
            return data.getParcelableExtra(Crop.CROP_TARGET_URI);
        }
        return null;
    }

}
