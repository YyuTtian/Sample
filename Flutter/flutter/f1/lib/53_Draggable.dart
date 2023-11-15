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
  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          DragTarget<int>(onWillAccept: (data) {
            print("onWillAccept");
            return true;
          }, onAccept: (data) {
            print("onAccept");
          }, builder: (context, candidateData, rejectedData) {
            // candidateData 候选数据列表
            // rejectedData  已拒数据列表
            print("builder $candidateData $rejectedData");
            return Container(
              width: 300,
              height: 300,
              color: Colors.blue,
            );
          }),
          const SizedBox(
            height: 50,
          ),
          Draggable<int>(
            // 允许同时拖拽的次数
            maxSimultaneousDrags: 1,
            // 设置拖动方向
            axis: Axis.vertical,
            onDragStarted: () {},
            onDragCompleted: () {},
            onDragEnd: (details) {},
            onDraggableCanceled: (velocity, offset) {},
            data: 0xffff0000,
            // 拖动状态下的组件
            feedback: Container(
              width: 100,
              height: 100,
              color: Colors.blue,
            ),
            // 拖动状态下留在原地的组件
            childWhenDragging: const SizedBox(
              width: 100,
              height: 100,
            ),
            child: Container(
              width: 100,
              height: 100,
              color: Colors.red,
            ),
          )
        ],
      ),
    );
  }
}
