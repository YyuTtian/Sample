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
  bool _shrink = false;

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        AnimatedPositioned(
            top: _shrink ? 300 : 200,
            bottom: _shrink ? 400 : 200,
            left: _shrink ? 100 : 50,
            right: _shrink ? 100 : 180,
            duration: const Duration(seconds: 1),
            child: Container(
              color: Colors.red,
            )),
        Align(
          alignment: const Alignment(0, 0.8),
          child: ElevatedButton(
            child: const Text("开始动画"),
            onPressed: () {
              setState(() {
                _shrink = !_shrink;
              });
            },
          ),
        )
      ],
    );
  }
}
