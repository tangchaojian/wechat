import 'package:flutter/material.dart';
import 'package:wechat/mall/CartListPage.dart';

class NewsPage extends StatefulWidget {

  @override
  _NewsPage createState() => _NewsPage();
}

class _NewsPage extends State<NewsPage> {
  @override
  Widget build(BuildContext context) {
    return Container(
        child: Center(
          child: FlatButton(
            textColor: Colors.white,
            color: Colors.blue,
            highlightColor: Colors.blueAccent,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(5)),
            child: Text("跳转购物车", style: TextStyle(fontSize: 14),),
            onPressed: (){
                // Navigator.push(context, MaterialPageRoute(builder: (BuildContext context){
                //   return CartListPage();
                // }));
              Navigator.pushNamed(context, "/cart");
            },
          ),
        ),
    );
  }
}