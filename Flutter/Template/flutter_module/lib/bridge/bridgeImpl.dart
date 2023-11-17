import 'package:flutter_module/bridge/bridge.dart';

class BridgeImpl extends NativeCallFlutter {
  @override
  FlutterBean loadFlutterData(int id) {
    return FlutterBean(id: 666, name: "Flutter返回的数据");
  }
}
