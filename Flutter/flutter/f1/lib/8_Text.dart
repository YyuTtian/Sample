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
    return const Center(
      child: Text(
        "测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试",
        // fontSize 默认14
        // color: Colors.blue 文字颜色
        // decoration: TextDecoration.lineThrough 删除线
        // decorationColor: Colors.white 删除线颜色
        // decorationStyle: TextDecorationStyle.dashed 删除线虚线
        // decorationThickness: 5 删除线的粗细
        // letterSpacing: 10 字之间的留白
        // fontWeight: FontWeight.bold 粗体
        // fontStyle: FontStyle.italic 斜体
        // ontFamily: "自定义字体" 字体
        // backgroundColor: Colors.red 背景色
        // shadows 文字阴影
        style: TextStyle(
            fontSize: 28,
            color: Colors.blue,
            decoration: TextDecoration.lineThrough,
            decorationColor: Colors.white,
            decorationStyle: TextDecorationStyle.dashed,
            decorationThickness: 5,
            letterSpacing: 10,
            fontWeight: FontWeight.bold,
            fontStyle: FontStyle.italic,
            fontFamily: "自定义字体",
            backgroundColor: Colors.red,
            shadows: [BoxShadow(color: Colors.grey, offset: Offset(10, 10), blurRadius: 10)]),
        // softWrap: false, 不允许换行
        softWrap: false,
        maxLines: 2,
        // 末尾 ...
        overflow: TextOverflow.ellipsis,
        // 字体缩放1.5倍
        textScaleFactor: 1.5,
      ),
    );
  }
}
