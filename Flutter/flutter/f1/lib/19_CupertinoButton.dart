import 'package:flutter/cupertino.dart';
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
    return const Center(
      child: Button2(),
    );
  }
}

class Button1 extends StatelessWidget {
  const Button1({super.key});

  @override
  Widget build(BuildContext context) {
    return CupertinoButton(
      child: const Text("没有背景"),
      onPressed: () {},
    );
  }
}

class Button2 extends StatelessWidget {
  const Button2({super.key});

  @override
  Widget build(BuildContext context) {
    // 没有长按事件
    return CupertinoButton.filled(
      borderRadius: BorderRadius.circular(20),
      child: const Text("有背景"),
      onPressed: () {},
    );
  }
}
