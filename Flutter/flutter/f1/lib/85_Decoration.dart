import 'package:f1/generated/assets.dart';
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
      child: DecoratedBox(
        decoration: BoxDecoration(
          color: Colors.grey[300],
          borderRadius: BorderRadius.circular(10),
          border: Border.all(color: Colors.red, width: 2),
          // 添加背景图片
          image: const DecorationImage(image: AssetImage(Assets.imagesSadIconPopup)),
          // 阴影
          boxShadow: const [BoxShadow(color: Colors.red, offset: Offset(4, 4), blurRadius: 2)],
          // 渐变
          gradient: const LinearGradient(
              begin: Alignment.centerLeft,
              end: Alignment.centerRight,
              colors: [Colors.blue, Colors.yellow]),
        ),
        child: const FlutterLogo(
          size: 100,
        ),
      ),
    );
  }
}
