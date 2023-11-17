import 'package:flutter/material.dart';

class Page1 extends StatelessWidget {
  const Page1({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: Scaffold(
        body: Center(
          child: Text(
            "Page1",
            style: TextStyle(color: Colors.red, fontSize: 30),
          ),
        ),
      ),
    );
  }
}
