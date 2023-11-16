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
      child: ShaderMask(
        shaderCallback: (bounds) => const LinearGradient(
          // 斑马效果  注释掉就是渐变效果
          begin: Alignment(-1, 0),
          end: Alignment(-0.9, 0),

          colors: [Colors.blue, Colors.green],
          tileMode: TileMode.repeated,
        ).createShader(bounds),
        child: const Text(
          "斑马效果",
          style: TextStyle(color: Colors.white, fontSize: 80),
        ),
      ),
    );
  }
}
