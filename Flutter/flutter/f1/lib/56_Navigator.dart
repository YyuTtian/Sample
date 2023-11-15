import 'package:flutter/material.dart';

void main() => runApp(const MyApp());

class Routes {
  static const page2 = '/page2';
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      routes: <String, WidgetBuilder>{
        Routes.page2: (context) => Page2(),
      },
      onGenerateRoute: (setting) {
        if (setting.name == Routes.page2) {
          // 匹配 /page2  自己额外处理
          return MaterialPageRoute(builder: (context) {
            return Page2();
          });
        }
      },
      onUnknownRoute: (setting) {
        return MaterialPageRoute(
            builder: (context) => Container(
                  color: Colors.red,
                  child: const Center(child: Text("404")),
                ));
      },
      home: const Scaffold(body: HomePage()),
    );
  }
}

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: ElevatedButton(
          onPressed: () async {
            var result = await Navigator.of(context).pushNamed(Routes.page2);
            print("页面返回结果=$result");
          },
          child: const Text("打开页面")),
    );
  }
}

class Page2 extends StatelessWidget {
  const Page2({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.yellow,
      child: Center(
        child: Material(
          color: Colors.transparent,
          child: TextButton(
              onPressed: () {
                Navigator.of(context).pop("返回值");
              },
              child: Text("退出界面")),
        ),
      ),
    );
  }
}
