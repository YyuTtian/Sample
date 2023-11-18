import 'package:flutter_module/storage/sp/SpUtils.dart';

class SetMgr {
  static SetMgr? _instance;

  SetMgr._internal() {
    _instance = this;
  }

  factory SetMgr() => _instance ?? SetMgr._internal();

  void setConfig(String config) {
    SpUtils.setString("config", config);
  }

  Future<String?> getConfig() {
    return SpUtils.getString("config");
  }
}
