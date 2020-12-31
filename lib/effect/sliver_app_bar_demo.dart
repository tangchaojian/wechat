import 'package:flutter/material.dart';

class UISliverAppBarDemo extends StatefulWidget {
  @override
  _UISliverAppBarDemo createState() => _UISliverAppBarDemo();
}

class _UISliverAppBarDemo extends State<UISliverAppBarDemo> {
  @override
  Widget build(BuildContext context) {
    var meida = MediaQuery.of(context);
    print('media.padding->${meida.padding}');

    return Scaffold(
      body: SafeArea(
        child:CustomScrollView(
          scrollDirection: Axis.vertical,
          slivers: [
            SliverAppBar(
              leading: new IconButton(
                icon: Icon(Icons.arrow_back),
                onPressed: () => {
                  Navigator.pop(context)
                },
              ),
              title: Text("标题"),
              centerTitle: false,
              pinned: true,
              floating: false,
              snap: false,
              primary: false,
              expandedHeight: 230.0,
              actions: [
                IconButton(icon: Icon(Icons.add), onPressed: ()=> {

                }),
                IconButton(icon: Icon(Icons.more_horiz), onPressed: ()=>{

                }),
              ],
              flexibleSpace: FlexibleSpaceBar(
                background: Image.asset("images/img_dlrb.jpg", fit: BoxFit.cover,),
              ),
            ),
            SliverPadding(
              padding: EdgeInsets.all(10.0),
              sliver: SliverFixedExtentList(
                itemExtent: 50.0,
                delegate: SliverChildBuilderDelegate(
                      (context, index) {
                    return ListTile(
                      title: Text('ITEM $index'),
                    );
                  },
                  childCount: 30,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
