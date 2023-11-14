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
  PageController controller = PageController();

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return PageView.builder(
        // scrollDirection: Axis.vertical, // 设置方向
        controller: controller,

        // controller.jumpToPage(5)
        // controller.animateToPage(5, duration: Duration(seconds: 1), curve: Curves.easeOut);
        // controller.previousPage
        // controller.nextPage

        onPageChanged: (index) {
          print("第$index页");
        },
        itemCount: 10,
        itemBuilder: (context, index) {
          return Center(child: Text("第${index}页"));
        });
  }
}
