
/// goodsName : "P30"

class GoodsEntity{
  String goodsName;

  GoodsEntity({this.goodsName}) : super();

  GoodsEntity.fromJson(json){
    this.goodsName = json['goodsName'];
  }

  Map<String, dynamic> toJson() {
    Map<String, dynamic> data = new Map<String, dynamic>();
    data['goodsName'] = this.goodsName;
    return data;
  }

}