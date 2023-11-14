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
    return const View5();
  }
}

class View1 extends StatelessWidget {
  const View1({super.key});

  @override
  Widget build(BuildContext context) {
    return GridView.count(
      physics: const BouncingScrollPhysics(),
      crossAxisCount: 4, // 每行显示4个
      children: List.generate(
          100,
          (index) => Container(
                color: Colors.grey[index % 6 * 100],
                child: Text("$index"),
              )),
    );
  }
}

class View2 extends StatelessWidget {
  const View2({super.key});

  @override
  Widget build(BuildContext context) {
    return GridView.extent(
      physics: const BouncingScrollPhysics(),
      maxCrossAxisExtent: 50, // 最大宽度是50 根据屏幕宽度不同每行的个数也不同
      children: List.generate(
          100,
          (index) => Container(
                color: Colors.grey[index % 6 * 100],
                child: Text("$index"),
              )),
    );
  }
}

class View3 extends StatelessWidget {
  const View3({super.key});

  @override
  Widget build(BuildContext context) {
    return GridView(
      // 等同于GridView.count
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 4),
      children: List.generate(
          100,
          (index) => Container(
                color: Colors.grey[index % 6 * 100],
                child: Text("$index"),
              )),
    );
  }
}

class View4 extends StatelessWidget {
  const View4({super.key});

  @override
  Widget build(BuildContext context) {
    return GridView(
      // 等同于GridView.extent
      gridDelegate: const SliverGridDelegateWithMaxCrossAxisExtent(maxCrossAxisExtent: 50),
      children: List.generate(
          100,
          (index) => Container(
                color: Colors.grey[index % 6 * 100],
                child: Text("$index"),
              )),
    );
  }
}

class View5 extends StatelessWidget {
  const View5({super.key});

  @override
  Widget build(BuildContext context) {
    return GridView.builder(
        physics: const BouncingScrollPhysics(),
        itemCount: 100,
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 4, // 每行显示4个
          mainAxisSpacing: 10, // 主轴间距
          crossAxisSpacing: 10, // 侧轴间距
          childAspectRatio: 9.0 / 16, // 设置宽高比
        ),
        itemBuilder: (context, index) {
          return Container(
            color: Colors.grey[index % 6 * 100],
            child: Text("$index"),
          );
        });
  }
}
