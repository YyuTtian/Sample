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
    return ListView.builder(
      itemCount: 50,
      itemBuilder: (context, index) {
        // 滑动删除作用
        return Dismissible(
            // 从右往左滑
            direction: DismissDirection.endToStart,
            confirmDismiss: (direction) async {
              return await showDialog(
                  context: context,
                  builder: (context) {
                    return const DeleteDialog();
                  });
            },
            // 从左往右
            background: Container(
              color: Colors.blue,
              padding: const EdgeInsets.all(10),
              child: const Icon(Icons.add),
            ),

            // 从右往左
            secondaryBackground: Container(
              color: Colors.black,
              padding: const EdgeInsets.all(10),
              child: const Icon(
                Icons.delete,
                color: Colors.red,
              ),
            ),
            key: UniqueKey(),
            child: SizedBox(
              height: 50,
              child: Center(
                child: Text("第$index个"),
              ),
            ));
      },
    );
  }
}

class DeleteDialog extends StatelessWidget {
  const DeleteDialog({super.key});

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text("确认"),
      content: const Text("确认要删除这一项吗？"),
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
            child: const Text(
              "确定",
              style: TextStyle(color: Colors.red),
            ))
      ],
    );
  }
}
