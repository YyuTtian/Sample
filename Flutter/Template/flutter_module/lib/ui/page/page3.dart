import 'package:flutter/material.dart';

import '../../bridge/bridge.dart';
import '../../bridge/bridgeImpl.dart';

class Page3 extends StatefulWidget {
  const Page3({super.key});

  @override
  State<Page3> createState() => _Page3State();
}

class _Page3State extends State<Page3> {
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
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: GestureDetector(
            onTap: () {
              getNativeResult();
            },
            child: Text(
              "Page3",
              style: TextStyle(color: Colors.red, fontSize: 30),
            ),
          ),
        ),
      ),
    );
  }
}
