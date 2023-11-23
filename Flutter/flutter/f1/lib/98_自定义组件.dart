import 'dart:math';

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
    return View2();
  }
}

class View1 extends StatefulWidget {
  const View1({super.key});

  @override
  State<View1> createState() => _View1State();
}

class _View1State extends State<View1> {
  final List<Offset?> points = [];

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onPanUpdate: (details) {
        setState(() {
          points.add(details.localPosition);
        });
      },
      onPanEnd: (details) {
        setState(() {
          points.add(null);
        });
      },
      child: CustomPaint(
        foregroundPainter: Painter1(points),
        child: Container(
          color: Colors.grey,
        ),
      ),
    );
  }
}

class Painter1 extends CustomPainter {
  final List<Offset?> points;

  Painter1(this.points);

  static final pen = Paint()
    ..color = Colors.black
    ..strokeWidth = 2;

  @override
  void paint(Canvas canvas, Size size) {
    for (int i = 0; i < points.length - 1; i++) {
      if (points[i] != null && points[i + 1] != null) {
        canvas.drawLine(points[i]!, points[i + 1]!, pen);
      }
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    return true;
  }
}

class View2 extends StatefulWidget {
  const View2({super.key});

  @override
  State<View2> createState() => _View2State();
}

class _View2State extends State<View2> with SingleTickerProviderStateMixin {
  late AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(vsync: this, duration: const Duration(seconds: 1))..repeat();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return CustomPaint(
      size: Size.infinite,
      painter: Painter2(_controller),
    );
  }
}

class Painter2 extends CustomPainter {
  static final whitePaint = Paint()..color = Colors.white;
  static List<SnowFlake> snowflakes = List.generate(100, (index) => SnowFlake());

  Painter2(Listenable controller) : super(repaint: controller);

  @override
  void paint(Canvas canvas, Size size) {
    final w = size.width;
    final h = size.height;

    // 绘制渐变的蓝色背景
    final gradientPaint = Paint()
      ..shader = const LinearGradient(
        begin: Alignment.topCenter,
        end: Alignment.bottomCenter,
        colors: [Colors.blue, Colors.lightBlue, Colors.white],
        stops: [0, 0.7, 0.95],
      ).createShader(Offset.zero & size);
    canvas.drawRect(Offset.zero & size, gradientPaint);

    // 绘制文字
    const textSpan =
        TextSpan(text: "Do you wanna build a snowman?", style: TextStyle(fontSize: 30, color: Colors.white));
    TextPainter(text: textSpan, textDirection: TextDirection.ltr)
      ..layout(maxWidth: w * 0.5)
      ..paint(canvas, const Offset(50, 100));

    // 绘制雪人
    canvas.save();
    final side = size.shortestSide;
    canvas.translate(w > h ? w - side : 0, h > w ? h - side : 0);
    canvas.drawOval(Rect.fromLTRB(side * 0.25, side * 0.4, side * 0.75, side), whitePaint);
    canvas.drawCircle(Offset(side * 0.5, side * 0.3), side * 0.18, whitePaint);
    canvas.restore();

    // 绘制雪花
    snowflakes.forEach((snow) {
      snow.fall();
      canvas.drawCircle(Offset(snow.x * w, snow.y * h), snow.radius, whitePaint);
    });
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    return true;
  }
}

class SnowFlake {
  final Random random = Random();
  late double x, y, radius, velocity;

  SnowFlake() {
    reset();
    y = random.nextDouble();
  }

  void reset() {
    x = random.nextDouble();
    y = 0;
    radius = random.nextDouble() * 2 + 2;
    velocity = (random.nextDouble() * 4 + 2) / 2000;
  }

  void fall() {
    y += velocity;
    if (y > 1.0) reset();
  }
}
