import 'package:flutter/material.dart';
import 'package:wechat/community/CommunityPage.dart';
import 'package:wechat/find/FindPage.dart';
import 'package:wechat/news/NewsPage.dart';
import 'package:wechat/personal/ProfilePage.dart';

class App extends StatefulWidget {

  @override
  _AppState createState() => _AppState();
}

class _AppState extends State<App>{

  int position = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text("微信"),
          elevation: 0,
        ),
        body: Center(
          child: IndexedStack(
            index: position,
            children: [
              NewsPage(),
              CommunityPage(),
              FindPage(),
              ProfilePage(),
            ],
          ),
        ),
        bottomNavigationBar: BottomNavigationBar(
          currentIndex: this.position,
          type: BottomNavigationBarType.fixed,
          selectedFontSize: 12,
          unselectedFontSize: 12,
          fixedColor: Color(0xff63ca6c),
          selectedLabelStyle: TextStyle(color: Color(0xff63ca6c)),
          unselectedLabelStyle: TextStyle(color: Color(0xFF969696)),
          items: [
            BottomNavigationBarItem(
              label: "资讯",
              icon: getNavItemIcon(0),),
            BottomNavigationBarItem(
              label: "社区",
              icon: getNavItemIcon(1),),

            BottomNavigationBarItem(
              label: "发现",
              icon: getNavItemIcon(2),),

            BottomNavigationBarItem(
              label: "我的",
              icon: getNavItemIcon(3),),
          ],
          onTap: (index){
            setState(() {
              this.position = index;
            });
          },
        ),
    );
  }

  Text getNavBarItemText(int index, String title){
      return index == position ? Text(title, style: TextStyle(color: Color(0xff63ca6c),)) : Text(title, style: TextStyle(color: Color(0xFF969696),),);
  }

  Image getNavItemIcon(int index) {
    if(index == 0){
      return Image.asset(
        index == this.position ? "images/ic_nav_news_actived.png" : "images/ic_nav_news_normal.png",
        width: 20,
        height: 20,
      );
    }else if(index == 1){
      return Image.asset(
        index == this.position ? "images/ic_nav_community_actived.png" : "images/ic_nav_community_normal.png",
        width: 20,
        height: 20,
      );
    }else if(index == 2){
      return Image.asset(
        index == this.position ? "images/ic_nav_discover_actived.png" : "images/ic_nav_discover_normal.png",
        width: 20,
        height: 20,
      );
    }else{
      return Image.asset(
        index == this.position ? "images/ic_nav_profile_actived.png" : "images/ic_nav_profile_normal.png",
        width: 20,
        height: 20,
      );
    }
  }

}