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
      child: Stepper(
        currentStep: 1,
        type: StepperType.horizontal,
        controlsBuilder: (context, details) {
          return Container();
        },
        steps: [
          Step(title: Text("第一步"), content: Container()),
          Step(title: Text("第二步"), content: Container()),
          Step(title: Text("第三步"), content: Container()),
        ],
      ),
    );
  }
}
