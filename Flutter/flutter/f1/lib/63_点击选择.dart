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
    return Column(
      children: [
        const SizedBox(
          height: 100,
        ),
        DropdownButton(
          hint: const Text("请选择"),
          items: const [
            DropdownMenuItem(
              value: "cn",
              child: Text("中文"),
            ),
            DropdownMenuItem(
              value: "en",
              child: Text("英文"),
            ),
          ],
          onChanged: (value) {
            print("选择了$value");
          },
        )
      ],
    );
  }
}
