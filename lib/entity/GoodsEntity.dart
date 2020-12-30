
/// goodsName : "P30"

class GoodsEntity{
  String goodsName;
  String goodsImageUrl;
  double goodsPrice;
  double goodsMarketPrice;

  GoodsEntity({this.goodsName}) : super();

  GoodsEntity.fromJson(json){
    this.goodsName = json['goodsName'];
    this.goodsImageUrl = json['goodsImageUrl'];
    this.goodsPrice = json['goodsPrice'];
    this.goodsMarketPrice = json['goodsMarketPrice'];
  }

  Map<String, dynamic> toJson() {
    Map<String, dynamic> data = new Map<String, dynamic>();
    data['goodsName'] = this.goodsName;
    data['goodsImageUrl'] = this.goodsImageUrl;
    data['goodsPrice'] = this.goodsPrice;
    data['goodsMarketPrice'] = this.goodsMarketPrice;
    return data;
  }

}