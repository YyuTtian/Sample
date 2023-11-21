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
      child: FutureBuilder(
          // 有默认值就不会出loading了
          // initialData: "默认值",
          future: _loadData(),
          builder: (context, snapshot) {
            if (snapshot.hasError) {
              return Text("Error ${snapshot.error}");
            } else if (snapshot.hasData) {
              return Text("Succ ${snapshot.data}");
            } else {
              return const CircularProgressIndicator();
            }
          }),
    );
  }

  Future<String> _loadData() async {
    await Future.delayed(const Duration(seconds: 3));
    return "hello";
  }
}
