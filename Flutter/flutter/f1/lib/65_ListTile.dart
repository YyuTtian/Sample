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
    return ListView.builder(
        itemCount: 100,
        itemBuilder: (context, index) {
          return const ListTile(
            title: Text("title"),
            subtitle: Text("subTitle"),
            leading: Icon(Icons.add),
            trailing: Icon(Icons.arrow_forward_ios_rounded),
          );
        });
  }
}
