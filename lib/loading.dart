import 'package:flutter/material.dart';

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
    });
  }

  @override
  Widget build(BuildContext context) {
    return Image.asset("images/loading.jpg");
  }

}