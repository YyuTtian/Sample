import 'package:flutter/material.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: Scaffold(body: HomePage()),
    );
  }
}

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () async {
        var resultValue = await showDialog(
            barrierDismissible: false,
            context: context,
            builder: (context) {
              return WillPopScope(child: SimpleDialogAndroid(), onWillPop: () async => false);
            });

        var result = resultValue ?? false;
        print("result=$result");
        if (result) {
          print('用户点击了确定');
        }
        return result;
      },
      child: Container(
        color: Colors.red,
      ),
    );
  }
}

class SimpleDialogAndroid extends StatelessWidget {
  SimpleDialogAndroid({super.key});

  @override
  Widget build(BuildContext context) {
    return UnconstrainedBox(
      child: SizedBox(
        width: 300,
        child: SimpleDialog(
          // title: Text(title),
          titlePadding: EdgeInsets.zero,
          contentPadding: EdgeInsets.zero,
          children: [
            Column(
              children: [
                const SizedBox(
                  height: 10,
                ),
                const Text("自定义标题"),
                const SizedBox(
                  height: 20,
                ),
                const Text("自定义内容"),
                const SizedBox(
                  height: 20,
                ),
                Row(
                  children: [
                    Expanded(
                        flex: 1,
                        child: TextButton(
                            onPressed: () {
                              Navigator.of(context).pop(false);
                            },
                            child: const Text("取消"))),
                    Expanded(
                        flex: 1,
                        child: TextButton(
                            onPressed: () {
                              Navigator.of(context).pop(true);
                            },
                            child: const Text("确定"))),
                  ],
                )
              ],
            )
          ],
        ),
      ),
    );
  }
}
