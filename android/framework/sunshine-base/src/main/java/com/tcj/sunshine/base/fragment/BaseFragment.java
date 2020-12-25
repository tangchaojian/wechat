package com.tcj.sunshine.base.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tcj.sunshine.tools.PermissionUtils;
import com.trello.rxlifecycle2.components.support.RxFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 基础Activity
 */
public abstract class BaseFragment extends RxFragment {
    protected Context context;
    protected Resources res;

    protected View view;
    protected Unbinder mUnbinder;

    public boolean DESTROYED = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.view = inflater.inflate(getLayoutResID(), container, false);

        this.mUnbinder = ButterKnife.bind(this, this.view);

        this.context = this.getContext();
        this.res = this.getResources();
        Bundle arguments = getArguments();
        if (arguments != null) {
            onHandleArgs(arguments);
        }
        this.initUI();
        return this.view;
    }

    protected void onHandleArgs(Bundle arguments) {
    }

    @Override
    public void onStart() {
        super.onStart();
        DESTROYED = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DESTROYED = true;
        this.mUnbinder.unbind();
    }

    //返回ContentView的layoutId
    public abstract int getLayoutResID();


    //初始化控件
    public abstract void initUI();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //权限结果回调
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 弹土司
     *
     * @param content
     */
    public void toast(String content) {

    }

    /**
     * 显示加载窗口
     */
    public void showLoading(String message) {

    }

    /**
     * 关闭加载窗口
     */
    public void hideLoading() {

    }
}