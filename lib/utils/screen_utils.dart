import 'package:flutter/material.dart';

class ScreenUtils {

  static List<double> getScreen(BuildContext context) {
    final size =MediaQuery.of(context).size;
    final width =size.width;
    final heith =size.height;

    return [width, heith];
  }

  static double getStatusBarHeight(BuildContext context) {
    return MediaQuery.of(context).padding.top;
  }
}