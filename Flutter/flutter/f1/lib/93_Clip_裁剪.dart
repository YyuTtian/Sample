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
    return const View4();
  }
}

class View1 extends StatelessWidget {
  const View1({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Container(
        width: 200,
        height: 100,
        color: Colors.blue,
        // 200 100 裁剪成椭圆
        child: ClipOval(
          clipper: MyClip1(),
          child: Container(
            color: Colors.grey,
          ),
        ),
      ),
    );
  }
}

class View2 extends StatelessWidget {
  const View2({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Container(
        width: 200,
        height: 100,
        color: Colors.blue,
        // 200 100 裁剪成矩形
        child: ClipRect(
          clipper: MyClip1(),
          child: Container(
            color: Colors.grey,
          ),
        ),
      ),
    );
  }
}

class View3 extends StatelessWidget {
  const View3({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Container(
        width: 200,
        height: 100,
        color: Colors.blue,
        // 200 100 裁剪成圆角矩形
        child: ClipRRect(
          clipper: MyClip2(),
          child: Container(
            color: Colors.grey,
          ),
        ),
      ),
    );
  }
}

class View4 extends StatelessWidget {
  const View4({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Container(
        width: 200,
        height: 100,
        color: Colors.blue,
        // 200 100 裁剪成自定义形状
        child: ClipPath(
          clipper: MyClip3(),
          child: Container(
            color: Colors.grey,
          ),
        ),
      ),
    );
  }
}

class MyClip1 extends CustomClipper<Rect> {
  @override
  Rect getClip(Size size) {
    // 左右各留10的空白
    return Rect.fromLTWH(10, 10, size.width - 20, size.height - 20);
  }

  @override
  bool shouldReclip(covariant CustomClipper<Rect> oldClipper) {
    return true;
  }
}

class MyClip2 extends CustomClipper<RRect> {
  @override
  RRect getClip(Size size) {
    // 左右各留10的空白
    var rect = Rect.fromLTWH(10, 10, size.width - 20, size.height - 20);
    return RRect.fromRectAndCorners(
      rect,
      topLeft: const Radius.circular(10),
      topRight: const Radius.circular(10),
      bottomLeft: const Radius.circular(10),
      bottomRight: const Radius.circular(10),
    );
  }

  @override
  bool shouldReclip(covariant CustomClipper<RRect> oldClipper) {
    return true;
  }
}

class MyClip3 extends CustomClipper<Path> {
  @override
  Path getClip(Size size) {
    return Path()
      ..moveTo(0, size.height)
      ..lineTo(size.width / 2, 0)
      ..lineTo(size.width, size.height)
      ..close();
  }

  @override
  bool shouldReclip(covariant CustomClipper<Path> oldClipper) {
    return true;
  }
}
