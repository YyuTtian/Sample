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
      physics: const BouncingScrollPhysics(),
      slivers: [
        SliverToBoxAdapter(
          child: Container(
            height: 100,
            color: Colors.red,
          ),
        ),
        SliverList(
            delegate: SliverChildListDelegate([
          const Text("SliverChildListDelegate 1"),
          const Text("SliverChildListDelegate 2"),
          const Text("SliverChildListDelegate 3"),
        ])),
        SliverList(
          delegate: SliverChildBuilderDelegate(childCount: 3, (context, index) {
            return Text("SliverChildBuilderDelegate ${index + 1}");
          }),
        ),
        SliverFixedExtentList(
            itemExtent: 50,
            delegate: SliverChildListDelegate([
              const Text("SliverChildListDelegate itemExtent 1"),
              const Text("SliverChildListDelegate itemExtent 2"),
              const Text("SliverChildListDelegate itemExtent 3"),
            ])),
        SliverFixedExtentList(
            itemExtent: 50,
            delegate: SliverChildBuilderDelegate(childCount: 10, (context, index) {
              return Text("SliverChildBuilderDelegate itemExtent ${index + 1}");
            })),
        SliverPrototypeExtentList(
          prototypeItem: const Text("模板尺寸"), // 以这个高度为参照
          delegate: SliverChildBuilderDelegate(childCount: 10, (context, index) {
            return Text("SliverPrototypeExtentList ${index + 1}");
          }),
        ),
        SliverGrid(
            delegate: SliverChildBuilderDelegate(childCount: 10, (context, index) {
              return Container(
                color: Colors.primaries[index * 2 % 18],
                child: Center(
                  child: Text("item ${index + 1}"),
                ),
              );
            }),
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 3, childAspectRatio: 9.0 / 16, crossAxisSpacing: 10, mainAxisSpacing: 10)),
        SliverFillViewport(
            // 类似于Pageview 占满一屏
            delegate: SliverChildListDelegate([
          Container(
            color: Colors.red,
            child: const Center(child: Text("page1")),
          ),
          Container(
            color: Colors.blue,
            child: const Center(child: Text("page2")),
          )
        ])),
      ],
    );
  }
}
