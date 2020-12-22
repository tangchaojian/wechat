import 'dart:collection';

import 'package:wechat/entity/GoodsEntity.dart';
import 'package:wechat/utils/log.dart';

class JsonHelper {

  static T fromJsonAsT<T>(json) {
    if(json is List) {
      //数组
      Log.i("TAG", "数组");
      return parseArray<T>(json);
    }else {
      //对象
      Log.i("TAG", "对象");
      return parseObject<T>(json);
    }
  }

  static T parseArray<T>(json) {
    String type = T.toString();
    List data = json as List;

    if(type == "List<GoodsEntity>") {
      List<GoodsEntity> list = List();
      list = addChildItem(data, list);
      return list as T;
    }
    return null;
  }

  static T parseObject<T>(json){
    String type = T.toString();
    if(type == "GoodsEntity") {
      return GoodsEntity.fromJson(json) as T;
    }
    return null;
  }

  static List<M> addChildItem<M>(List data, List<M> list) {
    data.forEach((element) {
      list.add(parseObject<M>(element));
    });
    return list;
  }
}