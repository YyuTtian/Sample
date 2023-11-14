import 'package:f1/generated/assets.dart';
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
    // 占位图
    return FadeInImage(
        // 占位图消失的时长
        fadeOutDuration: const Duration(seconds: 1),
        // 显示图显示的时长
        fadeInDuration: const Duration(seconds: 1),
        placeholderErrorBuilder: (context, error, stackTrace) {
          return const Text("占位图加载失败");
        },
        imageErrorBuilder: (context, error, stackTrace) {
          return const Text("图片加载失败");
        },
        placeholder: const AssetImage(Assets.imagesSadIconPopup),
        image: const NetworkImage(
            "https://flutter.github.io/assets-for-api-docs/assets/widgets/owl.jpg"));
  }
}
