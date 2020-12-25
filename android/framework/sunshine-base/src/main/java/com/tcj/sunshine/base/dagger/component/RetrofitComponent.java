package com.tcj.sunshine.base.dagger.component;


import com.tcj.sunshine.base.dagger.module.RetrofitModule;
import com.tcj.sunshine.net.HttpHelper;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RetrofitModule.class})
public interface RetrofitComponent {
    void inject(HttpHelper client);
}
