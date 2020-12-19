import 'package:dio/dio.dart';
import 'package:dio_cookie_manager/dio_cookie_manager.dart';
import 'package:cookie_jar/cookie_jar.dart';
import 'package:wechat/entity/ListInfoEntity.dart';
import 'package:wechat/entity/ResponseEntity.dart';
import 'package:wechat/net/http_callback.dart';
import 'package:wechat/net/http_constants.dart';

import 'http_log_interceptor.dart';




class HttpHelper {
  static HttpHelper _instance;
  Dio dio;

  HttpCallback callback;
  //内部构造方法，可避免外部暴露构造函数，进行实例化
  HttpHelper._internal();

  //工程构造方法，这里使用命名构造函数方式进行声明
  factory HttpHelper.init() => _init();

  // 获取单例内部方法
  static _init() {
    if(_instance == null) {
      _instance = HttpHelper._internal();

      BaseOptions options = BaseOptions(
        baseUrl: HttpConstants.baseUrl,
        connectTimeout: HttpConstants.connectTimeout,//链接超时
        sendTimeout: HttpConstants.sendTimeout,//发送超时
        receiveTimeout: HttpConstants.receiveTimeout,//接收超时
        headers: {//请求头
          'Accept': 'application/json',
          'Content-Type': 'application/json; charset=utf-8',
        },
        responseType: ResponseType.json,//相应格式
      );
      _instance.dio = Dio(options);
      _instance.dio.interceptors.add(HttpLogInterceptor());
      _instance.dio.interceptors.add(CookieManager(CookieJar()));
    }
    return _instance;
  }

  static void get<T>(String url, {params, HttpCallback<T> callback}) async{
    if(_instance == null || _instance.dio == null)return;
    try {
      _instance.dio.options.method = "GET";
      Response response = await _instance.dio.request(url, queryParameters: params);
      ResponseEntity<ListInfoEntity> responseEntity = ResponseEntity.fromMap(response.data);
      print();

    }on DioError catch (e) {
      /// 打印请求失败相关信息
      print('请求出错：' + e.toString());
    }
  }

}