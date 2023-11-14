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
    return Wrap(
      // 一行不够 会自动换行
      spacing: 10, // 每个child之间的距离
      runSpacing: 10, // 换行之后 行与行之间的距离
      children: [
        Container(
          margin: const EdgeInsets.all(10),
          width: 100,
          height: 100,
          color: Colors.red,
        ),
        Container(
          margin: const EdgeInsets.all(10),
          width: 100,
          height: 100,
          color: Colors.red,
        ),
        Container(
          margin: const EdgeInsets.all(10),
          width: 100,
          height: 100,
          color: Colors.red,
        ),
        Container(
          margin: const EdgeInsets.all(10),
          width: 100,
          height: 100,
          color: Colors.red,
        ),
        Container(
          margin: const EdgeInsets.all(10),
          width: 100,
          height: 100,
          color: Colors.red,
        ),
        Container(
          margin: const EdgeInsets.all(10),
          width: 100,
          height: 100,
          color: Colors.red,
        ),
        Container(
          margin: const EdgeInsets.all(10),
          width: 100,
          height: 100,
          color: Colors.red,
        ),
        Container(
          margin: const EdgeInsets.all(10),
          width: 100,
          height: 100,
          color: Colors.red,
        ),
      ],
    );
  }
}
