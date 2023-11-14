import 'package:flutter/gestures.dart';
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
    return const Rich();
  }
}

class Rich extends StatelessWidget {
  const Rich({super.key});

  @override
  Widget build(BuildContext context) {
    return RichText(
        text: TextSpan(
            style: const TextStyle(fontSize: 28, color: Colors.black),
            text: "111",
            children: [
          const TextSpan(style: TextStyle(fontSize: 50, color: Colors.red), text: "22"),
          TextSpan(
              style: const TextStyle(fontSize: 80, color: Colors.blue),
              text: "333",
              recognizer: TapGestureRecognizer()..onTap = () => {debugPrint("点击了333")}),
        ]));
  }
}

class TextRich extends StatelessWidget {
  const TextRich({super.key});

  @override
  Widget build(BuildContext context) {
    return Text.rich(
        TextSpan(text: "444", style: const TextStyle(fontSize: 28, color: Colors.black), children: [
      const TextSpan(style: TextStyle(fontSize: 50, color: Colors.red), text: "555"),
      TextSpan(
        style: const TextStyle(fontSize: 80, color: Colors.blue),
        text: "666",
        recognizer: TapGestureRecognizer()..onTap = () => {debugPrint("点击了666")},
      )
    ]));
  }
}
