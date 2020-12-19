import 'package:wechat/utils/json_helper.dart';

class ListInfoEntity<T>{
  int pageNum;
  List<T> list;

  ListInfoEntity({this.pageNum, this.list});

  fromJson(Map<String, dynamic> json) {
    this.pageNum = json['pageNum'];
    if (json['list'] != null && json['list'] != 'null') {
      this.list = JsonHelper.fromJsonAsT<List<T>>(json['list']);
    }

  }

  Map<String, dynamic> toJson() {
    Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.list != null) {
      data['list'] = this.list;
    }
    data['pageNum'] = this.pageNum;
    return data;
  }
}