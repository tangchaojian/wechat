import 'dart:ffi';

import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_easyrefresh/easy_refresh.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import 'package:wechat/entity/GoodsEntity.dart';
import 'package:wechat/net/http_helper.dart';

class GoodsListPage2 extends StatefulWidget {
  @override
  _GoodsListPage2 createState() => _GoodsListPage2();
}

class _GoodsListPage2 extends State<GoodsListPage2> {

  List<GoodsEntity> list = new List();
  var dio = Dio();
  int page = 1;
  int limit = 20;
  bool hasMore = false;
  var _controller = EasyRefreshController();

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    // refresh();
    HttpHelper.get("https://looyu.vip/goods/open-app/goods/queryList", params: {"pageNum":page, "pageSize":3});
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("商品列表"),
        elevation: 0,
      ),
      body: Padding(
        padding: EdgeInsets.only(left: 10, right: 10),
        child: EasyRefresh.custom(
          enableControlFinishRefresh: false,
          enableControlFinishLoad: true,
          controller: _controller,
          header: ClassicalHeader(),
          footer: ClassicalFooter(),
          onRefresh: refresh,
          onLoad: hasMore ? loadMore : null,
          slivers: [
            // SliverList(
            //   delegate: SliverChildBuilderDelegate((context, index) {
            //       return Container(
            //         width: 60.0,
            //         height: 60.0,
            //         child: Center(
            //           child: Text('$index'),
            //         ),
            //         color:
            //         index % 2 == 0 ? Colors.grey[300] : Colors.transparent,
            //       );
            //     },
            //     childCount: list.length,
            //   ),
            // ),
            SliverGrid(
                delegate: SliverChildBuilderDelegate(
                        (BuildContext context, int index) {
                      return Container(
                        color: Colors.primaries[(index % 18)],
                      );
                    },
                    childCount: list.length
                ),
                gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2,
                  crossAxisSpacing: 10,
                  mainAxisSpacing: 10,
                  childAspectRatio: 3/4,
                )
            )
          ],
        ),
      ),
    );
  }

  Future<void> refresh() async{
    page = 1;
    _controller.finishLoad(noMore: true);
    request();
  }

  Future<void> loadMore() async{
    page++;
    request();
  }

  void request() async{
    Response response = await dio.get("https://looyu.vip/goods/open-app/goods/queryList", queryParameters: {"pageNum":page, "pageSize":limit});
    int code = response.data["code"];
    List<GoodsEntity> _list = List();

    if(code == 200) {
      var data = response.data["data"];
      if(data != null) {

        if(page == 1) {
          list.clear();
        }

        List array = data["list"];
        array.forEach((entity) {
          GoodsEntity item = new GoodsEntity();
          item.goodsName = entity["goodsName"];
          _list.add(item);
        });
      }
    }

    setState(() {
      this.list.addAll(_list);
    });

    if(page == 1) {
      if(_list.length < limit) {
        _controller.finishLoad(noMore: true);
      }else {
        setState(() {
          hasMore = true;
        });
        _controller.finishLoad(noMore: false);
      }

      _controller.resetLoadState();
    }else {
      if(_list.length < limit) {
        _controller.finishLoad(noMore: true);
      }
    }


  }
}
