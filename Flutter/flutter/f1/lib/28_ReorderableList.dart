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
      child: View1(),
    );
  }
}

class View1 extends StatefulWidget {
  const View1({super.key});

  @override
  State<View1> createState() => _View1State();
}

class _View1State extends State<View1> {
  final shades = [700, 200, 600, 500, 900, 800];

  @override
  Widget build(BuildContext context) {
    return ReorderableList(
      itemCount: shades.length,
      // 触摸直接触发
      onReorder: (oldIndex, newIndex) {
        print("oldIndex=$oldIndex newIndex=$newIndex");
        setState(() {
          if (newIndex > oldIndex) newIndex--;
          final shade = shades.removeAt(oldIndex);
          shades.insert(newIndex, shade);
          print(shades);
        });
      },
      itemBuilder: (context, index) {
        return ReorderableDragStartListener(
          key: ValueKey(shades[index]),
          index: index,
          child: Container(
            height: 50,
            margin: EdgeInsets.all(5),
            color: Colors.grey[shades[index]],
          ),
        );
      },
    );
  }
}
