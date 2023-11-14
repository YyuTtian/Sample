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
    return const View1();
  }
}

class View1 extends StatefulWidget {
  const View1({super.key});

  @override
  State<View1> createState() => _View1State();
}

class _View1State extends State<View1> {
  List list = ["第0个"];

  @override
  Widget build(BuildContext context) {
    return RefreshIndicator(
        child: ListView.builder(
            physics: const AlwaysScrollableScrollPhysics(), // 当itemCount太小 不够一屏的时候 设置永远可以滑动
            itemCount: list.length,
            itemBuilder: (context, index) {
              return Text("${list[index]}");
            }),
        onRefresh: () async {
          await Future.delayed(const Duration(seconds: 2));
          setState(() {
            list.add("第${list.length}个");
          });
        });
  }
}
