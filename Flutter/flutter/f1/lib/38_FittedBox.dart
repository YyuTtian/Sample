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
      width: 100,
      height: 50,
      color: Colors.red,
      // 让Text的宽度和Container的宽度一样  Container宽度不同会导致Text的字号也跟着变化
      child: const FittedBox(
        fit: BoxFit.contain, // 完全显示Text 还有其他枚举
        alignment: Alignment.center,
        child: Text("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
      ),
    );
  }
}
