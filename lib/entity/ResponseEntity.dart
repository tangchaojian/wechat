import 'package:wechat/utils/json_helper.dart';

class ResponseEntity<T> {
  int code;
  int status;
  T data;

  ResponseEntity({this.code, this.status, this.data});

  ResponseEntity.fromJson(Map<String, dynamic> json) {
    this.code = json['code'];
    this.status = json['status'];
    if (json['data'] != null && json['data'] != 'null') {
      this.data = JsonHelper.fromJsonAsT<T>(json['data']);
    }

  }

  Map<String, dynamic> toJson() {
    Map<String, dynamic> data = new Map<String, dynamic>();
    data['code'] = this.code;
    data['status'] = this.status;

    if (this.data != null) {
      data['data'] = this.data;
    }
    return data;
  }
}
