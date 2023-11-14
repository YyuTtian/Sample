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
    return View1();
  }
}

class View1 extends StatefulWidget {
  const View1({super.key});

  @override
  State<View1> createState() => _View1State();
}

class _View1State extends State<View1> {
  double paddingValue = 10.0;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Container(
            color: Colors.red,
            width: 300,
            height: 300,
            child: AnimatedPadding(
              padding: EdgeInsets.all(paddingValue),
              duration: const Duration(milliseconds: 500),
              child: Container(
                // paddingValue变大  blue越小
                color: Colors.blue,
              ),
            ),
          ),
          const SizedBox(
            height: 50,
          ),
          ElevatedButton(
              onPressed: () {
                setState(() {
                  paddingValue += 10;
                });
              },
              child: const Text("开始动画"))
        ],
      ),
    );
  }
}
