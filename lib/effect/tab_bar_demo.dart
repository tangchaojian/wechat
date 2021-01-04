import 'package:flutter/material.dart';
import 'package:wechat/utils/screen_utils.dart';
import 'package:wechat/widgets/UIBannerView.dart';

class UITabBarViewDemo extends StatefulWidget {
  @override
  _UITabBarViewDemo createState() => _UITabBarViewDemo();
}

class _UITabBarViewDemo extends State<UITabBarViewDemo>
    with SingleTickerProviderStateMixin {
  List<String> _bannerList = [
    "https://gw.alicdn.com/tfs/TB17emto4n1gK0jSZKPXXXvUXXa-1035-390.png_790x10000.jpg_.webp",
    "https://img.alicdn.com/tps/i4///img.alicdn.com/imgextra/i4/6000000006436/O1CN01APiEL01xPjinBVB2H_!!6000000006436-0-octopus.jpg_790x10000Q75.jpg_.webp",
    "https://gw.alicdn.com/imgextra/i2/1350990/O1CN01KcaNeF1JBSkNjWqId_!!1350990-0-lubanu.jpg_790x10000Q75.jpg_.webp",
    "https://img.alicdn.com/tps/i4///img.alicdn.com/imgextra/i3/6000000001865/O1CN01fa8m0G1PeDIs2LPXS_!!6000000001865-0-octopus.jpg_790x10000Q75.jpg_.webp",
  ];

  List<String> _menus = [
    "签到",
    "商城",
    "新品",
    "优惠券",
    "消息",
    "购物车",
    "热度",
    "榜单",
    "视频",
    "会员",
  ];

  List<String> _menus_icon_uri = [
    "images/img_menu_sign.png",
    "images/img_menu_mall.png",
    "images/img_menu_new_product.png",
    "images/img_menu_coupon.png",
    "images/img_menu_message.png",
    "images/img_menu_cart.png",
    "images/img_menu_hot.png",
    "images/img_menu_list.png",
    "images/img_menu_video.png",
    "images/img_menu_member.png",
  ];

  List<String> _tabNams = ["推荐", "手机", "美妆"];

  double bannerWidth = 0;
  double bannerHeight = 0;

  int current = 0;

  ScrollController _scrollController;
  TabController _tabController;

  @override
  void initState() {
    super.initState();
    _scrollController = ScrollController(initialScrollOffset: 0.0);
    _tabController = TabController(vsync: this, length: 3);
  }

  @override
  void dispose() {
    super.dispose();
    _scrollController.dispose();
    _tabController.dispose();
  }

  @override
  Widget build(BuildContext context) {
    bannerWidth = ScreenUtils.getScreen(context)[0];
    bannerHeight = bannerWidth * 298 / 790;

    return Scaffold(
      body: SafeArea(
        child: NestedScrollView(
          controller: _scrollController,
          scrollDirection: Axis.vertical,
          headerSliverBuilder: (context, innerBoxIsScrolled) {
            return <Widget>[
              SliverAppBar(
                title: Text("吸顶效果"),
                centerTitle: true,
                pinned: true,
                floating: false,
                snap: false,
                expandedHeight: 325,
                flexibleSpace: FlexibleSpaceBar(
                  // 背景 固定到位，直到达到最小范围。 默认是CollapseMode.parallax(将以视差方式滚动。)，还有一个是none，滚动没有效果
                  collapseMode: CollapseMode.pin,
                  background: _buildBannerView(),
                ),
                bottom: TabBar(
                  controller: _tabController,
                  tabs: _tabNams.map((e) {
                    return Tab(text: e.toString());
                  }).toList(),
                ),
              ),
            ];
          },
          body: TabBarView(
            controller: _tabController,
            children: [
              _buildListView(0),
              _buildListView(1),
              _buildListView(2),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildBannerView() {
    return Container(
      height: double.infinity,
      child: Column(
        children: [
          Container(
            width: double.infinity,
            height: bannerHeight,
            child: Stack(
              children: [
                UIBannerView(
                  data: _bannerList,
                  delayTime: 3,
                  buildShowView: (index, itemData) {
                    return Image.network(
                      itemData,
                      fit: BoxFit.cover,
                    );
                  },
                  onPageChangeListener: (index, itemData) {
                    setState(() {
                      current = index;
                    });
                  },
                ),
                Align(
                  alignment: Alignment.bottomCenter,
                  child: Container(
                    height: 20,
                    margin: EdgeInsets.only(bottom: 5),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: _bannerList.map((item) {
                        int index = _bannerList.indexOf(item);
                        if (current == index) {
                          return Container(
                            width: 10,
                            height: 5,
                            margin: EdgeInsets.only(left: 2.5, right: 2.5),
                            decoration: BoxDecoration(
                                color: Colors.blueAccent,
                                borderRadius: BorderRadius.circular(5)),
                          );
                        } else {
                          return Container(
                            width: 5,
                            height: 5,
                            margin: EdgeInsets.only(left: 2.5, right: 2.5),
                            decoration: BoxDecoration(
                                color: Colors.white,
                                borderRadius: BorderRadius.circular(5)),
                          );
                        }
                        return Text("索引");
                      }).toList(),
                    ),
                  ),
                )
              ],
            ),
          ),
          Expanded(
            flex: 1,
            child: Padding(
              padding: EdgeInsets.only(left: 10, top: 10, right: 10),
              child: GridView.builder(
                gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisSpacing: 10,
                  mainAxisSpacing: 10,
                  crossAxisCount: 5,
                  childAspectRatio: 1,
                ),
                itemBuilder: (context, index) {
                  return _getMenuItemView(index);
                },
                itemCount: _menus.length,
              ),
            )
          ),
        ],
      ),
    );
  }

  Widget _getMenuItemView(int index) {
    return Container(
      child: Column(
        children: [
          Image.asset(
            _menus_icon_uri[index],
            width: 40,
            height: 40,
          ),
          Text(
            _menus[index],
            style: TextStyle(
              fontSize: 14,
              color: Color(0xFF333333),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildListView(int index) {
    return ListView.separated(
      itemCount: 30,
      separatorBuilder: (context, index) => Divider(
        color: Colors.grey,
        height: 1,
      ),
      itemBuilder: (context, index) {
        return ListTile(
          tileColor: Colors.white,
          title: Text('ITEM $index'),
        );
      },
    );
  }
}
