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
      child: Stack(
        children: [
          GestureDetector(
            behavior: HitTestBehavior.opaque,
            onTapDown: (details) {
              print(
                  "onTapDown kind=${details.kind} globalPosition=${details.globalPosition}  localPosition=${details.localPosition}");
            },
            onTapUp: (details) {
              print(
                  "onTapUp kind=${details.kind} globalPosition=${details.globalPosition}  localPosition=${details.localPosition}");
            },
            onTap: () {
              print("单击了");
            },
            onTapCancel: () {
              print("onTapCancel");
            },
            onLongPress: () {
              print("长按了");
            },
            onDoubleTap: () {
              print("双击了");
            },
            // onPanDown: (details) {
            //   print("平移按下");
            // },
            // onPanStart: (details) {
            //   print("平移开始");
            // },
            // onPanUpdate: (details) {
            //   print("平移更新 ${details.delta} ${details.globalPosition} ${details.localPosition}");
            // },
            // onPanEnd: (details) {
            //   print("平移结束");
            // },
            // onPanCancel: () {
            //   print("平移取消");
            // },
            // onHorizontalDragUpdate: (details) {
            //   print("只监测水平拖拽");
            // },
            // onVerticalDragUpdate: (details) {
            //   print("只监测垂直拖拽");
            // },
            onScaleStart: (details) {},
            onScaleUpdate: (details) {
              print("onScaleUpdate scale=${details.scale} rotation=${details.rotation}");
            },
            onScaleEnd: (details) {},
            child: Container(
              width: 200,
              height: 200,
              color: Colors.red,
            ),
          ),
          GestureDetector(
            // 两个GestureDetector重叠了
            // HitTestBehavior.opaque 自己拦截不会传递
            // HitTestBehavior.translucent 可以传递
            // HitTestBehavior.deferToChild 随子组件 因为Contain透明  所以会传递下去
            behavior: HitTestBehavior.opaque,
            child: Container(
              width: 200,
              height: 200,
            ),
          )
        ],
      ),
    );
  }
}
