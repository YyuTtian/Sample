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
  OverlayEntry? entry;
  double left = 100.0;

  @override
  Widget build(BuildContext context) {
    var textStyle = Theme.of(context).textTheme.bodyMedium;
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          ElevatedButton(
            child: const Text("生成悬浮窗"),
            onPressed: () {
              entry = OverlayEntry(builder: (context) {
                return Positioned(
                  left: left,
                  top: 100,
                  child: Container(
                    width: 100,
                    height: 100,
                    color: Colors.red,
                    child: const Center(
                      child: Material(
                        color: Colors.transparent,
                        // 悬浮窗上的Text要用Material包一下
                        child: Text("我是悬浮窗"),
                      ),
                    ),
                  ),
                );
              });

              Overlay.of(context).insert(entry!);
            },
          ),
          const SizedBox(
            height: 10,
          ),
          ElevatedButton(
            child: const Text("删除悬浮窗"),
            onPressed: () {
              entry!.remove();
            },
          ),
          const SizedBox(
            height: 10,
          ),
          ElevatedButton(
            child: const Text("移动悬浮窗"),
            onPressed: () {
              left += 10;
              entry!.markNeedsBuild();
            },
          ),
        ],
      ),
    );
  }
}
