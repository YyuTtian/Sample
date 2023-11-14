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
    // 相当于ScrollView
    return SingleChildScrollView(
      // scrollDirection: Axis.vertical, 设置滑动方向
      physics: const BouncingScrollPhysics(),
      child: Column(
        children: [
          Container(
            height: 180,
            color: Colors.red,
          ),
          SizedBox(
            height: 10,
          ),
          Container(
            height: 180,
            color: Colors.red,
          ),
          SizedBox(
            height: 10,
          ),
          Container(
            height: 180,
            color: Colors.red,
          ),
          SizedBox(
            height: 10,
          ),
          Container(
            height: 180,
            color: Colors.red,
          ),
          SizedBox(
            height: 10,
          ),
          Container(
            height: 180,
            color: Colors.red,
          ),
          SizedBox(
            height: 10,
          ),
          Container(
            height: 180,
            color: Colors.red,
          ),
          SizedBox(
            height: 10,
          ),
        ],
      ),
    );
  }
}
