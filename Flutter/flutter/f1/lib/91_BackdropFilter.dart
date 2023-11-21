import 'dart:ui';

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
      child: Stack(
        children: [
          const FlutterLogo(
            size: 100,
          ),
          BackdropFilter(
            // 100尺寸的模糊 50尺寸的不模糊 用来凸显50尺寸的
            filter: ImageFilter.blur(sigmaY: 5, sigmaX: 5),
            child: const FlutterLogo(
              size: 50,
            ),
          )
        ],
      ),
    );
  }
}
