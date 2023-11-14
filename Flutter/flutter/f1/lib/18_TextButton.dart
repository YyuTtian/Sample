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
      child: TextButton(
        onPressed: () {},
        style: ButtonStyle(backgroundColor: MaterialStateProperty.all(Colors.blue)),
        child: Text(
          "按钮",
          style: TextStyle(color: Colors.red),
        ),
      ),
    );
  }
}
