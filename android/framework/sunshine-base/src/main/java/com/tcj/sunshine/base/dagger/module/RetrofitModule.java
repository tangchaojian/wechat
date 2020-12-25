package com.tcj.sunshine.base.dagger.module;


import com.tcj.sunshine.lib.config.Constants;
import com.tcj.sunshine.net.ApiService;
import com.tcj.sunshine.net.RetrofitConfig;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RetrofitModule {

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .cache(RetrofitConfig.HTTP_CACHE)
                .retryOnConnectionFailure(true)
                .addInterceptor(RetrofitConfig.HTTP_LOG_INTERCEPTOR)
                .addInterceptor(RetrofitConfig.HTTP_HEADER_INTERCEPTOR)
                .addInterceptor(RetrofitConfig.HTTP_CACHE_CONTROL_INTERCEPTOR)
                .addNetworkInterceptor(RetrofitConfig.HTTP_CACHE_CONTROL_INTERCEPTOR)
                .connectTimeout(RetrofitConfig.HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(RetrofitConfig.HTTP_READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(RetrofitConfig.HTTP_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
        return httpClient;
    }

    @Singleton
    @Provides
    ApiService provideApiService(OkHttpClient httpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .baseUrl(Constants.HTTP_BASE_URL)
                .build();

        return retrofit.create(ApiService.class);
    }


}
