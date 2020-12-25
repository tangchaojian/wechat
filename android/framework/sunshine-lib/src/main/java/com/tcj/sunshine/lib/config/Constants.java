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

    /**
     * 微信APP_ID
     */
    public static final String WX_APP_ID = "wx9f7d0711f8b3ea60";

    public static final String KEY_TOKEN = "accessToken";

    public static final String KEY_IS_SHOW_SHORT_VIDEO_PROTOCOL = "key_is_show_short_video_protocol";

    public static final String HTTP_URL_APP_URL_TREATY = HTTP_TREATY_URL;  //分享，举报

    public static final String HTTP_URL_APP_URL_SHORTVIDEO = HTTP_TREATY_URL + "#/short-video/"; //短视频

    /**
     * 短视频可见权限
     */
    public static final int POWER_OPEN = 2;  //所有人可见
    public static final int POWER_FANS = 1;  //关注可见
    public static final int POWER_OWNER = 0; //自己可见


    //用户信息
    public static final String P_ISLOGIN = "pref.isLogin";
    public static final String P_MEMBERID = "pref.memberid";
    public static final String P_MY_ACCOUNT= "pref.MYaccount";
    public static final String P_NICKNAME = "pref.returnusername";
    public static final String P_REGION = "pref.region";
    public static final String P_PHONE = "pref.mrPhone";
    public static final String P_GENDER = "pref.sex";
    public static final String P_HEADERURL = "pref.headerurl";
    public static final String P_ISREAL = "pref.isReal";
    public static final String P_ISONLINE = "pref.isOnline";
    public static final String P_ISPWD = "pref.isPwd";
    public static final String P_CREDITSOURCE = "pref.creditScore";
    public static final String P_TOKEN = "pref.token";
    public static final String P_SHARENUMBER = "pref.shareNumber";
    public static final String P_STATUS = "pref.status";
    public static final String P_CREATEDATE = "pref.createDate";
    public static final String P_UPDATEDATE = "pref.updateDate";
    public static final String P_ISACCOUNT = "pref.isAccount";
    public static final String P_ISSELLER = "pref.isSeller";
    public static final String P_SCOPE = "pref.scope";
    public static final String P_ACCESSTOKEN = "pref.accessToken";
    public static final String P_TOKENTYPE = "pref.tokenType";
    public static final String P_EXPIREIN = "pref.expiresIn";
    public static final String P_REFRESHTOKEN = "pref.refreshToken";
    public static final String P_SIGN = "pref.sign";
    public static final String P_ISAGENT = "pref.isAgent";
    public static final String P_ISUSERSIGN = "pref.isUserSign";
    public static final String P_ISPAYPHONE = "pref.isPayPhone";
    public static final String P_INVITECODE = "pref_inviteCode";
    public static final String P_ISVERIFICATION = "pref_isVerification";
    public static final String P_ALIACCOUNT = "pref.aliAccount";
    public static final String P_WXACCOUNT = "pref_wxAccount";
    public static final String P_IS_FIRST_TIME = "pref_isFirstTime";

    public static final String KEY_SEARCH_GOODS = "key.search.goods";
    public static final String KEY_SEARCH_SHOP = "key.search.shop";


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

    public static final String KEY_CART_NUM = "cart_num";

    public static final String FACE_APP_ID = "IDAzM92B";
    public static final String FACE_SDK_LICENSE = "riveM4kriIT+WTQH7z60hu4layYpoqTaDaZcX5KXSK+c6k6lHG1b8RXNY9No0nPnj3NahGlkzEjLCi3TRNetdzqgJGX8E36169qukSBy3WGb2nZ67aGAQAU0iU/6nSMtqAiGhe9IsF9xtxOCVa5wRELETlSaaiMyvw7VQ7CIH85CCnrqitNpHTYsQVxdw54hC9Yp/je3j9Hsy5TbyP+Qhu7jG12ZQ8vpldHWhkhjYMgI3TWh82xBGf5dVf6ZMQ5vO/qELtc0oMB+Mvhgp+xbr6D1I3xpCzSFC2y64W+HxM0mPS1E6SdqrNOOsRK4Is+OEFuvRQtoD9132/w/ZICUQg==";

}
