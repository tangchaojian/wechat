import 'dart:ffi';

import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_easyrefresh/easy_refresh.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import 'package:wechat/entity/GoodsEntity.dart';
import 'package:wechat/entity/ResponseEntity.dart';
import 'package:wechat/entity/goods_list_data_entity.dart';
import 'package:wechat/net/http_callback.dart';
import 'package:wechat/net/http_helper.dart';
import 'package:wechat/net/http_status.dart';
import 'dart:convert' as convert;

import 'package:wechat/utils/log.dart';

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
    // HttpHelper.get("https://looyu.vip/goods/open-app/goods/queryList", params: {"pageNum":page, "pageSize":3});

    // String jsonStr = "{\"code\":1, \"msg\":\"\", \"data\":{\"goodsName\":\"华为P30 Pro手机【保价15天】智能手机 天空之境 8G+512G 全网通\"}}";
    // String jsonStr = "{\"code\":1, \"msg\":\"\", \"data\":[{\"goodsName\":\"华为P30 Pro手机【保价15天】智能手机 天空之境 8G+512G 全网通\"},{\"goodsName\":\"南极人NanJiren 被子 春秋冬单人加厚被芯150*200cm 5斤学生空调盖被褥四季被子\"}]}";
    // var json = convert.jsonDecode(jsonStr);

    // ResponseEntity<GoodsEntity> response = ResponseEntity.fromJson(json);
    // Log.i("TAG", "goodsName->" + response.data.goodsName);

    // ResponseEntity<List<GoodsEntity>> response = ResponseEntity.fromJson(json);
    // Log.i("TAG", "goodsName->" + response.data[1].goodsName);
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
    HttpHelper.get("https://looyu.vip/goods/open-app/goods/queryList", params: {"pageNum":page, "pageSize":limit}, callback: HttpCallback<GoodsListDataEntity>(
      onCallback: (status, code, message, {data}) {
        if(status == HttpStatus.SUCCESS) {
          if(page == 1) {
            list.clear();
          }
          List<GoodsEntity> array = data.list;
          setState(() {
            this.list.addAll(array);
          });
        }else if(status == HttpStatus.FAIL) {

        }else if(status == HttpStatus.COMPLETE) {
          List<GoodsEntity> array = data.list ?? List();
          if(page == 1) {
            if(array.length < limit) {
              _controller.finishLoad(noMore: true);
            }else {
              setState(() {
                hasMore = true;
              });
              _controller.finishLoad(noMore: false);
            }

            _controller.resetLoadState();
          }else {
            if (array.length < limit) {
              _controller.finishLoad(noMore: true);
            }
          }
        }
      }
    ));
  }
}
