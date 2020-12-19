import 'package:wechat/entity/GoodsEntity.dart';
import 'package:wechat/entity/ListInfoEntity.dart';
import 'package:wechat/entity/ResponseEntity.dart';

class JsonHelper {
  static T fromJsonAsT<T>(json) {
    if(T is ListInfoEntity) {
      return ListInfoEntity().fromJson(json);
    }
    return json;
  }
}