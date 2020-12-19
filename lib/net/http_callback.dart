
typedef Success<T> = Function(int code, String msg, T data);//成功回调
typedef Fail = Function(int code, String msg);//失败回调
typedef Complete<T> = Function(int code, String msg, T data);//完成回调

class HttpCallback<T> {
  Success<T> success;
  Fail fail;
  Complete<T> complete;

  HttpCallback ({Success this.success, Fail this.fail, Complete this.complete});
}