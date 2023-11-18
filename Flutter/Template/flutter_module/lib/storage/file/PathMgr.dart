import 'dart:io';

import 'package:path_provider/path_provider.dart';

class PathMgr {
  static Future<String> _getCacheFolderPath() async {
    return (await getTemporaryDirectory()).path;
  }

  static Future<String> _getFileFolderPath() async {
    return (await getApplicationDocumentsDirectory()).path;
  }

  static void saveStringToCacheFile(String fileName, String content) async {
    var file = File("${await _getCacheFolderPath()}/$fileName.txt");
    print("qglog cache filePath=${file.path}");
    file.writeAsString(content);
  }

  static Future<String> getStringFromCacheFile(String fileName) async {
    var file = File("${await _getCacheFolderPath()}/$fileName.txt");
    return file.readAsString();
  }

  static void saveStringToFile(String fileName, String content) async {
    var file = File("${await _getFileFolderPath()}/$fileName.txt");
    print("qglog file filePath=${file.path}");
    file.writeAsString(content);
  }

  static Future<String> getStringFromFile(String fileName) async {
    var file = File("${await _getFileFolderPath()}/$fileName.txt");
    return file.readAsString();
  }
}
