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
    // 不会渲染到刘海 摄像孔区域
    return SafeArea(
        top: false, // 上方不用避让
        child: ListView.builder(
            itemCount: 100,
            itemBuilder: (context, index) {
              return Container(
                height: 100,
                padding: const EdgeInsets.all(10),
                child: Container(
                  color: Colors.red,
                ),
              );
            }));
  }
}
