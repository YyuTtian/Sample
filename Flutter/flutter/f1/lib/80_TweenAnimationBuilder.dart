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
      child: AnimatedFlipCounter(
        duration: Duration(seconds: 1),
        value: 2023, // value值变化的时候触发动画
      ),
    );
  }
}

class AnimatedFlipCounter extends StatelessWidget {
  final int value;
  final Duration duration;
  final double size;
  final Color textColor;

  const AnimatedFlipCounter(
      {super.key,
      required this.value,
      required this.duration,
      this.size = 100,
      this.textColor = Colors.black});

  @override
  Widget build(BuildContext context) {
    List<int> digits = value == 0 ? [0] : [];
    int v = value;
    if (v < 0) {
      v *= -1;
    }

    while (v > 0) {
      digits.add(v);
      v = v ~/ 10;
    }

    return Row(
      mainAxisSize: MainAxisSize.min,
      children: List.generate(digits.length, (i) {
        return SingleDigitFlipCounter(
            key: ValueKey(digits.length - i),
            value: digits[digits.length - i - 1].toDouble(),
            duration: duration,
            height: size,
            width: size / 1.8,
            color: textColor);
      }),
    );
  }
}

class SingleDigitFlipCounter extends StatelessWidget {
  const SingleDigitFlipCounter(
      {super.key,
      required this.value,
      required this.duration,
      required this.height,
      required this.width,
      required this.color});

  final double value;
  final Duration duration;
  final double height;
  final double width;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return TweenAnimationBuilder(
      tween: Tween(begin: value, end: value),
      duration: duration,
      builder: (context, value, child) {
        final whole = value ~/ 1;
        final decimal = value - whole;
        return SizedBox(
          height: height * 1.2,
          width: width,
          child: Stack(
            children: [
              _buildSingleDigit(digit: whole % 10, offset: height * decimal, opacity: 1 - decimal),
              _buildSingleDigit(
                  digit: (whole + 1) % 10, offset: height * decimal - height, opacity: decimal),
            ],
          ),
        );
      },
    );
  }

  Widget _buildSingleDigit({required int digit, required double offset, required double opacity}) {
    return Positioned(
        bottom: offset,
        child: Text(
          "$digit",
          style: TextStyle(fontSize: height, color: color.withOpacity(opacity)),
        ));
  }
}
