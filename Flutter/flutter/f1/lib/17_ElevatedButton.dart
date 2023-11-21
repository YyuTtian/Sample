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
      child: Button2(),
    );
  }
}

class Button1 extends StatelessWidget {
  const Button1({super.key});

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
        onPressed: () {
          print("单击了");
        },
        onLongPress: () {
          print("长按了");
        },
        child: const Text("按钮"));
  }
}

class Button2 extends StatelessWidget {
  const Button2({super.key});

  @override
  Widget build(BuildContext context) {
    return ElevatedButton.icon(
      onPressed: () {
        print("点击了");
      },
      // onLongPress: (){
      //   print('长按了');
      // },

      icon: const Icon(Icons.star),
      label: const Text("按钮"),
      style: ButtonStyle(
        enableFeedback: true,
        padding: MaterialStateProperty.all(const EdgeInsets.all(10)),
        backgroundColor: MaterialStateProperty.resolveWith((states) {
          // 根据status判断
          if (states.contains(MaterialState.pressed)) {
            return Colors.red;
          }
          return Colors.blue;
        }),
      ),
    );
  }
}
