import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:umeng_sdk/umeng_sdk.dart';
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
    return SingleChildScrollView(
      scrollDirection: Axis.vertical,
      child: Container(
        padding: EdgeInsets.only(top: 30, bottom: 60),
        child: Center(
          child: Column(
            children: [
              FlatButton(
                textColor: Colors.white,
                color: Colors.blue,
                highlightColor: Colors.blueAccent,
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(5)),
                child: Text(
                  "跳转购物车",
                  style: TextStyle(fontSize: 14),
                ),
                onPressed: () {
                  // Navigator.push(context, MaterialPageRoute(builder: (BuildContext context){
                  //   return CartListPage();
                  // }));
                  Navigator.pushNamed(context, "/cart");
                },
              ),
              Padding(
                padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  child: Text(
                    "跳转商品列表",
                    style: TextStyle(fontSize: 14),
                  ),
                  onPressed: () {
                    // Navigator.push(context, MaterialPageRoute(builder: (BuildContext context){
                    //   return CartListPage();
                    // }));
                    Navigator.pushNamed(context, "/goodsList");
                  },
                ),
              ),
              Padding(
                padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  child: Text(
                    "EASY_REFRESH",
                    style: TextStyle(fontSize: 14),
                  ),
                  onPressed: () {
                    // Navigator.push(context, MaterialPageRoute(builder: (BuildContext context){
                    //   return CartListPage();
                    // }));
                    Navigator.pushNamed(context, "/goodsList2");
                  },
                ),
              ),
              Padding(
                padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  child: Text(
                    "相册调用",
                    style: TextStyle(fontSize: 14),
                  ),
                  onPressed: () {
                    InteractNative.openAlbum(6);
                    // Log.i("TAG", "相册调用结果$result");
                  },
                ),
              ),
              Padding(
                padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  child: Text(
                    "阿里云视频录制",
                    style: TextStyle(fontSize: 14),
                  ),
                  onPressed: () {
                    InteractNative.startAliyunRecord();
                    // Log.i("TAG", "相册调用结果$result");
                  },
                ),
              ),

              Padding(
                padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  child: Text(
                    "直播列表",
                    style: TextStyle(fontSize: 14),
                  ),
                  onPressed: () {
                    InteractNative.startLiveList();
                    // Log.i("TAG", "相册调用结果$result");
                  },
                ),
              ),

              Padding(
                padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  child: Text(
                    "看直播",
                    style: TextStyle(fontSize: 14),
                  ),
                  onPressed: () {
                    InteractNative.startLookLive();
                    // Log.i("TAG", "相册调用结果$result");
                  },
                ),
              ),

              Padding(
                padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  child: Text(
                    "开直播",
                    style: TextStyle(fontSize: 14),
                  ),
                  onPressed: () {
                    InteractNative.startTencentLive();
                    // Log.i("TAG", "相册调用结果$result");
                  },
                ),
              ),
              Padding(
                padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  child: Text(
                    "友盟统计",
                    style: TextStyle(fontSize: 14),
                  ),
                  onPressed: () {
                    UmengSdk.onEvent(
                        'bool', {'name': 'tcj', 'age': 18, 'male': true});
                    // Log.i("TAG", "相册调用结果$result");
                  },
                ),
              ),
              Padding(
                padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  child: Text(
                    "SliverAppBar",
                    style: TextStyle(fontSize: 14),
                  ),
                  onPressed: () {
                    Navigator.pushNamed(context, "/sliverAppBarDemo");
                  },
                ),
              ),
              Padding(
                padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  child: Text(
                    "TabBarView",
                    style: TextStyle(fontSize: 14),
                  ),
                  onPressed: () {
                    Navigator.pushNamed(context, "/tabBarViewDemo");
                  },
                ),
              ),

              Padding(
                padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  child: Text(
                    "TabBarView2",
                    style: TextStyle(fontSize: 14),
                  ),
                  onPressed: () {
                    Navigator.pushNamed(context, "/tabBarViewDemo2");
                  },
                ),
              ),

              Padding(
                padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  child: Text(
                    "悬浮拖动按钮",
                    style: TextStyle(fontSize: 14),
                  ),
                  onPressed: () {
                    Navigator.pushNamed(context, "/floatButtonDemo");
                  },
                ),
              ),

              Padding(
                padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  child: Text(
                    "仿抖音",
                    style: TextStyle(fontSize: 14),
                  ),
                  onPressed: () {
                    Navigator.pushNamed(context, "/douYinDemo");
                  },
                ),
              ),

              Padding(
                padding: EdgeInsets.only(top: 20),
                child: FlatButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  highlightColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  child: Text(
                    "打开微信小程序",
                    style: TextStyle(fontSize: 14),
                  ),
                  onPressed: () {
                    InteractNative.openWxMinProgram();
                  },
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
