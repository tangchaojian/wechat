import 'package:flutter/material.dart';
import 'package:umeng_sdk/umeng_sdk.dart';

class Loading extends StatefulWidget {

  @override
  _LoadingState createState() => _LoadingState();
}

class _LoadingState extends State<Loading>{

  @override
  void initState() {
    super.initState();
    Future.delayed(Duration(seconds: 3), (){
        print("倒计时完成，准备跳转...");
        Navigator.pushNamedAndRemoveUntil(context, "/app", (route) => false);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Container(
        child: Image.asset("images/loading.jpg"),
    );
  }

  @override
  void dispose() {
    super.dispose();
  }

}