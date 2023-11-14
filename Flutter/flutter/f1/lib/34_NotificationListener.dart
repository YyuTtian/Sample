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
    return NotificationListener(
        onNotification: (notification) {
          print(notification);

          // 接收自定义Nofification发送的事件
          if (notification is MyNoti) {
            print(notification.data);
          }

          return false; // false 继续传递  true 终止传递
        },
        child: ListView.builder(
            itemCount: 100,
            itemBuilder: (context, index) {
              return GestureDetector(
                child: Text("第$index个"),
                onTap: () {
                  // 自定义Nofification发送事件
                  MyNoti("第$index个").dispatch(context);
                },
              );
            }));
  }
}

class MyNoti extends Notification {
  final dynamic data;

  MyNoti(this.data);
}
