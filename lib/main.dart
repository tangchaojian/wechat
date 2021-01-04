import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:umeng_sdk/umeng_sdk.dart';
import 'package:wechat/app.dart';
import 'package:wechat/effect/sliver_app_bar_demo.dart';
import 'package:wechat/effect/tab_bar_demo.dart';
import 'package:wechat/loading.dart';
import 'package:wechat/mall/CartListPage.dart';
import 'package:wechat/mall/GoodsListPage.dart';
import 'package:wechat/mall/OrderConfirmPage.dart';
import 'package:wechat/net/http_helper.dart';

import 'effect/float_button_demo.dart';
import 'effect/tab_bar_demo2.dart';
import 'mall/GoodsListPage2.dart';
import 'model/CartModel.dart';

//程序入口
void main() {
  HttpHelper.init();
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {

    return ChangeNotifierProvider(
      create: (context) => CartModel(),
      child:MaterialApp(
        debugShowCheckedModeBanner: false,//是否显示状态栏右上角的debug标识
        title: 'wechat',
        theme: themeData,
        routes: <String, WidgetBuilder>{
          '/app': (BuildContext context) => new App(),
          '/cart': (BuildContext context) => new CartListPage(),
          '/orderConfirm': (BuildContext context) => new OrderConfirmPage(),
          '/goodsList': (BuildContext context) => new GoodsListPage(),
          '/goodsList2': (BuildContext context) => new GoodsListPage2(),
          '/sliverAppBarDemo': (BuildContext context) => new UISliverAppBarDemo(),
          '/tabBarViewDemo': (BuildContext context) => new UITabBarViewDemo(),
          '/tabBarViewDemo2': (BuildContext context) => new UITabBarViewDemo2(),
          '/floatButtonDemo': (BuildContext context) => new FloatButtonDemo(),
        },
        home: Loading(),
      ),
    );
  }
}

//主题
ThemeData themeData = ThemeData(
  primaryColor: Colors.white,//状态栏颜色
  primarySwatch: Colors.blue,//主题颜色
  backgroundColor: Color(0xFFEDEDED),//背景颜色
  scaffoldBackgroundColor: Color(0xFFEDEDED),
  visualDensity: VisualDensity.adaptivePlatformDensity,//设置视觉密度:适应平台密度
);
