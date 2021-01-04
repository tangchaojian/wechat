import 'package:flutter/material.dart';
import 'package:wechat/utils/screen_utils.dart';
import 'package:wechat/widgets/UIBannerView.dart';

class UITabBarViewDemo2 extends StatefulWidget {
  @override
  _UITabBarViewDemo2 createState() => _UITabBarViewDemo2();
}

class _UITabBarViewDemo2 extends State<UITabBarViewDemo2> with SingleTickerProviderStateMixin{

  List<String> _bannerList = [
    "https://gw.alicdn.com/tfs/TB17emto4n1gK0jSZKPXXXvUXXa-1035-390.png_790x10000.jpg_.webp",
    "https://img.alicdn.com/tps/i4///img.alicdn.com/imgextra/i4/6000000006436/O1CN01APiEL01xPjinBVB2H_!!6000000006436-0-octopus.jpg_790x10000Q75.jpg_.webp",
    "https://gw.alicdn.com/imgextra/i2/1350990/O1CN01KcaNeF1JBSkNjWqId_!!1350990-0-lubanu.jpg_790x10000Q75.jpg_.webp",
    "https://img.alicdn.com/tps/i4///img.alicdn.com/imgextra/i3/6000000001865/O1CN01fa8m0G1PeDIs2LPXS_!!6000000001865-0-octopus.jpg_790x10000Q75.jpg_.webp",
  ];

  double bannerWidth = 0;
  double bannerHeight = 0;
  double statusBarHeight = 0;

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
    statusBarHeight = MediaQuery.of(context).padding.top;

    return Scaffold(
      body: CustomScrollView(
        slivers: [
          SliverAppBar(
            pinned: true,
            expandedHeight: bannerHeight - statusBarHeight,
            flexibleSpace: FlexibleSpaceBar(
              background: _buildBannerView(),
            ),
          ),
          SliverFixedExtentList(
              itemExtent: 50.0,
              delegate: SliverChildBuilderDelegate(
                  (BuildContext context, int index){
                    return ListTile(
                      title: Text('ITEM $index'),
                    );
                  },
                childCount: 30,
              ),
          )
        ],
      ),
    );
  }

  Widget _buildBannerView(){
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
                            margin: EdgeInsets.only(
                                left: 2.5, right: 2.5),
                            decoration: BoxDecoration(
                                color: Colors.blueAccent,
                                borderRadius:
                                BorderRadius.circular(5)),
                          );
                        } else {
                          return Container(
                            width: 5,
                            height: 5,
                            margin: EdgeInsets.only(
                                left: 2.5, right: 2.5),
                            decoration: BoxDecoration(
                                color: Colors.white,
                                borderRadius:
                                BorderRadius.circular(5)),
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
        ],
      ),
    );
  }

  Widget _buildListView(int index){
    return ListView.separated(
      itemCount: 30,
      separatorBuilder: (context, index) => Divider(color: Colors.grey, height: 1,),
      itemBuilder: (context, index){
        return ListTile(
          tileColor: Colors.white,
          title: Text('ITEM $index'),
        );
      },
    );
  }
}
