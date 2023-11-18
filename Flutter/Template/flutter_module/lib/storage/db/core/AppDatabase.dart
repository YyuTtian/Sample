// required package imports

import 'dart:async';

// 执行命令后要把下面这两个加上 不然生成的文件报错
import 'package:floor/floor.dart';
import 'package:sqflite/sqflite.dart' as sqflite;

import 'Person.dart';
import 'PersonDao.dart';

part 'AppDatabase.g.dart';

// flutter packages pub run build_runner build
@Database(version: 1, entities: [Person])
abstract class AppDatabase extends FloorDatabase {
  PersonDao get personDao;
}
