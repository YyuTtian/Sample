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

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  Widget build(BuildContext context) {
    final width = MediaQuery.of(context).size.width;
    final height = MediaQuery.of(context).size.height;
    final orientation = MediaQuery.of(context).orientation;
    final devicePixelRatio = MediaQuery.of(context).devicePixelRatio;
    final viewPadding = MediaQuery.of(context).viewPadding;
    final viewInsets = MediaQuery.of(context).viewInsets;
    final padding = MediaQuery.of(context).padding;
    final platformBrightness = MediaQuery.of(context).platformBrightness;
    final alwaysUse24HourFormat = MediaQuery.of(context).alwaysUse24HourFormat;
    final disableAnimations = MediaQuery.of(context).disableAnimations;
    final textScaleFactor = MediaQuery.of(context).textScaleFactor;

    print("""
    width=$width
    height=$height
    orientation=$orientation 竖屏=${orientation == Orientation.portrait}
    devicePixelRatio=$devicePixelRatio 逻辑宽度设置成100 实际使用${devicePixelRatio * 100}个物理像素
    viewPadding=$viewPadding  viewPadding指的是屏幕的物理缺陷(刘海 状态栏)
    viewInsets=$viewInsets  viewInsets指的是系统级别的遮挡（软键盘）
    padding=$padding   padding=viewPadding+viewInsets
    platformBrightness=$platformBrightness    夜间模式=${platformBrightness == Brightness.dark}
    alwaysUse24HourFormat=$alwaysUse24HourFormat     
    disableAnimations=$disableAnimations     
    textScaleFactor=$textScaleFactor 用户是否调整了字体大小=${textScaleFactor != 1.0}     
    
    """);
    return View2();
  }
}

class View1 extends StatelessWidget {
  const View1({super.key});

  @override
  Widget build(BuildContext context) {
    return MyColor(
        color: Colors.red,
        child: Builder(builder: (context) {
          return Container(
            width: 100,
            height: 100,
            color: Colors.blue,
            child: Center(
              child: Container(
                width: 50,
                height: 50,
                // context使用 builder: (context)回调里的context 不能用Widget build(BuildContext context)这个context
                color: MyColor.of(context)?.color,
              ),
            ),
          );
        }));
  }
}

class MyColor extends InheritedWidget {
  final Color color;

  MyColor({required this.color, required Widget child}) : super(child: child);

  @override
  bool updateShouldNotify(MyColor oldWidget) {
    return oldWidget.color != color;
  }

  static MyColor? of(BuildContext context) {
    return context.dependOnInheritedWidgetOfExactType<MyColor>();
  }
}

class View2 extends StatelessWidget {
  const View2({super.key});

  @override
  Widget build(BuildContext context) {
    return MediaQuery(
        // 即便用户设置了字体缩放和加粗也没有用
        data: MediaQuery.of(context).copyWith(
          textScaleFactor: 1.0,
          boldText: false,
        ),
        child: Column(
          children: const [
            Text("sdfa"),
            Text("sdfaf"),
          ],
        ));
  }
}
