import 'package:shared_preferences/shared_preferences.dart';

class SpUtils {
  static void setInt(String key, int value) async {
    final SharedPreferences sp = await SharedPreferences.getInstance();
    sp.setInt(key, value);
  }

  static Future<int?> getInt(String key) async {
    final SharedPreferences sp = await SharedPreferences.getInstance();
    return sp.getInt(key);
  }

  static void setDouble(String key, double value) async {
    final SharedPreferences sp = await SharedPreferences.getInstance();
    sp.setDouble(key, value);
  }

  static Future<double?> getDouble(String key) async {
    final SharedPreferences sp = await SharedPreferences.getInstance();
    return sp.getDouble(key);
  }

  static void setBool(String key, bool value) async {
    final SharedPreferences sp = await SharedPreferences.getInstance();
    sp.setBool(key, value);
  }

  static Future<bool?> getBool(String key) async {
    final SharedPreferences sp = await SharedPreferences.getInstance();
    return sp.getBool(key);
  }

  static void setString(String key, String value) async {
    final SharedPreferences sp = await SharedPreferences.getInstance();
    sp.setString(key, value);
  }

  static Future<String?> getString(String key) async {
    final SharedPreferences sp = await SharedPreferences.getInstance();
    return sp.getString(key);
  }
}
