import 'package:flutter/material.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: Scaffold(body: BtnScale()),
    );
  }
}

class BtnScale extends StatefulWidget {
  const BtnScale({super.key});

  @override
  State<BtnScale> createState() => _BtnScaleState();
}

class _BtnScaleState extends State<BtnScale> with SingleTickerProviderStateMixin {
  late AnimationController controller;
  late Animation<double> animation;

  @override
  void initState() {
    controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 500),
    )..repeat(reverse: true);
    // 设置动画曲线 为Curves.easeOut
    animation = CurvedAnimation(parent: controller, curve: Curves.easeOut);
    animation = Tween(begin: 1.0, end: 1.5).animate(animation)
      ..addListener(() {
        print("controller.value=${animation.value}");
        // setState(() {});
      });

    super.initState();
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: controller,
      builder: (context, child) {
        return Transform.scale(
          scale: animation.value,
          child: Center(
            child: Container(
              width: 200,
              height: 70,
              color: Colors.red,
              child: const Center(
                child: Text("按钮"),
              ),
            ),
          ),
        );
      },
    );
  }
}
