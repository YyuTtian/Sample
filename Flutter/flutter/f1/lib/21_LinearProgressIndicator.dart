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
      child: LoadingV(),
    );
  }
}

class Loading extends StatelessWidget {
  const Loading({super.key});

  @override
  Widget build(BuildContext context) {
    return const SizedBox(
      width: 200,
      height: 200,
      child: LinearProgressIndicator(
        // value: 0.8,
        // valueColor: AlwaysStoppedAnimation(Colors.red),
        color: Colors.red,
        backgroundColor: Colors.grey,
      ),
    );
  }
}

class LoadingV extends StatelessWidget {
  const LoadingV({super.key});

  @override
  Widget build(BuildContext context) {
    return const RotatedBox(
      quarterTurns: -1,
      child: LinearProgressIndicator(
        value: 0.6,
      ),
    );
  }
}
