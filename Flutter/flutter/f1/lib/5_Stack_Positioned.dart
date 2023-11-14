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
      color: Colors.black,
      width: double.infinity,
      height: double.infinity,
      child: Stack(
        // RelativeLayout
        children: [
          Container(
            width: 200,
            height: 200,
            color: Colors.red,
          ),
          Positioned(
              top: 50,
              // 距离上边50
              left: 50,
              // 距离左边50
              width: 100,
              height: 100,
              child: Container(
                color: Colors.blue,
              ))
        ],
      ),
    );
  }
}
