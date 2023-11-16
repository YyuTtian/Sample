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
    return View3();
  }
}

class View1 extends StatelessWidget {
  const View1({super.key});

  @override
  Widget build(BuildContext context) {
    return const Offstage(
      child: Text("类似于Invisible"),
    );
  }
}

class View2 extends StatelessWidget {
  const View2({super.key});

  @override
  Widget build(BuildContext context) {
    return const Visibility(
        visible: false,
        // 是否保持状态
        maintainState: true,
        // 是否保持动画
        maintainAnimation: true,
        // 是否保持尺寸
        maintainSize: true,
        // 是否保持交互
        maintainInteractivity: true,
        child: Text("Visibility"));
  }
}

class View3 extends StatelessWidget {
  const View3({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: SizedBox(
        width: 100,
        height: 100,
        child: IndexedStack(
          index: 1 > 2 ? 1 : 2, // index是几就显示第几个
          children: [
            Container(
              color: Colors.red,
            ),
            Container(
              color: Colors.black,
            ),
            Container(
              color: Colors.grey,
            ),
          ],
        ),
      ),
    );
  }
}
