import 'package:flutter/material.dart';
import 'package:wechat/loading.dart';

//程序入口
void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,//是否显示状态栏右上角的debug标识
      title: 'wechat',
      theme: themeData,
      home: Loading(),
    );
  }
}

//主题
ThemeData themeData = ThemeData(
  primaryColor: Color(0xffededed),//状态栏颜色
  primarySwatch: Colors.blue,//主题颜色
  backgroundColor: Color(0xFFEDEDED),//背景颜色
  visualDensity: VisualDensity.adaptivePlatformDensity,//设置视觉密度:适应平台密度
);
