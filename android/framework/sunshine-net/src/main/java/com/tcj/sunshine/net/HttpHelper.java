package com.tcj.sunshine.net;

import android.os.Handler;
import android.util.Log;

import com.tcj.sunshine.net.callback.HttpCallBack;
import com.tcj.sunshine.net.callback.HttpEmptyCallBack;
import com.tcj.sunshine.net.callback.HttpFileCallBack;
import com.tcj.sunshine.net.entity.FilePartEntity;
import com.tcj.sunshine.net.entity.ResponseEntity;
import com.tcj.sunshine.tools.FileUtils;
import com.tcj.sunshine.tools.GsonUtils;
import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.StringUtils;
import com.trello.rxlifecycle2.LifecycleTransformer;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Stefan Lau on 2019/11/9.
 */
public class HttpHelper {

    private static HttpHelper INSTANCE;

    @Inject
    ApiService service;

    private HttpHelper() {
    }

    public static HttpHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpHelper();
        }
        return INSTANCE;
    }

    public void test() {
        LogUtils.i("sunshine-net", "SERVICE 是否依赖注入成功[" + (service != null) + "]");
    }

    /**
     * GET请求
     *
     * @param url
     */
    public static void get(String url) {
        if (INSTANCE != null && INSTANCE.service != null) {

            Observable<ResponseBody> observable = INSTANCE.service.get(url);
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseObserver());
        }
    }

    /**
     * GET请求
     *
     * @param url
     * @param params
     * @param callback
     * @param <T>
     */
    public static <T> void get(String url, Map<String, String> params, HttpCallBack<T> callback) {
        get(url, params, null, callback);
    }

    /**
     * GET请求(建议在Activity和Fragment里面用这个get方法,但Activity必须继承BaseActivity，Fragment必须继承BaseFragment)
     *
     * @param url
     * @param params
     * @param composer
     * @param callback
     * @param <T>
     */
    public static <T> void get(String url, Map<String, String> params, LifecycleTransformer<ResponseBody> composer, HttpCallBack<T> callback) {
        if (INSTANCE != null && INSTANCE.service != null) {

            if (params == null) {
                params = new HashMap<>();
            }

            Observable<ResponseBody> observable = INSTANCE.service.get(url, params);
            if (composer != null) {
                observable.compose(composer);
            }
            //响应观察者
            ResponseObserver<T> observer = new ResponseObserver<>(callback);

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        }
    }

    /**
     * GET请求(建议在Activity和Fragment里面用这个get方法,但Activity必须继承BaseActivity，Fragment必须继承BaseFragment)
     *
     * @param url
     * @param composer
     * @param callback
     * @param <T>
     */
    public static <T> void get(String url, LifecycleTransformer<ResponseBody> composer, HttpCallBack<T> callback) {
        if (INSTANCE != null && INSTANCE.service != null) {
            Map<String, String> params = new HashMap<>();
            Observable<ResponseBody> observable = INSTANCE.service.get(url, params);
            if (composer != null) {
                observable.compose(composer);
            }
            //响应观察者
            ResponseObserver<T> observer = new ResponseObserver<>(callback);

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        }
    }


    /**
     * post 请求
     *
     * @param url
     * @param <T>
     */
    public static <T> void post(String url) {
        post(url, null, null);
    }


    /**
     * post 请求
     *
     * @param url
     * @param callback
     * @param <T>
     */
    public static <T> void post(String url, HttpCallBack<T> callback) {
        post(url, null, callback);
    }


    /**
     * POST表单提交
     *
     * @param url
     * @param params
     * @param callback
     * @param <T>
     */
    public static <T> void post(String url, Map<String, String> params, HttpCallBack<T> callback) {
        post(url, params, null, callback);
    }


    /**
     * POST表单提交(建议在Activity和Fragment里面用这个post方法,但Activity必须继承BaseActivity，Fragment必须继承BaseFragment)
     *
     * @param url
     * @param params
     * @param composer
     * @param callback
     * @param <T>
     */
    public static <T> void post(String url, Map<String, String> params, LifecycleTransformer<ResponseBody> composer, HttpCallBack<T> callback) {
        if (INSTANCE != null && INSTANCE.service != null) {

            if (params == null) {
                params = new HashMap<>();
            }

            Observable<ResponseBody> observable = INSTANCE.service.post(url, toHttpParams(params));
            if (composer != null) {
                observable.compose(composer);
            }
            //响应观察者
            ResponseObserver<T> observer = new ResponseObserver<>(callback);

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        }
    }

    /**
     * post表单,针对含有@Query和@Field的api
     *
     * @param url
     * @param querys
     * @param fields
     * @param composer
     * @param callback
     * @param <T>
     */
    public static <T> void post(String url, Map<String, String> querys, Map<String, String> fields, LifecycleTransformer<ResponseBody> composer, HttpCallBack<T> callback) {
        if (INSTANCE != null && INSTANCE.service != null) {
            if (querys == null) {
                querys = new HashMap<>();
            }
            if (fields == null) {
                fields = new HashMap<>();
            }
            Observable<ResponseBody> observable = INSTANCE.service.post(url, querys, fields);
            if (composer != null) {
                observable.compose(composer);
            }
            //响应观察者
            ResponseObserver<T> observer = new ResponseObserver<>(callback);
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        }
    }


    /**
     * PUT
     *
     * @param url
     * @param params
     * @param composer
     * @param callback
     * @param <T>
     */
    public static <T> void put(String url, Map<String, String> params, LifecycleTransformer<ResponseBody> composer, HttpCallBack<T> callback) {
        if (INSTANCE != null && INSTANCE.service != null) {

            Observable<ResponseBody> observable = INSTANCE.service.put(url, params);
            if (composer != null) {
                observable.compose(composer);
            }
            //响应观察者
            ResponseObserver<T> observer = new ResponseObserver<>(callback);

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        }
    }


    /**
     * POST请求（格式json）
     *
     * @param url
     * @param params
     * @param callback
     * @param <T>
     */
    public static <T> void postJson(String url, Map<String, Object> params, HttpCallBack<T> callback) {
        postJson(url, params, null, callback);
    }


    /**
     * POST请求（格式json, 建议在Activity和Fragment里面用这个postJson方法,但Activity必须继承BaseActivity，Fragment必须继承BaseFragment）
     *
     * @param url
     * @param params
     * @param composer
     * @param callback
     * @param <T>
     */
    public static <T> void postJson(String url, Map<String, Object> params, LifecycleTransformer<ResponseBody> composer, HttpCallBack<T> callback) {
        if (INSTANCE != null && INSTANCE.service != null) {

            String json = GsonUtils.toJson(params);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);

            Observable<ResponseBody> observable = INSTANCE.service.postJson(url, body);
            if (composer != null) {
                observable.compose(composer);
            }
            //响应观察者
            ResponseObserver<T> observer = new ResponseObserver<>(callback);

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        }
    }


    /**
     * POST请求（格式json, 建议在Activity和Fragment里面用这个postJson方法,但Activity必须继承BaseActivity，Fragment必须继承BaseFragment）
     *
     * @param url
     * @param json
     * @param composer
     * @param callback
     * @param <T>
     */
    public static <T> void postJson(String url, String json, LifecycleTransformer<ResponseBody> composer, HttpCallBack<T> callback) {
        if (INSTANCE != null && INSTANCE.service != null) {

            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);

            Observable<ResponseBody> observable = INSTANCE.service.postJson(url, body);
            if (composer != null) {
                observable.compose(composer);
            }
            //响应观察者
            ResponseObserver<T> observer = new ResponseObserver<>(callback);

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        }
    }

    public static <T> void deleteOrder(String url, Map<String, String> params, LifecycleTransformer<ResponseBody> composer, HttpCallBack<T> callback) {
        if (INSTANCE != null && INSTANCE.service != null) {

            Observable<ResponseBody> observable = INSTANCE.service.deleteOrder(params.get("odrId"), params.get("year"));
            if (composer != null) {
                observable.compose(composer);
            }
            //响应观察者
            ResponseObserver<T> observer = new ResponseObserver<>(callback);

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        }
    }


    public static <T> void putUpdateOrderState(String url, Map<String, String> params, LifecycleTransformer<ResponseBody> composer, HttpCallBack<T> callback) {
        if (INSTANCE != null && INSTANCE.service != null) {

            Observable<ResponseBody> observable = INSTANCE.service.putUpdateOrderState(params.get("odrId"), params.get("year"), params.get("odrStatus"));
            if (composer != null) {
                observable.compose(composer);
            }
            //响应观察者
            ResponseObserver<T> observer = new ResponseObserver<>(callback);

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        }
    }


    /**
     * 混合表单提交，上传字段和文件
     *
     * @param url
     * @param params
     * @param files
     * @param callback
     * @param <T>
     */
    public static <T> void upload(String url, Map<String, String> params, List<FilePartEntity> files, HttpCallBack<T> callback) {
        upload(url, params, files, null, callback);
    }


    /**
     * 混合表单提交，上传字段和文件
     *
     * @param url
     * @param params
     * @param files
     * @param callback
     * @param <T>
     */
    public static <T> void upload(String url, Map<String, String> params, List<FilePartEntity> files, LifecycleTransformer<ResponseBody> composer, HttpCallBack<T> callback) {

        if (INSTANCE != null && INSTANCE.service != null) {

            List<MultipartBody.Part> list = new ArrayList<>();
            if (files != null && !files.isEmpty()) {
                for (FilePartEntity part : files) {
                    String contentType = FileUtils.toContentType(part.file);
                    RequestBody body = RequestBody.create(MediaType.parse(contentType), part.file);
                    MultipartBody.Part multiPart = MultipartBody.Part.createFormData(part.name, part.file.getName(), body);
                    list.add(multiPart);
                }
            }

            Observable<ResponseBody> observable = INSTANCE.service.upload(url, toHttpParams(params), list);
            if (composer != null) {
                observable.compose(composer);
            }
            //响应观察者
            ResponseObserver<T> observer = new ResponseObserver<>(callback);

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        }
    }

    /**
     * 下载文件
     *
     * @param url
     * @param callback (code = 0 表示失败； code = 1 表示成功)
     */
    public static void download(String url, HttpFileCallBack callback) {
        download(url, null, callback);
    }


    /**
     * 下载文件
     *
     * @param url
     * @param composer
     * @param callback(code = 0 表示失败； code = 1 表示成功)
     */
    public static void download(String url, LifecycleTransformer<ResponseBody> composer, HttpCallBack<byte[]> callback) {
        if (INSTANCE != null && INSTANCE.service != null) {

            List<MultipartBody.Part> list = new ArrayList<>();
            Observable<ResponseBody> observable = INSTANCE.service.download(url);
            if (composer != null) {
                observable.compose(composer);
            }
            //响应观察者
            DownloadObserver<byte[]> observer = new DownloadObserver(callback);

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        }
    }


    /**
     * 下载文件
     *
     * @param url
     * @param composer
     * @param callback(code = 0 表示失败； code = 1 表示成功)
     */
    public static void download(String url, LifecycleTransformer<ResponseBody> composer, HttpFileCallBack callback) {
        if (INSTANCE != null && INSTANCE.service != null) {

            List<MultipartBody.Part> list = new ArrayList<>();
            Observable<ResponseBody> observable = INSTANCE.service.download(url);
            if (composer != null) {
                observable.compose(composer);
            }
            //响应观察者
            DownloadObserver2 observer = new DownloadObserver2(callback);

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        }
    }


    /**
     * 转换成表单参数
     *
     * @param params
     * @return
     */
    private static Map<String, RequestBody> toHttpParams(Map<String, String> params) {
        Map<String, RequestBody> newParams = new HashMap();

        if (params != null && !params.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String value = StringUtils.isEmpty(entry.getValue()) ? "" : entry.getValue();
                newParams.put(entry.getKey(), RequestBody.create(MediaType.parse("text/plain"), value));
            }
        }
        return newParams;
    }


    /**
     * 数据响应
     *
     * @param <T>
     */
    private static class ResponseObserver<T> implements Observer<ResponseBody> {

        private HttpCallBack<T> callback;
        private ResponseEntity<T> result;
        private Charset UTF8 = Charset.forName("UTF-8");

        public ResponseObserver(HttpCallBack<T> callback) {
            this.callback = callback;
        }

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(ResponseBody response) {

            if (callback == null) return;

            String code = "0";
            int status = 0;
            String message = "";
            T t = null;
            long timestamp = 0;
            Object bean = null;
            boolean rel = false;
            String json = "";
            try {
                json = response.string();
                if (!StringUtils.isEmpty(json)) {

                    ResponseEntity<T> entity = null;
                    if (callback instanceof HttpEmptyCallBack) {
                        entity = GsonUtils.fromJson(json, ResponseEntity.class);
                    } else {
                        entity = GsonUtils.fromJson(json, callback);
                    }

                    if (entity != null) {
                        if ("200".equals(entity.getCode()) && 0 == entity.getStatus()) {
                            code = "1";
                        } else {
                            code = entity.getCode();
//                            if("401".equals(code)) {
//                                EventBus.getDefault().post(new MYSystemEvent("reLogin"));//重新登录
//                            }
                        }
                        timestamp = entity.getTimestamp();
                        status = entity.getStatus();
                        message = entity.getMessage();
                        t = entity.getData();
                        bean = entity.getBean();
                        rel = entity.isRel();
                    } else {
                        code = "0";
                        message = "解析内容为空";
                    }
                } else {
                    code = "0";
                    message = "返回json为空";
                }
            } catch (Exception e) {
                try {
                    ResponseEntity<T> entity = GsonUtils.fromJson(json, ResponseEntity.class);
                    if (entity != null) {
                        code = entity.getCode();
                        timestamp = entity.getTimestamp();
                        message = entity.getMessage();
                    } else {
                        code = "0";
                        message = "数据解析异常";
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                e.printStackTrace();
            } finally {
                if (this.callback != null) {
                    //如果回调对象不为空，则不管什么情况，都要有回调
                    this.result = new ResponseEntity<>();
                    this.result.setCode(code);
                    this.result.setMessage(message);
                    this.result.setStatus(status);
                    this.result.setData(t);
                    this.result.setTimestamp(timestamp);
                    this.result.setBean(bean);
                    this.result.setRel(rel);
                    this.callback.setResult(result);
                    if ("1".equals(code)) {
                        callback.success(code, message, t);
                    } else {
                        this.callback.error(code, message);
                    }
                }
            }

        }

        @Override
        public void onError(Throwable e) {
            LogUtils.e("sunshine-exception", StringUtils.isEmpty(e.getMessage()) ? "请求失败" : e.getMessage());
            e.printStackTrace();

            this.result = new ResponseEntity<>();
            this.result.setCode("0");
            this.result.setMessage("网络或者服务器异常");
            this.callback.setResult(result);
            if (this.callback != null) {
                this.callback.error(this.result.getCode(), this.result.getMessage());
                this.callback.complete(this.result.getCode(), this.result.getMessage(), this.result.getData());
            }
        }

        @Override
        public void onComplete() {
            if (this.callback != null && this.result != null) {
                this.callback.complete(this.result.getCode(), this.result.getMessage(), this.result.getData());
            }
        }
    }


    /**
     * 下载响应
     *
     * @param <T>
     */
    private static class DownloadObserver<T> implements Observer<ResponseBody> {

        private HttpCallBack<T> callback;
        private T t;

        public DownloadObserver(HttpCallBack<T> callback) {
            this.callback = callback;
        }

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(ResponseBody response) {

            if (callback == null) return;
            byte[] buffer = null;
            try {
                t = (T) response.bytes();
            } catch (IOException e) {
                LogUtils.e("sunshine-exception", StringUtils.isEmpty(e.getMessage()) ? "下载失败" : e.getMessage());
                e.printStackTrace();
            } finally {
                if (this.callback != null) {
                    if (t != null) {
                        callback.success("1", "下载成功", t);
                    } else {
                        this.callback.error("0", "下载失败");
                    }
                }
            }

        }

        @Override
        public void onError(Throwable e) {
            LogUtils.e("sunshine-exception", StringUtils.isEmpty(e.getMessage()) ? "下载失败" : e.getMessage());
            e.printStackTrace();
            t = null;
            if (this.callback != null) {
                this.callback.error("0", "下载失败");
            }
        }

        @Override
        public void onComplete() {
            if (this.callback != null) {
                if (t != null) {
                    callback.complete("1", "下载成功", t);
                } else {
                    this.callback.complete("0", "下载失败", null);
                }
            }
        }
    }

    /**
     * 下载响应
     */
    private static class DownloadObserver2 implements Observer<ResponseBody> {

        private HttpFileCallBack callback;
        private Handler handler;

        public DownloadObserver2(HttpFileCallBack callback) {
            this.callback = callback;
        }

        @Override
        public void onSubscribe(Disposable d) {
            LogUtils.i("SUNSHINE-NET", "onSubscribe");
        }

        @Override
        public void onNext(ResponseBody response) {
            LogUtils.i("SUNSHINE-NET", "onNext");
            if (callback == null) return;
            this.handler = new Handler();
            DownloadThread thread = new DownloadThread(response, callback, handler);
            thread.start();
        }

        @Override
        public void onError(Throwable e) {
            LogUtils.e("sunshine-exception", StringUtils.isEmpty(e.getMessage()) ? "下载失败" : e.getMessage());
            e.printStackTrace();
            if (this.callback != null) {
                this.callback.error("0", "下载失败");
            }
        }

        @Override
        public void onComplete() {

        }
    }

    private static class DownloadThread<T> extends Thread {

        private ResponseBody response;
        private HttpFileCallBack callback;
        private Handler handler;

        public DownloadThread(ResponseBody response, HttpFileCallBack callback, Handler handler) {
            this.response = response;
            this.callback = callback;
            this.handler = handler;
        }

        @Override
        public void run() {
            super.run();

            try {
                long maxLength = response.contentLength();
                long sumLength = 0;
                int max = 100;
                InputStream is = response.byteStream();
                FileOutputStream fos = new FileOutputStream(callback.getTargetFile());
                if (is != null) {
                    byte[] buffer = new byte[4096];
                    int len = -1;
                    while ((len = is.read(buffer)) != -1) {
                        sumLength += len;
                        fos.write(buffer, 0, len);
                        int progress = (int) ((sumLength * 1.0d / maxLength) * 100);
                        LogUtils.i("SUNSHINE-NET", "进度->" + progress);
                        if (handler != null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (callback != null) {
                                        callback.onProgress(max, progress, maxLength, maxLength);
                                    }
                                }
                            });
                        }
                    }
                    fos.flush();
                    fos.close();
                    is.close();
                }
            } catch (IOException e) {
                LogUtils.e("sunshine-exception", StringUtils.isEmpty(e.getMessage()) ? "下载失败" : e.getMessage());
                e.printStackTrace();
            } finally {
                if (this.callback != null && this.handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback.getTargetFile() != null && callback.getTargetFile().length() > 0) {
                                callback.success("1", "下载成功", callback.getTargetFile());
                            } else {
                                callback.error("0", "下载失败");
                            }

                            callback.complete("2", "", callback.getTargetFile());
                        }
                    });
                }
            }


        }
    }

}
