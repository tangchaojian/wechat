import 'package:dio/dio.dart';
import 'package:wechat/utils/log.dart';

class HttpLogInterceptor extends Interceptor {


  @override
  Future onRequest(RequestOptions options) async{

    Log.i("NET", "=====================HTTP 请求 START =========================");
    Log.i("NET", "method:" + options.method);
    Log.i("NET", "url:" + options.path);
    Map<String, dynamic> params = options.queryParameters;
    if(params != null && params.isNotEmpty) {
      StringBuffer buffer = StringBuffer();
      buffer.write("[");
      params.forEach((key, value) {
        if(value is String || value is int || value is double || value is bool ) {
          buffer.write("$key:$value");
          buffer.write(", ");
        }else {
          buffer.write("$key:DATA");
          buffer.write(", ");
        }
      });
      buffer.write("]");
      Log.i("NET", "参数:" + buffer.toString());
    }
    Log.i("NET", "=====================HTTP 请求 END============================");
    return options;
  }

  @override
  Future onError(DioError err) async {
    Log.e("NET", "=====================HTTP 响应 START =========================");
    Log.e("NET", "method:" + err.request.method);
    Log.e("NET", "url:" + err.request.path);
    Log.e("NET", "HTTP错误码:" + err.error.toString());
    Log.e("NET", "HTTP错误信息:" + err.message);

    Log.i("NET", "=====================HTTP 响应 END ============================");
    return err;
  }

  @override
  Future onResponse(Response response) async{

    Log.i("NET", "=====================HTTP 响应 START =========================");
    Log.i("NET", "method:" + response.request.method);
    Log.i("NET", "url:" + response.request.path);
    Log.i("NET", "HTTP响应码:" + response.statusCode.toString());
    Log.i("NET", "HTTP响应信息:" + response.statusMessage);
    Log.i("NET", "HTTP响应内容:" + response.data.toString());

    Log.i("NET", "=====================HTTP 响应 END ============================");
    return response;
  }
}