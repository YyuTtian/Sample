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
    return const SizedBox(
      width: 200,
      height: 200,
      child: Image3(),
    );
  }
}

class Image1 extends StatelessWidget {
  const Image1({super.key});

  @override
  Widget build(BuildContext context) {
    return Image(
        // fill拉伸填满   contain完全显示留空白  cover等比放大
        fit: BoxFit.cover,
        // 容器有空白的时候 平铺方式
        repeat: ImageRepeat.repeat,
        // 矩形部分可以无限缩放  类似.9图
        centerSlice: const Rect.fromLTRB(20, 20, 20, 20),
        // 当url地址发生变化的时候 新图还没获取到的时候一直显示旧图  不会出现空白现象
        gaplessPlayback: true,
        // 图片加载失败后  返回本地表示失败的图片
        errorBuilder: (context, error, stackTrace) {
          return Image.asset(Assets.imagesSadIconPopup);
        },
        // 图片显示前 占位图  wasSynchronouslyLoaded是否同步加载  推荐用FadeInImage
        frameBuilder: (context, child, frame, wasSynchronouslyLoaded) {
          if (frame == null) return const Text("加载中");
          return child;
        },
        // 加载进度
        loadingBuilder: (context, child, progress) {
          // 如果加载完成 直接显示图片
          if (progress == null) return child;
          final percent = progress.cumulativeBytesLoaded / progress.expectedTotalBytes! * 100;
          return Text("加载$percent");
        },
        image:
            // NetworkImage('https://flutter.github.io/assets-for-api-docs/assets/widgets/owl.jpg')
            // FileImage(File("path"))
            const AssetImage(Assets.imagesSadIconPopup)
        // MemoryImage(bytes)
        );
  }
}

class Image2 extends StatelessWidget {
  const Image2({super.key});

  @override
  Widget build(BuildContext context) {
    return Image.network("https://flutter.github.io/assets-for-api-docs/assets/widgets/owl.jpg");
  }
}

class Image3 extends StatelessWidget {
  const Image3({super.key});

  @override
  Widget build(BuildContext context) {
    return Image.asset(Assets.imagesSadIconPopup);
  }
}
