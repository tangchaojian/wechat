package com.tcj.sunshine.lib.config;

import com.tcj.sunshine.lib.BuildConfig;

/**
 * 配置文件
 */
public class Constants {

    /**
     * 正式环境
     */
    public static final String HTTP_RELEASE_BASE_URL = "https://looyu.vip/";
    public static final String HTTP_RELEASE_LIVE_URL = "https://live.looyu.vip/";
    private static final String HTTP_BASE_TREATY_URL = "https://h.looyu.vip/";

    /**
     * 测试环境
     */
    public static final String HTTP_DEBUG_BASE_URL = "https://miaoyin.top/";
    public static final String HTTP_DEBUG_LIVE_URL = "http://live.tcstzg.com/";

    /**
     * 接口共用url
     */
    public static final String HTTP_BASE_URL = BuildConfig.HTTP_URL_RELEASE ? Constants.HTTP_RELEASE_BASE_URL : Constants.HTTP_DEBUG_BASE_URL;
    public static final String HTTP_LIVE_URL = BuildConfig.HTTP_URL_RELEASE ? Constants.HTTP_RELEASE_LIVE_URL : Constants.HTTP_DEBUG_LIVE_URL;
    public static final String HTTP_TREATY_URL = BuildConfig.HTTP_URL_RELEASE ? HTTP_BASE_TREATY_URL : (Constants.HTTP_BASE_URL + "app/treaty/");


    /**
     * HTTP缓存大小
     */
    public static final long HTTP_MAX_CACHE = 1024 * 1024 * 100L;//缓存大小

    /**
     * 网络连接超时,单位秒
     */
    public static final int HTTP_CONNECT_TIMEOUT = 10;//单位秒(s)

    /**
     * 网络读超时,单位秒
     */
    public static final int HTTP_READ_TIMEOUT = 10;//单位秒(s)

    /**
     * 网络写超时,单位秒
     */
    public static final int HTTP_WRITE_TIMEOUT = 10;//单位秒(s)

    /**
     * Http请求头：User-Agent 避免http请求出现 HTTP 403 Forbidden
     */
    public static final String HTTP_USER_AGENT = "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Mobile Safari/537.36";

    /**
     * 有网的情况下，在第一次请求数据到max-age秒内，不会去请求新的数据，只会从缓存中读取
     */
    public static final int HTTP_MAX_CACHE_TIME = 3;//秒

    /**
     * http 头部：Cache-Control， 有网情况下，假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内, 客户端将不会发送对应的请求到服务器，数据由缓存直接返回
     */
    public static final String HTTP_CACHE_CONTROL = "public, max-age=" + HTTP_MAX_CACHE_TIME;

    /**
     * 设置没有网络缓存有效期为1天
     */
    public static final int HTTP_NO_NET_MAX_CACHE_TIME = 60 * 60;

    /**
     * http 头部：Cache-Control， 没有网络为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
     */
    public static final String HTTP_NO_NET_CACHE_CONTROL = "public, only-if-cached, max-stale=" + HTTP_NO_NET_MAX_CACHE_TIME;


    public static String HTML_HEADER = "<html>"
            + "<head>"
            + "<meta charset=\"UTF-8\">"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\" />"
            + "<meta content=\"email=no\" name=\"format-detection\" />"
            + "<meta content=\"telephone=no\" name=\"format-detection\" />"
            + "<title></title>"
            + "<style type=\"text/css\">"
            + "html{margin: 0 0 0 0}"
            + "body{background:#FFFFFF;padding: 0 0 0 0; margin: 0 0 0 0}"
            + "img{width:100%;}"
            + "</style>"
            + "</head>"
            + "<body>";


    public static final String HTML_FOOTER = "</body></html>";

}
