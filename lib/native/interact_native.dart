import 'package:flutter/services.dart';

class InteractNative {
  
  static const MethodChannel _channel = const MethodChannel("com.wechant.app.plugins/flutter_to_native");

  //打开相册
  static Future<String> openAlbum(int count) async{
    final String result = await _channel.invokeMethod("openAlbum", count);
    return result;
  }

  //开始录制视频
  static Future<String> startRecord() async{
    final String result = await _channel.invokeMethod("startRecord");
    return result;
  }
}