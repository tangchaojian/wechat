import 'package:flutter/material.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import 'package:wechat/utils/screen_utils.dart';

class DouYinDemo extends StatefulWidget {
  @override
  _DouYinDemo createState() => _DouYinDemo();
}

class _DouYinDemo extends State<DouYinDemo> {
  PageController _scrollController = PageController();
  RefreshController _refreshController =
      RefreshController(initialRefresh: false);

  List<String> imageList = [
    "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=112959228,1349518836&fm=26&gp=0.jpg",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2F2a.zol-img.com.cn%2Fproduct%2F2_800x600%2F14%2FceKQr6D0BYvSI.jpg&refer=http%3A%2F%2F2a.zol-img.com.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1612417946&t=2a5bd51ec9ebd92f795cb532fd9bd25d",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.daimg.com%2Fuploads%2Fallimg%2F190806%2F1-1ZP6161Q4.jpg&refer=http%3A%2F%2Fimg.daimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1612418021&t=8b391343f90a7f56d010c005208af89d",
    "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3496248834,3240935515&fm=26&gp=0.jpg",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.redocn.com%2F201904%2F20190409%2F20190409_2090c31a05c9abf55c35uLgFgoxilHtn.jpg&refer=http%3A%2F%2Fimg.redocn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1612418021&t=657f9614253bfc65ab0f2064a33a728e",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.sccnn.com%2Fbimg%2F339%2F21613.jpg&refer=http%3A%2F%2Fimg.sccnn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1612418021&t=a70efd14d8a508bc3f2012423790c8c9",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg4.333cn.com%2Fimg333cn%2F2017%2F12%2F04%2F1512377842390.jpg&refer=http%3A%2F%2Fimg4.333cn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1612418021&t=f4a21e8941b7ecf8a8f4a98a3ba35550",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpic18.nipic.com%2F20120203%2F589292_102352034336_2.jpg&refer=http%3A%2F%2Fpic18.nipic.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1612418021&t=70b922ce80a990287bde194e8174df85",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.sccnn.com%2Fbimg%2F341%2F14070.jpg&refer=http%3A%2F%2Fimg.sccnn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1612418021&t=83bb85d12673283ab2615c683299040a",
    "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201506%2F15%2F20150615232205_MasiV.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1612418021&t=a16ea9710b0d840d754d71abdb5bf784",
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SizedBox(
        width: double.infinity,
        height: double.infinity,
        child: Stack(
          children: [
            PageView.builder(
              scrollDirection: Axis.vertical,
              controller: _scrollController,
              physics: ClampingScrollPhysics(),
              itemCount: 10,
              itemBuilder: (BuildContext context, int index) {
                return SizedBox(
                  width: double.infinity,
                  height: double.infinity,
                  child: Stack(
                    children: [
                      Align(
                        alignment: Alignment.center,
                        child: Image.network(
                          imageList[index],
                          fit: BoxFit.cover,
                        ),
                      )
                    ],
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}
