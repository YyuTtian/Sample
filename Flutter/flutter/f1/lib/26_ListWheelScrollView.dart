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
    return Center(
      child: View1(),
    );
  }
}

class View1 extends StatelessWidget {
  View1({super.key});

  FixedExtentScrollController controller = FixedExtentScrollController();

  @override
  Widget build(BuildContext context) {
    return ListWheelScrollView(
      itemExtent: 100,

      // 放大选中元素1.5倍
      // useMagnifier: true,
      // magnification: 1.5,

      // 设置未选中的透明度
      // overAndUnderCenterOpacity: 0.5,

      physics: BouncingScrollPhysics(),

      onSelectedItemChanged: (index) {
        print("index=$index");
      },

      controller: controller,

      // controller.jumpToItem(2) 跳转到第2个
      // controller.selectedItem  获取当前选中的索引

      children: [
        // children里面可以使用if 和 for
        if (true) Container(color: Colors.red),
        for (int i = 0; i < 10; i++) Container(color: Colors.grey)
      ],
    );
  }
}

class View2 extends StatelessWidget {
  const View2({super.key});

  @override
  Widget build(BuildContext context) {
    return ListWheelScrollView(
        itemExtent: 100, children: List.generate(5, (index) => Container(color: Colors.red)));
  }
}
