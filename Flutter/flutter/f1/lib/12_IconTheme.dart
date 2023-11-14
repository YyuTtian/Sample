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
    return IconTheme(
        data: const IconThemeData(size: 50, color: Colors.red), // 一组icon都用相同的尺寸颜色
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: const [
            Icon(Icons.close),
            Icon(
              Icons.arrow_back, // 也可以自己单独设置
              size: 180,
            ),
            Icon(Icons.star),
          ],
        ));
  }
}
