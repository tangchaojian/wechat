import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:wechat/mall/CartListPage.dart';
import 'package:wechat/native/interact_native.dart';
import 'package:wechat/utils/log.dart';

class NewsPage extends StatefulWidget {

  @override
  _NewsPage createState() => _NewsPage();
}

class _NewsPage extends State<NewsPage> {

  // static const EventChannel _channel = const EventChannel('flutter_wechat_plugin_native_to_flutter');
  // StreamSubscription _streamSubscription;
  //
  // String version = "";

  @override
  void initState() {
    super.initState();
    // _enableEventReceiver();
  }

  @override
  void dispose() {
    super.dispose();
    // _disableEventReceiver();
  }

  // void _enableEventReceiver(){
  //   _streamSubscription = _channel.receiveBroadcastStream().listen((dynamic event) {
  //     print('Received event: $event');
  //   },
  //     onError: (dynamic event){
  //       print("错误");
  //     },
  //     cancelOnError: true
  //   );
  // }
  //
  // void _disableEventReceiver() {
  //   if (_streamSubscription != null) {
  //     _streamSubscription.cancel();
  //     _streamSubscription = null;
  //   }
  // }


  @override
  Widget build(BuildContext context) {
    return Container(
        child: Center(
          child: Column(
            children: [
              FlatButton(
                textColor: Colors.white,
                color: Colors.blue,
                highlightColor: Colors.blueAccent,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(5)),
                child: Text("跳转购物车", style: TextStyle(fontSize: 14),),
                onPressed: (){
                  // Navigator.push(context, MaterialPageRoute(builder: (BuildContext context){
                  //   return CartListPage();
                  // }));
                  Navigator.pushNamed(context, "/cart");
                },
              ),

              Padding(padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(5)),
                  child: Text("跳转商品列表", style: TextStyle(fontSize: 14),),
                  onPressed: (){
                    // Navigator.push(context, MaterialPageRoute(builder: (BuildContext context){
                    //   return CartListPage();
                    // }));
                    Navigator.pushNamed(context, "/goodsList");
                  },
                ),
              ),

              Padding(padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(5)),
                  child: Text("EASY_REFRESH", style: TextStyle(fontSize: 14),),
                  onPressed: (){
                    // Navigator.push(context, MaterialPageRoute(builder: (BuildContext context){
                    //   return CartListPage();
                    // }));
                    Navigator.pushNamed(context, "/goodsList2");
                  },
                ),
              ),

              Padding(padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(5)),
                  child: Text("相册调用", style: TextStyle(fontSize: 14),),
                  onPressed: () {
                    InteractNative.openAlbum(6);
                    // Log.i("TAG", "相册调用结果$result");
                  },
                ),
              ),

              Padding(padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(5)),
                  child: Text("开始录制视频", style: TextStyle(fontSize: 14),),
                  onPressed: () {
                    InteractNative.startRecord();
                    // Log.i("TAG", "相册调用结果$result");
                  },
                ),
              ),

            ],
          ),
        ),
    );
  }
}