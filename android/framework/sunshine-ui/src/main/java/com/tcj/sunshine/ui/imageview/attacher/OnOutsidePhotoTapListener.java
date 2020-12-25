package com.tcj.sunshine.ui.imageview.attacher;

import android.widget.ImageView;

/**
 * Callback when the user tapped outside of the photo
 */
public interface OnOutsidePhotoTapListener {

    /**
     * The outside of the photo has been tapped
     */
    public void onOutsidePhotoTap(ImageView imageView);
}
