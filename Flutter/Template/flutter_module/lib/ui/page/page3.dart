import 'package:flutter/material.dart';

class Page3 extends StatelessWidget {
  const Page3({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: Scaffold(
        body: Center(
          child: Text(
            "Page3",
            style: TextStyle(color: Colors.red, fontSize: 30),
          ),
        ),
      ),
    );
  }
}
