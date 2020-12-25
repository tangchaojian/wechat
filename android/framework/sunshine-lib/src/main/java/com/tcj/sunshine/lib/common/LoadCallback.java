package com.tcj.sunshine.lib.common;

import android.graphics.Bitmap;
import android.view.View;

public interface LoadCallback {

    void success(View view, String url, Object resource);

    void fail(String error);
}
