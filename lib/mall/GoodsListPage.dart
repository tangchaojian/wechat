import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import 'package:wechat/entity/GoodsEntity.dart';

class GoodsListPage extends StatefulWidget {
  @override
  _GoodsListPage createState() => _GoodsListPage();
}

class _GoodsListPage extends State<GoodsListPage> {

  List<GoodsEntity> list = new List();
  var dio = Dio();
  int page = 1;
  int limit = 3;
  var _refreshController = RefreshController(initialRefresh: false);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("商品列表"),
        elevation: 0,
      ),
      body: SmartRefresher(
        enablePullDown: true,
        enablePullUp: true,
        header: WaterDropHeader(),
        footer: CustomFooter(
          builder: (BuildContext context, LoadStatus mode) {
            Widget body;
            if(mode == LoadStatus.idle) {
              body = Text("上拉加载");
            }else if(mode == LoadStatus.loading) {
              body = CupertinoActivityIndicator();
            }else if(mode == LoadStatus.canLoading) {
              body = Text("松手，加载更多!");
            }else {
              body = Text("没有更多数据了!");
            }
            return Container(
              height: 55.0,
              child: Center(child: body,),
            );
          },
        ),
        controller: _refreshController,
        onRefresh: refresh,
        onLoading: loadMore,
        child: ListView.builder(
          itemBuilder: (context, index) => Text(list[index].goodsName),
          itemExtent: 100.0,
          itemCount: list.length,
        ),
      ),
    );
  }

  void refresh(){
    page = 1;
    request();
  }

  void loadMore(){
    page++;
    request();
  }

  void request() async{
    Response response = await dio.get("https://looyu.vip/goods/open-app/goods/queryList", queryParameters: {"pageNum":page, "pageSize":limit});
    int code = response.data["code"];
    if(code == 200) {
      var data = response.data["data"];
      if(data != null) {

        if(page == 1) {
          list.clear();
        }

        List array = data["list"];
        List<GoodsEntity> _list = List();
        array.forEach((entity) {
          GoodsEntity item = new GoodsEntity();
          item.goodsName = entity["goodsName"];
          _list.add(item);
        });

        setState(() {
          this.list.addAll(_list);
        });
      }
    }

    if(page == 1) {
      _refreshController.refreshCompleted();
    }else {
      _refreshController.loadComplete();
    }

  }
}
