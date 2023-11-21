import 'package:flutter/material.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: Scaffold(body: HomePage()),
    );
  }
}

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return const ListItemState();
  }
}

class ListBuild extends StatelessWidget {
  const ListBuild({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
        itemExtent: 100, // 固定每个item的高度 可以不写
        itemCount: 100,
        itemBuilder: (context, index) {
          return Center(
            child: Text("第$index个"),
          );
        });
  }
}

class Separated extends StatelessWidget {
  const Separated({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView.separated(
        separatorBuilder: (context, index) {
          return Container(
            color: Colors.blue,
            child: const Text('分割线'),
          );
        },
        itemCount: 200,
        itemBuilder: (context, index) {
          return Container(
            height: 50,
            color: Colors.red,
          );
        });
  }
}

class ListItemState extends StatefulWidget {
  const ListItemState({super.key});

  @override
  State<ListItemState> createState() => _ListItemStateState();
}

class _ListItemStateState extends State<ListItemState> {
  ScrollController controller = ScrollController();

  @override
  void initState() {
    super.initState();

    controller.addListener(() {
      print("现在的位置${controller.offset}");
    });
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SizedBox(
          height: 500,
          child: ListView.builder(
            // 列表方向
            // scrollDirection: Axis.horizontal,
            itemExtent: 100,
            itemCount: 50,
            controller: controller,
            // iOS回弹 BouncingScrollPhysics   Android回弹 ClampingScrollPhysics
            physics: const BouncingScrollPhysics(),
            itemBuilder: (context, index) {
              return const CalcCount();
            },
          ),
        ),
        ElevatedButton(
            onPressed: () {
              // 瞬间跳过去
              // controller.jumpTo(0);

              // 动画过去
              controller.animateTo(0, duration: const Duration(milliseconds: 500), curve: Curves.easeOut);
            },
            child: const Text("滑动到顶部"))
      ],
    );
  }
}

class CalcCount extends StatefulWidget {
  const CalcCount({super.key});

  @override
  State<CalcCount> createState() => _CalcCountState();
}

class _CalcCountState extends State<CalcCount> with AutomaticKeepAliveClientMixin {
  int _count = 0;

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return SizedBox(
      width: 200,
      child: Padding(
        padding: const EdgeInsets.all(10),
        child: ElevatedButton(
            onPressed: () {
              setState(() {
                _count++;
              });
            },
            child: Text("按钮点击次数=$_count")),
      ),
    );
  }

  // item移除屏幕外 数据保留  _count不会被重置为0
  @override
  bool get wantKeepAlive => _count != 0;
}
