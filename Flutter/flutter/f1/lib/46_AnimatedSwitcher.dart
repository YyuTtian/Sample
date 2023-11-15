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
    return const View1();
  }
}

class View1 extends StatefulWidget {
  const View1({super.key});

  @override
  State<View1> createState() => _View1State();
}

class _View1State extends State<View1> {
  final bool _loading = false;

  @override
  Widget build(BuildContext context) {
    return Center(
      // 组件切换动画  默认淡入淡出
      child: AnimatedSwitcher(
        duration: const Duration(seconds: 3),
        child: _loading
            ? const CircularProgressIndicator()
            : const FlutterLogo(
                size: 50,
              ),
      ),
    );
  }
}
