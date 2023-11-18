import 'package:flutter/material.dart';
import 'package:flutter_module/net/core/HttpUtil.dart';
import 'package:flutter_module/storage/db/DBUtils.dart';
import 'package:flutter_module/storage/file/PathMgr.dart';
import 'package:flutter_module/storage/sp/SetMgr.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

class Page1 extends StatefulWidget {
  const Page1({super.key});

  @override
  State<Page1> createState() => _Page1State();
}

class _Page1State extends State<Page1> {
  @override
  void initState() {
    super.initState();
  }

  void test() async {
    SetMgr().setConfig("sdfafasdf");

    var result = await SetMgr().getConfig();
    print("qglog config is $result");

    PathMgr.saveStringToCacheFile("log", "cache string");
    print("qglog cache fileString=${await PathMgr.getStringFromCacheFile("log")}");

    PathMgr.saveStringToFile("log", "file string");
    print("qglog file fileString=${await PathMgr.getStringFromFile("log")}");

    DBUtils.test();
  }

  @override
  Widget build(BuildContext context) {
    print("qglog  page1 build");

    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: GestureDetector(
            onTap: () {
              click();
            },
            child: Container(
              color: Colors.blue,
              width: 340.w,
              height: 100.h,
              child: Text(
                "Page1",
                style: TextStyle(color: Colors.red, fontSize: 30),
              ),
            ),
          ),
        ),
      ),
    );
  }

  void click() async {
    var result = await HttpUtil.get("https://www.baidu.com");
    print("get result = $result");
  }
}
