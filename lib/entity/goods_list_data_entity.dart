import 'package:wechat/entity/GoodsEntity.dart';

class GoodsListDataEntity {
  int pageNum;
  int pageSize;
  int size;
  List<GoodsEntity> list;

  GoodsListDataEntity({this.pageNum, this.pageSize, this.size, this.list});

  GoodsListDataEntity.fromJson(Map<String, dynamic> json) {
    pageNum = json['pageNum'];
    pageSize = json['pageSize'];
    size = json['size'];
    if (json['list'] != null) {
      list = new List<GoodsEntity>();
      json['list'].forEach((v) {
        list.add(new GoodsEntity.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['pageNum'] = this.pageNum;
    data['pageSize'] = this.pageSize;
    data['size'] = this.size;
    if (this.list != null) {
      data['list'] = this.list.map((v) => v.toJson()).toList();
    }
    return data;
  }
}