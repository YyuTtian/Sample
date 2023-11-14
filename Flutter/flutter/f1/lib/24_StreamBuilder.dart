import 'dart:async';

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
      child: SizedBox(
        width: 200,
        height: 200,
        child: Stream2(),
      ),
    );
  }
}

class Stream1 extends StatelessWidget {
  const Stream1({super.key});

  @override
  Widget build(BuildContext context) {
    return StreamBuilder(
      stream: time(),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.none) {
          return Text("没有数据流");
        } else if (snapshot.connectionState == ConnectionState.waiting) {
          return Text("等待数据流");
        } else if (snapshot.connectionState == ConnectionState.active) {
          if (snapshot.hasError) {
            return Text("数据流异常");
          } else {
            return Text("数据流活跃 数据=${snapshot.data}");
          }
        } else {
          return Text("数据流关闭");
        }
      },
    );
  }

  Stream<DateTime> time() => Stream.periodic(const Duration(seconds: 1), (_) {
        return DateTime.now();
      });
}

class Stream2 extends StatelessWidget {
  Stream2({super.key});

  final control = StreamController();

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ElevatedButton(
            onPressed: () {
              // 发送数据流
              control.add(1);

              // control.close();
              // control.addError("error");
              // control.done
            },
            child: Text("按钮")),
        StreamBuilder(
            stream: control.stream,
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.none) {
                return Text("没有数据流");
              } else if (snapshot.connectionState == ConnectionState.waiting) {
                return Text("等待数据流");
              } else if (snapshot.connectionState == ConnectionState.active) {
                if (snapshot.hasError) {
                  return Text("数据流异常");
                } else {
                  return Text("数据流活跃 数据=${snapshot.data}");
                }
              } else {
                return Text("数据流关闭");
              }
            })
      ],
    );
  }
}
