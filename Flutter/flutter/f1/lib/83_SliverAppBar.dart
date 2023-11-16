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
    return CustomScrollView(
      slivers: [
        const SliverAppBar(
          // pinned: true,// 钉住
          // floating: true, // 上滑跟着走  下滑一点就出来
          title: Text("SliverAppBar"),
          leading: BackButton(),
          actions: [CloseButton()],
        ),
        SliverList(
            delegate: SliverChildBuilderDelegate(childCount: 100, (context, index) {
          return Container(
            height: 100,
            color: index % 2 == 0 ? Colors.red : Colors.grey,
          );
        })),
      ],
    );
  }
}
