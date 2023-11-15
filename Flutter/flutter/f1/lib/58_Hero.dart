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
    return const Page1();
  }
}

class Page1 extends StatelessWidget {
  const Page1({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        children: [
          SizedBox(
            width: 100,
            height: 100,
            child: Hero(tag: "heroTag", child: Icon(Icons.heart_broken)),
          ),
          SizedBox(
            height: 20,
          ),
          ElevatedButton(
              onPressed: () {
                Navigator.of(context).push(MaterialPageRoute(builder: (context) {
                  return Page2();
                }));
              },
              child: Text("跳转")),
        ],
      ),
    );
  }
}

class Page2 extends StatelessWidget {
  const Page2({super.key});

  // 有点问题
  @override
  Widget build(BuildContext context) {
    return const Center(
      child: SizedBox(
        width: 100,
        height: 100,
        child: Hero(tag: "heroTag", child: Icon(Icons.heart_broken)),
      ),
    );
  }
}
