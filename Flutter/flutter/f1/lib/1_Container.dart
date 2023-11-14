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
    return Container(
      width: 200,
      height: 200,
      margin: const EdgeInsets.all(100),
      padding: const EdgeInsets.only(top: 30),
      // color: const Color(0xffff0000).withOpacity(0.6),
      decoration: BoxDecoration(
        // 圆角
        borderRadius: BorderRadius.circular(20),
        color: Colors.red,
        // 背景色渐变
        // gradient: const LinearGradient(
        //     begin: Alignment.topCenter,
        //     end: Alignment.bottomCenter,
        //     colors: [Colors.red, Colors.blue])
      ),
      alignment: Alignment.center,
      // child对齐方式
      child: Container(
        width: 100,
        height: 100,
        color: Colors.blue.withOpacity(0.5),
      ),
    );
  }
}
