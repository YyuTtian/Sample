import 'package:flutter/material.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Material App',
      home: Scaffold(
        body: Test(),
      ),
    );
  }
}

class Test extends StatefulWidget {
  const Test({super.key});

  @override
  State<Test> createState() => _TestState();
}

class _TestState extends State<Test> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(vsync: this, length: 10);
  }

  @override
  void dispose() {
    super.dispose();
    _tabController.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return NestedScrollView(
      headerSliverBuilder: (BuildContext context, bool innerBoxIsScrolled) {
        return <Widget>[
          SliverAppBar(
            backgroundColor: Colors.red,
            foregroundColor: Colors.red,
            title: Text("SliverAppBar"),
            leading: BackButton(),
            actions: [CloseButton()],
            toolbarHeight: 50,
            expandedHeight: 200,
            pinned: true,
            flexibleSpace: Container(
              color: Colors.yellow,
            ),
            // bottom: PreferredSize(
            //   // Add this code
            //   preferredSize: Size.fromHeight(20.0), // Add this code
            //   child: Text(''), // Add this code
            // ),
          ),
          SliverPersistentHeader(
            pinned: true,
            delegate: StickyTabBarDelegate(
              child: TabBar(
                isScrollable: true,
                tabAlignment: TabAlignment.start,
                labelColor: Colors.black,
                controller: this._tabController,
                tabs: <Widget>[
                  Tab(text: '资讯'),
                  Tab(text: '技术'),
                  Tab(text: '资讯'),
                  Tab(text: '技术'),
                  Tab(text: '资讯'),
                  Tab(text: '技术'),
                  Tab(text: '资讯'),
                  Tab(text: '技术'),
                  Tab(text: '资讯'),
                  Tab(text: '技术'),
                ],
              ),
            ),
          ),
        ];
      },
      body: TabBarView(
        controller: this._tabController,
        children: <Widget>[
          for (int i = 0; i < 10; i++)
            Container(
              color: i % 2 == 0 ? Colors.red : Colors.blue,
            )
        ],
      ),
    );
  }
}

class StickyTabBarDelegate extends SliverPersistentHeaderDelegate {
  final TabBar child;

  StickyTabBarDelegate({required this.child});

  @override
  Widget build(BuildContext context, double shrinkOffset, bool overlapsContent) {
    return Container(
      color: Theme.of(context).backgroundColor,
      child: this.child,
    );
  }

  @override
  double get maxExtent => this.child.preferredSize.height;

  @override
  double get minExtent => this.child.preferredSize.height;

  @override
  bool shouldRebuild(SliverPersistentHeaderDelegate oldDelegate) {
    return true;
  }
}
