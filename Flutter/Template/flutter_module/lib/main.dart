import 'package:flutter/material.dart';
import 'package:flutter_module/bridge/bridge.dart';
import 'package:flutter_module/bridge/bridgeImpl.dart';
import 'package:flutter_module/ui/page/page2.dart';

import 'routes.dart';
import 'ui/page/page1.dart';
import 'ui/page/page3.dart';

void main() {
  print("enter main");
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
    print("myapp initState");
    NativeCallFlutter.setup(BridgeImpl());
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      routes: <String, WidgetBuilder>{
        Routes.splash: (context) => const Init(),
        Routes.page1: (context) => const Page1(),
        Routes.page2: (context) => const Page2(),
        Routes.page3: (context) => const Page3(),
      },
      // home: Init(),
    );
  }
}

class Init extends StatefulWidget {
  const Init({super.key});

  @override
  State<Init> createState() => _InitState();
}

class _InitState extends State<Init> {
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
