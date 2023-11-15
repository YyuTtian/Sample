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
      child: SizedBox(
          height: 100,
          child: Padding(
            padding: EdgeInsets.all(8.0),
            child: Card(
              color: Colors.red,
              elevation: 2,
              child: Container(),
            ),
          )),
    );
  }
}
