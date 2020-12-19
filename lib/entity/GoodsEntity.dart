/// goodsName : "P30"

class GoodsEntity {
  String goodsName;

  static GoodsEntity fromMap(Map<String, dynamic> map) {
    if (map == null) return null;
    GoodsEntity goodsEntityBean = GoodsEntity();
    goodsEntityBean.goodsName = map['goodsName'];
    return goodsEntityBean;
  }

  Map toJson() => {
    "goodsName": goodsName,
  };
}