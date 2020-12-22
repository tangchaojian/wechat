import 'package:wechat/net/http_status.dart';

typedef OnCallback<T> = Function(HttpStatus status, int code, String message, {T data});//回调

class HttpCallback<T> {
  OnCallback<T> onCallback;

  HttpCallback({OnCallback<T> this.onCallback});
}
