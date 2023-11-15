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

class View1 extends StatelessWidget {
  const View1({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      // AbsorbPointer吸收触摸点击事件  不会传递给其他组件  达到禁用的效果
      child: AbsorbPointer(
        absorbing: false, // 设置为false 按钮又可以点击了
        child: ElevatedButton(
          onPressed: () {
            print("按钮点击事件");
          },
          child: Text("禁用的按钮"),
        ),
      ),
    );
  }
}
