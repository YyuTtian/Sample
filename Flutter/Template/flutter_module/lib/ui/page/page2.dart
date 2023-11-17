import 'package:flutter/material.dart';

class Page2 extends StatelessWidget {
  const Page2({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: Scaffold(
        body: Center(
          child: Text(
            "Page2",
            style: TextStyle(color: Colors.red, fontSize: 30),
          ),
        ),
      ),
    );
  }
}
