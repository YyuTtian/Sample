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
    return const View4();
  }
}

class View1 extends StatelessWidget {
  const View1({super.key});

  @override
  Widget build(BuildContext context) {
    return ConstrainedBox(
      constraints: const BoxConstraints(maxWidth: 100, maxHeight: 100),
      child: Container(
        width: 200, // 设置了200也没用 最终尺寸为100*100
        height: 200,
        color: Colors.red,
      ),
    );
  }
}

class View2 extends StatelessWidget {
  const View2({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // 父组件尺寸是无穷的时候用
        LimitedBox(
          // 父组件是Column 高是无穷 maxHeight起作用   maxWidth不起作用
          maxHeight: 300,
          maxWidth: 300,
          child: Container(
            color: Colors.red,
          ),
        )
      ],
    );
  }
}

class View3 extends StatelessWidget {
  const View3({super.key});

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(builder: (context, constraints) {
      print("当前尺寸约束为$constraints");
      return Container(
        color: Colors.red,
        // 用父组件约束的宽高各一半作为Container的宽高
        width: constraints.maxWidth / 2,
        height: constraints.maxHeight / 2,
      );
    });
  }
}

class View4 extends StatelessWidget {
  const View4({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Container(
        width: 100,
        height: 100,
        color: Colors.blue,
        child: FractionallySizedBox(
          widthFactor: 3, // 可以大于1
          heightFactor: 3,
          child: Container(
            color: Colors.red,
          ),
        ),
      ),
    );
  }
}
