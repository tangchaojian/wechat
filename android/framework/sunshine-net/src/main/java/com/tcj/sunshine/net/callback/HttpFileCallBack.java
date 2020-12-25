package com.tcj.sunshine.net.callback;

import java.io.File;

/**
 * Created by Stefan Lau on 2019/11/11.
 */
public class HttpFileCallBack extends HttpCallBack<File>{

    private File targetFile;

    public HttpFileCallBack(File targetFile) {
        this.targetFile = targetFile;
    }

    public File getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }

    public void onProgress(int max, int progress, long maxLength, long length) {

    }


    @Override
    public void success(String code, String msg, File file) {

    }

    @Override
    public void error(String code, String msg) {

    }

    @Override
    public void complete(String code, String msg, File file) {

    }
}