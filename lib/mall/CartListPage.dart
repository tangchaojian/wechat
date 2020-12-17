import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:wechat/entity/CartEntity.dart';
import 'package:wechat/mall/OrderConfirmPage.dart';
import 'package:wechat/model/CartModel.dart';

//购物车列表
class CartListPage extends StatefulWidget {
  @override
  _CartListPage createState() => _CartListPage();
}

class _CartListPage extends State<CartListPage> {
  List<CartEntity> list = List();

  @override
  void initState() {
    super.initState();
    CartEntity item1 = new CartEntity("华为P30Pro/p30/p40pro 手机 p40pro亮黑色（5G） 全网通 （8G+512G）", 1, 7388.0);
    CartEntity item2 = new CartEntity("圆珠笔", 1, 10.0);
    CartEntity item3 = new CartEntity("小米（MI） 小爱音箱Pro小爱同学人工智能语音蓝牙AI音响WIFI小艾网络迷你低音炮 小米小爱音箱Pro", 1, 2090.0);
    CartEntity item4 = new CartEntity("潮牌T恤", 1, 129.0);

    list.add(item1);
    list.add(item2);
    list.add(item3);
    list.add(item4);

    var cart = context.read<CartModel>();
    cart.add(item1);
    cart.add(item2);
    cart.add(item3);
    cart.add(item4);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("购物车列表"),
        elevation: 0.5,
      ),
      body: Column(
        children: [
          Expanded(
            flex: 1,
            child: ListView.separated(
                itemBuilder: (BuildContext context, int index) {
                  return Container(
                    height: 80,
                    color: Colors.white,
                    child: Stack(
                      children: [
                        Container(
                          height: 80,
                          margin: EdgeInsets.only(right: 90),
                          alignment: Alignment.centerLeft,
                          padding: EdgeInsets.only(left: 10, top: 5),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                "${list[index].name}",
                                maxLines: 2,
                                overflow: TextOverflow.ellipsis,
                                style: TextStyle(fontSize: 16),
                              ),
                              Padding(
                                padding: EdgeInsets.only(top: 10),
                                child: Row(
                                  children: [
                                    Text(
                                      "¥${list[index].price}",
                                      style: TextStyle(
                                          fontSize: 14, color: Colors.red),
                                    ),
                                    Padding(
                                      padding: EdgeInsets.only(left: 20),
                                      child: Consumer<CartModel>(
                                        builder: (context, cart, child) {
                                          return Text(
                                            "x${cart.getItemCartNum(index)}",
                                            style: TextStyle(
                                                fontSize: 14,
                                                color: Colors.black),
                                          );
                                        },
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ],
                          ),
                        ),
                        Align(
                          alignment: Alignment.centerRight,
                          child: Padding(
                            padding: EdgeInsets.only(right: 20),
                            child: SizedBox(
                              width: 60,
                              height: 30,
                              child: FlatButton(
                                child: Text("添加"),
                                textColor: Colors.white,
                                color: Colors.red,
                                highlightColor: Colors.redAccent,
                                shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(5.0)),
                                onPressed: () {
                                  //加入购物车
                                  print("加入购物车");
                                  // Provider.of<CartModel>(context,
                                  //         listen: false)
                                  //     .add(list[index]);

                                  var cart = context.read<CartModel>();
                                  cart.add(list[index]);
                                },
                              ),
                            ),
                          ),
                        )
                      ],
                    ),
                  );
                },
                separatorBuilder: (BuildContext context, int index) {
                  return new Divider(
                    color: Color(0xFFD0D0D0),
                    height: 0.5,
                  );
                },
                itemCount: list.length),
          ),
          SizedBox(
            width: double.infinity,
            height: 60,
            child: Consumer<CartModel>(
              builder: (context, cart, child) {
                return Stack(
                  children: [
                    Container(
                      width: double.infinity,
                      height: 60,
                      color: Colors.white,
                    ),
                    Align(
                      alignment: Alignment.centerLeft,
                      child: Row(
                        children: [
                          Padding(padding: EdgeInsets.only(left: 10),
                            child: Text("总计:", style: TextStyle(
                                fontSize: 14,
                                fontWeight: FontWeight.bold
                            ),),
                          ),
                          Text(
                            "¥${cart.totalMoney.toString()}",
                            style: TextStyle(
                                fontSize: 14,
                                fontWeight: FontWeight.bold
                            ),),
                        ],
                      ),
                    ),
                    Align(
                        alignment: Alignment.centerRight,
                        child: Padding(
                          padding: EdgeInsets.only(right: 10),
                          child: InkWell(
                            onTap: (){
                              // Navigator.push(context, MaterialPageRoute(builder: (BuildContext context) => new OrderConfirmPage()));
                              Navigator.pushNamed(context, "/orderConfirm");
                            },
                            child: SizedBox(
                              width: 60,
                              height: 60,
                              child: Stack(
                                children: [
                                  Align(
                                    alignment: Alignment.center,
                                    child: Image.asset("images/ic_cart.png",
                                        width: 40, height: 40),
                                  ),
                                  Offstage(
                                    offstage: cart.cartNum <= 0,
                                    child: Align(
                                        alignment: Alignment.topRight,
                                        child: Container(
                                          alignment: Alignment.center,
                                          padding: EdgeInsets.only(left: 3, right: 3),
                                          width: 22,
                                          height: 22,
                                          margin: EdgeInsets.only(top: 5, right: 5),
                                          decoration: BoxDecoration(
                                              color: Colors.red,
                                              borderRadius: BorderRadius.circular(11),
                                              border: Border.all(
                                                color: Colors.white,
                                                width: 1,
                                              )),
                                          child: Text(
                                            cart.cartNum.toString(),
                                            style: TextStyle(
                                                color: Colors.white,
                                                fontSize: 14),
                                          ),
                                        )),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        )),
                  ],
                );
              },
            ),
          )
        ],
      ),
    );
  }
}
