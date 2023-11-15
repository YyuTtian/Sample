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
    return Center(
      child: CupertinoPicker(itemExtent: 50, onSelectedItemChanged: (value) {}, children: [
        for (int i = 1; i < 50; i++) Center(child: Text("第$i个")),
      ]),
    );
  }
}
