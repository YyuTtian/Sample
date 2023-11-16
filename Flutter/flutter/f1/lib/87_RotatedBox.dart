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
    return const RotatedBox(
      // 1 顺时针 90
      // 2 顺时针 180
      // 3 顺时针 270
      // 4 顺时针 360
      // -1 逆时针 90
      quarterTurns: 1,
      child: Text("asdfsafsfdasf"),
    );
  }
}
