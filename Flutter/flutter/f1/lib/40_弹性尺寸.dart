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
    return SizedBox(
      height: 100,
      child: Row(
        children: [
          Expanded(
              flex: 1, // 一定会占1/4
              child: Container(
                color: Colors.green,
              )),
          Expanded(
              flex: 1, // 一定会占1/4
              child: Container(
                color: Colors.grey,
              )),
          const Spacer(
            flex: 1, // 一定会占1/4 留白用的
          ),
          Flexible(
              flex: 1, // 如果子组件的宽度小于1/4  就会使用子组件的宽度
              child: Container(
                width: 20,
                color: Colors.red,
              ))
        ],
      ),
    );
  }
}
