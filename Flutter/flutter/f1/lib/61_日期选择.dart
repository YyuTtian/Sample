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
      child: ElevatedButton(
        onPressed: () async {
          var result = await showDatePicker(
            context: context,
            initialDate: DateTime.now(),
            firstDate: DateTime(1990, 1, 1),
            lastDate: DateTime(2099, 12, 31),
          );

          print("result=$result");
        },
        child: Text("选择日期"),
      ),
    );
  }
}
