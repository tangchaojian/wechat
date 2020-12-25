package com.tcj.sunshine.net;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.tcj.sunshine.lib.BuildConfig;
import com.tcj.sunshine.lib.config.Constants;
import com.tcj.sunshine.tools.ContextUtils;
import com.tcj.sunshine.tools.FileUtils;
import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.NetUtils;
import com.tcj.sunshine.tools.PreferenceUtils;
import com.tcj.sunshine.tools.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Okhttp的一些配置信息
 */
public class RetrofitConfig {

    /**
     * 网络连接超时,单位秒
     */
    public static final int HTTP_CONNECT_TIMEOUT = Constants.HTTP_CONNECT_TIMEOUT;

    /**
     * 网络读超时,单位秒
     */
    public static final int HTTP_READ_TIMEOUT = Constants.HTTP_READ_TIMEOUT;

    /**
     * 网络写超时,单位秒
     */
    public static final int HTTP_WRITE_TIMEOUT = Constants.HTTP_WRITE_TIMEOUT;

    /**
     * 缓存目录
     */
    public static final Cache HTTP_CACHE = new Cache(FileUtils.getCacheDir(FileUtils.DIR_NAME_HTTP_CACHE), Constants.HTTP_MAX_CACHE);

    /**
     * 请求响应日志拦截器
     */
    public static Interceptor HTTP_LOG_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            try {
                final Charset UTF8 = Charset.forName("UTF-8");

                Request request = chain.request();
                RequestBody requestBody = request.body();

                String body = null;

                if(requestBody != null) {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);

                    Charset charset = UTF8;
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(UTF8);
                    }
                    body = buffer.readString(charset);
                }

                if(BuildConfig.IS_SHOW_LOG) {
                    LogUtils.d("SUNSHINE-NET", "=====================HTTP 请求 START =========================");
                    LogUtils.d("SUNSHINE-NET", "method:" + request.method());
                    LogUtils.d("SUNSHINE-NET", "url:" + request.url());
                    LogUtils.d("SUNSHINE-NET", "headers:" + request.headers());
                }

                boolean isNeedLog = true;
                String url = request.url().toString();
                String suffix = !TextUtils.isEmpty(url) ? StringUtils.getSuffix(request.url().toString()) : "";
                if("jpg".equals(suffix) || "jpeg".equals(suffix) || "png".equals(suffix) || "apk".equals(suffix) || "txt".equals(suffix)){
                    isNeedLog = false;
                }else {
                    isNeedLog = true;
                    LogUtils.d("SUNSHINE-NET", "body:" + body);
                }

                if(BuildConfig.IS_SHOW_LOG) {
                    LogUtils.d("SUNSHINE-NET", "=====================HTTP 请求 END============================");
                }

                Response response = null;
                if(isNeedLog) {
                    long startNs = System.nanoTime();
                    response = chain.proceed(request);
                    long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

                    ResponseBody responseBody = response.body();
                    String content = null;

                    if (responseBody != null) {
                        BufferedSource source = responseBody.source();
                        source.request(Long.MAX_VALUE); // Buffer the entire body.
                        Buffer buffer = source.buffer();

                        Charset charset = UTF8;
                        MediaType contentType = responseBody.contentType();
                        if (contentType != null) {
                            try {
                                charset = contentType.charset(UTF8);
                            } catch (UnsupportedCharsetException e) {
                                e.printStackTrace();
                            }
                        }
                        content = buffer.clone().readString(charset);

                        if(BuildConfig.IS_SHOW_LOG) {
                            LogUtils.d("SUNSHINE-NET", "=====================HTTP 响应 START =========================");
                            LogUtils.d("SUNSHINE-NET", "method:" + response.request().method());
                            LogUtils.d("SUNSHINE-NET", "url:" + response.request().url());
                            LogUtils.d("SUNSHINE-NET", "HTTP响应码:" + response.code());
                            LogUtils.d("SUNSHINE-NET", "HTTP响应信息:" + response.message());
                            LogUtils.d("SUNSHINE-NET", "HTTP响应时长(单位:毫秒):" + tookMs);
                            LogUtils.d("SUNSHINE-NET", "HTTP响应内容:" + content);

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ContextUtils.getContext());
                            String accessToken = preferences.getString("pref.accessToken", "");
                            LogUtils.d("SUNSHINE-NET", "ACCESS_TOKEN:" + accessToken);

                            LogUtils.d("SUNSHINE-NET", "=====================HTTP 响应 END============================");
                        }
                    }

                }

                if(response == null) {
                    response = chain.proceed(request);
                }

                if(response.code() == 401) {
                    EventBus.getDefault().post(new MYSystemEvent("reLogin"));//重新登录
                }

                return response;
            }catch (Exception e) {
                LogUtils.e("SUNSHINE-NET", "HTTP_LOG_INTERCEPTOR 异常:" + e.getMessage());
                return chain.proceed(chain.request());
            }
        }
    };

    /**
     * 头部拦截器
     */
    public static Interceptor HTTP_HEADER_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            try {

                Request original = chain.request();//原来的请求
                Request.Builder builder = original.newBuilder()
//                    .addHeader("Accept-Encoding", "gzip")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("Accept", "application/json;charset=UTF-8");
                Request request = builder.build();//现在组装后的新请求
                return chain.proceed(request);
            }catch (Exception e) {
                LogUtils.e("sunshine-exception", "HTTP_HEADER_INTERCEPTOR 异常:" + e.getMessage());
                e.printStackTrace();
                return chain.proceed(chain.request());
            }
        }
    };

    public static Interceptor HTTP_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            try {
                Request request = chain.request();

                if(!NetUtils.isConnected(ContextUtils.getContext())) {
                    request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                }

                Response original = chain.proceed(request);
                if(NetUtils.isConnected(ContextUtils.getContext())){
                    //有网的情况
                    String cacheControl = request.cacheControl().toString();

                    Response.Builder builder = original.newBuilder();
                    if(TextUtils.isEmpty(cacheControl)) {
                        //如果没有设置Cache-Control,则添加3s之内不重复请求的Cache-Control
                        builder.header("Cache-Control", Constants.HTTP_CACHE_CONTROL);
                    }else {
                        builder.header("Cache-Control", cacheControl);
                    }
                    builder.removeHeader("Pragma");
                    builder.removeHeader("User-Agent"); //移除旧的User-Agent
                    builder.addHeader("User-Agent", Constants.HTTP_USER_AGENT);
                    builder.build();
                }else {
                    original.newBuilder()
                            .header("Cache-Control", Constants.HTTP_NO_NET_CACHE_CONTROL)
                            .removeHeader("Pragma")
                            .build();
                }
                return original;
            }catch (Exception e) {
                LogUtils.e("sunshine-exception", "HTTP_CACHE_CONTROL_INTERCEPTOR 异常:" + e.getMessage());
                e.printStackTrace();
                return chain.proceed(chain.request());
            }
        }
    };

}
