import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';

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

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  GlobalKey globalKey = GlobalKey();
  var _Bytes;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // 需要被截图的区域
        RepaintBoundary(
          key: globalKey,
          child: Container(
            width: 120,
            height: 120,
            decoration: BoxDecoration(border: Border.all(color: Colors.red, width: 2)),
            child: const Center(
              child: FlutterLogo(
                size: 80,
              ),
            ),
          ),
        ),
        const SizedBox(
          height: 20,
        ),
        ElevatedButton(
            onPressed: () async {
              final render = globalKey.currentContext!.findRenderObject() as RenderRepaintBoundary;
              final imageBytes =
                  (await (await render.toImage()).toByteData(format: ImageByteFormat.png))!
                      .buffer
                      .asUint8List();
              setState(() {
                _Bytes = imageBytes;
              });
            },
            child: const Text("截图")),

        const SizedBox(
          height: 20,
        ),

        if (_Bytes != null)
          Image.memory(
            _Bytes,
            width: 200,
          )
      ],
    );
  }
}
