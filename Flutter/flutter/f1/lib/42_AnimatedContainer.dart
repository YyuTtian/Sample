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
  bool _round = false;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          AnimatedContainer(
            width: _round ? 200 : 300,
            height: 200,
            decoration: BoxDecoration(
                color: Colors.grey,
                borderRadius: BorderRadius.circular(_round ? 100 : 0),
                boxShadow: _round ? [const BoxShadow(spreadRadius: 8, blurRadius: 8)] : null),
            duration: const Duration(milliseconds: 500),
            curve: Curves.easeOut,
            onEnd: () {
              print("动画已完成");
            },
          ),
          const SizedBox(
            height: 50,
          ),
          ElevatedButton(
              style: ButtonStyle(backgroundColor: MaterialStateProperty.all(Colors.red)),
              onPressed: () {
                setState(() {
                  _round = !_round;
                });
              },
              child: const Text("开始动画"))
        ],
      ),
    );
  }
}
