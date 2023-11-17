import 'package:flutter/material.dart';
import 'package:flutter_module/bridge/bridge.dart';
import 'package:flutter_module/bridge/bridgeImpl.dart';
import 'package:flutter_module/ui/page/page2.dart';

import 'routes.dart';
import 'ui/page/page1.dart';
import 'ui/page/page3.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Material App',
      routes: <String, WidgetBuilder>{
        Routes.splash: (context) => const Splash(),
        Routes.page1: (context) => const Page1(),
        Routes.page2: (context) => const Page2(),
        Routes.page3: (context) => const Page3(),
      },
      // home: Scaffold(
      //   appBar: AppBar(
      //     title: const Text('Material App Bar'),
      //   ),
      //   body: const Splash(),
      // ),
    );
  }
}

class Splash extends StatefulWidget {
  const Splash({super.key});

  @override
  State<Splash> createState() => _SplashState();
}

class _SplashState extends State<Splash> {
  Future<void> getNativeResult() async {
    var flutterCallNative = FlutterCallNative();
    var result = await flutterCallNative.loadNativeData(1);
    print("Flutter从客户端拿到的值 id=${result.id} name=${result.name}");
  }

  @override
  void initState() {
    super.initState();
    NativeCallFlutter.setup(BridgeImpl());
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: GestureDetector(
        onTap: () {
          getNativeResult();
        },
        child: const Text(
          "Flutter开屏",
          style: TextStyle(color: Colors.red, fontSize: 30),
        ),
      ),
    );
  }
}
