import 'package:flutter/cupertino.dart';
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
    return const View5();
  }
}

class View1 extends StatelessWidget {
  const View1({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          ElevatedButton(
              onPressed: () async {
                await showDialog(
                  context: context,
                  barrierDismissible: true, //点击黑色区域自动消失
                  builder: (context) {
                    return Center(
                      child: Container(
                        width: 200,
                        height: 200,
                        color: Colors.blue,
                        child: const Text("dialog"),
                      ),
                    );
                  },
                );
              },
              child: const Text("弹窗"))
        ],
      ),
    );
  }
}

class View2 extends StatelessWidget {
  const View2({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          ElevatedButton(
              onPressed: () async {
                var result = await showDialog(
                    barrierDismissible: false,
                    context: context,
                    builder: (context) {
                      return SimpleDialogAndroid();
                    });

                print("dialog result=${result}");
              },
              child: const Text("弹窗"))
        ],
      ),
    );
  }
}

class View3 extends StatelessWidget {
  const View3({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          ElevatedButton(
              onPressed: () async {
                // 底部弹出对话框
                var result = await showModalBottomSheet(
                    context: context,
                    builder: (context) {
                      return SimpleDialogAndroid();
                    });

                print("dialog result=${result}");
              },
              child: const Text("弹窗"))
        ],
      ),
    );
  }
}

class View4 extends StatelessWidget {
  const View4({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          ElevatedButton(
              onPressed: () async {
                // 底部弹出对话框
                var result = await showModalBottomSheet(
                    context: context,
                    isScrollControlled: true,
                    builder: (context) {
                      return DraggableScrollableSheet(
                          expand: false,
                          builder: (context, scrollController) {
                            return Container(
                              color: Colors.grey,
                              child: ListView.builder(itemBuilder: (context, index) {
                                return Center(
                                  child: Text("第$index个"),
                                );
                              }),
                            );
                          });
                    });

                print("dialog result=$result");
              },
              child: const Text("弹窗"))
        ],
      ),
    );
  }
}

class View5 extends StatelessWidget {
  const View5({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          ElevatedButton(
              onPressed: () async {
                // 底部弹出对话框iOS风格
                var result = await showCupertinoModalPopup(
                    context: context,
                    builder: (context) {
                      return CupertinoActionSheet(
                        title: const Text("title"),
                        message: const Text("message"),
                        actions: [
                          CupertinoActionSheetAction(
                            isDefaultAction: true, // 默认选项 加粗
                            onPressed: () {},
                            child: const Text("选项1"),
                          ),
                          CupertinoActionSheetAction(
                            isDestructiveAction: true, // 字变红
                            onPressed: () {},
                            child: const Text("选项2"),
                          ),
                        ],
                        cancelButton: CupertinoActionSheetAction(onPressed: () {}, child: const Text("取消")),
                      );
                    });

                print("dialog result=${result}");
              },
              child: const Text("弹窗"))
        ],
      ),
    );
  }
}

class AlertDialogAndroid extends StatelessWidget {
  String title = "";
  String content = "";

  AlertDialogAndroid(this.title, this.content, {super.key});

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(title),
      content: Text(content),
      actions: [
        TextButton(
            onPressed: () {
              Navigator.of(context).pop(false);
            },
            child: const Text("取消")),
        TextButton(
            onPressed: () {
              Navigator.of(context).pop(true);
            },
            child: const Text("确定")),
      ],
    );
  }
}

class AlertDialogiOS extends StatelessWidget {
  String title = "";
  String content = "";

  AlertDialogiOS(this.title, this.content, {super.key});

  @override
  Widget build(BuildContext context) {
    return CupertinoAlertDialog(
      title: Text(title),
      content: Text(content),
      actions: [
        CupertinoDialogAction(
            onPressed: () {
              Navigator.of(context).pop(false);
            },
            child: const Text("取消")),
        CupertinoDialogAction(
            onPressed: () {
              Navigator.of(context).pop(true);
            },
            child: const Text(
              "确定",
              style: TextStyle(color: Colors.red),
            )),
      ],
    );
  }
}

class SimpleDialogAndroid extends StatelessWidget {
  const SimpleDialogAndroid({super.key});

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
