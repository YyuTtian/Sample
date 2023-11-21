import 'package:flutter/cupertino.dart';
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
    return Center(
      child: CupertinoTextField(
        placeholder: "提示文字",
        placeholderStyle: const TextStyle(color: Colors.grey),
        clearButtonMode: OverlayVisibilityMode.editing,
        decoration: BoxDecoration(
          gradient: const LinearGradient(colors: [Colors.red, Colors.blue]),
          borderRadius: BorderRadius.circular(5),
        ),
        padding: const EdgeInsets.all(10),
      ),
    );
  }
}
