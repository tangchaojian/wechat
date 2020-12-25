package com.tcj.sunshine.base.mvp;

import com.trello.rxlifecycle2.LifecycleTransformer;

import okhttp3.ResponseBody;

/**
 * 基础Presenter
 */
public class BasePresenter <V extends BaseView>{

    public LifecycleTransformer<ResponseBody> composer;

    public V mContractView;

    public void attchView(V mContractView, LifecycleTransformer<ResponseBody> composer) {
        this.mContractView = mContractView;
        this.composer = composer;
    }
}