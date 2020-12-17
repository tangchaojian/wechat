import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:wechat/entity/CartEntity.dart';
import 'package:wechat/model/CartModel.dart';

//购物车列表
class OrderConfirmPage extends StatefulWidget {
  @override
  _OrderConfirmPage createState() => _OrderConfirmPage();
}

class _OrderConfirmPage extends State<OrderConfirmPage> {
  List<CartEntity> list = List();

  @override
  Widget build(BuildContext context) {

    var cart = context.watch<CartModel>();

    return Scaffold(
      appBar: AppBar(
        title: Text("确认订单页"),
        elevation: 0.5,
      ),
      body: Container(
        alignment: Alignment.center,
        child: Consumer<CartModel>(
          builder: (context, cart, child){
            return Text("总金额:¥${cart.totalMoney}", style: TextStyle(color: Colors.black),);
          },
        ),
      ),
    );
  }
}
