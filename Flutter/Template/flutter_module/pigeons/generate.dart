import 'package:pigeon/pigeon.dart';

// flutter pub run pigeon --input pigeons/generate.dart
@ConfigurePigeon(PigeonOptions(
  dartOut: "./lib/bridge/bridge.dart",
  kotlinOut: "./pigeons/Bridge.kt",
))
class NativeBean {
  final int id;
  final String name;

  NativeBean(this.id, this.name);
}

class FlutterBean {
  final int id;
  final String name;

  FlutterBean(this.id, this.name);
}

@HostApi()
abstract class FlutterCallNative {
  NativeBean loadNativeData(int id);
}

@FlutterApi()
abstract class NativeCallFlutter {
  FlutterBean loadFlutterData(int id);
}
