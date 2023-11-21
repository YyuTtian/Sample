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
    return const Center(
      child: Loading1(),
    );
  }
}

class Loading1 extends StatelessWidget {
  const Loading1({super.key});

  @override
  Widget build(BuildContext context) {
    // 不传入value就一直转  传入就不转了
    return const CircularProgressIndicator(
      // value: 0.8,
      // valueColor: AlwaysStoppedAnimation(Colors.red),
      color: Colors.red,
      backgroundColor: Colors.grey,
    );
  }
}
