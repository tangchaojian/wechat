import 'package:flutter/material.dart';

class UISliverAppBarDemo extends StatefulWidget {

  @override
  _UISliverAppBarDemo createState()=> _UISliverAppBarDemo();
}

class _UISliverAppBarDemo extends State<UISliverAppBarDemo> {

  @override
  Widget build(BuildContext context) {

    return NestedScrollView(
        headerSliverBuilder: (BuildContext context, bool innerBoxIsScrolled) {
          return <Widget>[
            SliverOverlapAbsorber(
              handle: NestedScrollView.sliverOverlapAbsorberHandleFor(context),
              sliver: SliverAppBar(
                leading: new IconButton(
                    icon: Icon(Icons.arrow_back),
                    onPressed: ()=> {

                    },
                ),
                title: Text("标题"),
                centerTitle: false,
                pinned: false,
                floating: false,
                snap: false,
                primary: false,
                expandedHeight: 230.0,
                elevation: 10,
                //是否显示阴影，直接取值innerBoxIsScrolled，展开不显示阴影，合并后会显示
                forceElevated: innerBoxIsScrolled,

                actions: [

                  IconButton(
                      icon: Icon(Icons.add),
                      onPressed: null
                  ),

                  IconButton(
                      icon: Icon(Icons.more_horiz),
                      onPressed: null
                  ),
                ],

                flexibleSpace: FlexibleSpaceBar(
                  background: Image.asset("images/img_dlrb"),
                ),

              ),
            ),
          ];
        },
        body: SafeArea(
          top: false,
          bottom: false,
          child: Builder(
            builder: (BuildContext context){
              return CustomScrollView(
                slivers: [
                  SliverOverlapInjector(
                    handle: NestedScrollView.sliverOverlapAbsorberHandleFor(context),
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
                            }
                        ),
                      ),
                  ),
                ],
              );
            },
          ),
        ),
    );
  }
}