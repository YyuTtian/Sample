import 'package:flutter_module/bridge/bridge.dart';

class BridgeImpl extends NativeCallFlutter {
  @override
  FlutterBean loadFlutterData(int id) {
    print("flutter BridgeImpl loadFlutterData");
    return FlutterBean(id: 666, name: "Flutter返回的数据");
  }
}
