import 'dart:async';

import 'package:flutter/material.dart';
import 'package:wechat/utils/screen_utils.dart';

class FloatButtonDemo extends StatefulWidget {
  @override
  _FloatButtonDemo createState() => _FloatButtonDemo();
}

class _FloatButtonDemo extends State<FloatButtonDemo> with SingleTickerProviderStateMixin {
  double right = 10.0;
  double bottom = 80.0;
  double maxY = 0.0;
  bool isBegin = false;

  AnimationController controller;
  Animation<double> animation;

  Timer timer;

  @override
  void initState() {
    super.initState();

    controller = new AnimationController(duration: const Duration(milliseconds: 500), vsync: this);
    animation = new Tween(begin: 10.0, end: -35.0).animate(controller);
    animation.addStatusListener((status) {

    });
    animation.addListener(() {
      setState(() {});
    });
  }

  @override
  void dispose() {
    if(timer != null && timer.isActive){
      timer.cancel();
    }
    isBegin = false;
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    maxY = ScreenUtils.getScreen(context)[1] - ScreenUtils.getStatusBarHeight(context) - 160.0;

    return Scaffold(
      appBar: AppBar(
        title: Text("悬浮拖动按钮"),
      ),
      body: SizedBox(
        width: double.infinity,
        height: double.infinity,
        child: Stack(
          children: [
            NotificationListener(
              onNotification: (Notification notification) {
                if (notification is ScrollUpdateNotification) {
                  if(!isBegin) {
                    isBegin = true;
                    controller.forward();
                    print("正在滚动");
                  }
                  timer?.cancel();
                } else if (notification is ScrollEndNotification) {
                  print("结束滚动");
                  timer = Timer(Duration(milliseconds: 500), () {
                    print("执行完毕");
                    controller.reverse();
                    isBegin = false;
                  });
                }
                return true;
              },
              child: ListView.builder(
                  itemCount: 1000,
                  itemBuilder: (context, index) {
                    return ListTile(
                      title: Text('ITEM $index'),
                    );
                  }),
            ),
            Positioned(
              right: animation.value,
              bottom: bottom,
              child: GestureDetector(
                onVerticalDragUpdate: _dragEvent,
                child: SizedBox(
                  width: 60,
                  height: 60,
                  child: Stack(
                    children: [
                      Image.asset(
                        "images/img_float_button.gif",
                        width: 60,
                        height: 60,
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _dragEvent(DragUpdateDetails details) {
    double y = details.delta.dy;
    double _bottom = this.bottom;
    _bottom -= y;
    if (_bottom < 80.0) {
      _bottom = 80.0;
    }

    if (_bottom > maxY) {
      _bottom = maxY;
    }

    setState(() {
      this.bottom = _bottom;
    });
  }
}
