import 'package:flutter/material.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Material App',
      home: Scaffold(
        body: NestedScrollView(
          headerSliverBuilder: (BuildContext context, bool innerBoxIsScrolled) {
            return <Widget>[
              // SliverAppBar(
              //   title: Text('ZaLou.Cn'),
              // )

              // SliverToBoxAdapter(
              //   child: Container(
              //     height: 300,
              //     color: Colors.red,
              //   ),
              // ),

              const SliverAppBar(
                // pinned: true, // 钉住
                floating: true, // 上滑跟着走  下滑一点就出来
                title: Text("SliverAppBar"),
                leading: BackButton(),
                actions: [CloseButton()],
              ),
            ];
          },
          body: CustomTabPage(),
        ),
      ),
    );
  }
}

class CustomTabPage extends StatefulWidget {
  const CustomTabPage({super.key});

  @override
  State<CustomTabPage> createState() => _CustomTabPageState();
}

class _CustomTabPageState extends State<CustomTabPage> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  late PageController _pageController;
  List<String> _titleList = <String>['关注', '推荐', '抗疫', '热榜', '精品课', '旅游', '关注', '推荐', '抗疫', '热榜', '精品课', '旅游'];

  @override
  void initState() {
    super.initState();
    _tabController = TabController(vsync: this, length: _titleList.length);
    _pageController = PageController();
  }

  void _changeTab(int index) {
    _pageController.animateToPage(index, duration: const Duration(milliseconds: 300), curve: Curves.ease);
  }

  void _onPageChanged(int index) {
    _tabController.animateTo(index, duration: const Duration(milliseconds: 300));
  }

  @override
  void dispose() {
    _pageController.dispose();
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: <Widget>[
        Container(
          width: double.infinity,
          color: Colors.green,
          padding: EdgeInsets.symmetric(vertical: 10),
          height: 60,
          child: TabBar(
            tabAlignment: TabAlignment.start,
            labelPadding: EdgeInsets.only(left: 1, right: 1),
            dividerColor: Colors.transparent,
            dividerHeight: 0,
            labelColor: Colors.blue,
            //选中的颜色
            labelStyle: TextStyle(color: Colors.blue, fontSize: 14),
            unselectedLabelColor: Colors.black54,
            //未选中的颜色
            unselectedLabelStyle: TextStyle(color: Colors.black54, fontSize: 14),
            isScrollable: true,
            //自定义indicator样式
            indicator: BoxDecoration(color: Colors.red, borderRadius: BorderRadius.all(Radius.circular(5))),
            controller: _tabController,
            onTap: _changeTab,
            tabs: _titleList
                .map((e) => Padding(
                      padding: const EdgeInsets.only(left: 10, right: 10),
                      child: Tab(text: e),
                    ))
                .toList(),
          ),
        ),
        Expanded(
            child: PageView.builder(
                physics: BouncingScrollPhysics(),
                controller: _pageController,
                onPageChanged: _onPageChanged,
                itemCount: _titleList.length,
                itemBuilder: (context, index) {
                  return PageItemView(index: index);
                }))
      ],
    );
  }
}

class PageItemView extends StatelessWidget {
  var index = 0;

  PageItemView({super.key, required this.index});

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.black,
      child: ListView.builder(
          padding: EdgeInsets.zero,
          itemCount: 100,
          itemBuilder: (context, index) {
            return Text(
              "第$index个",
              style: TextStyle(color: Colors.white),
            );
          }),
    );
  }
}
