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
    return TextField2();
  }
}

class TextField1 extends StatelessWidget {
  const TextField1({super.key});

  @override
  Widget build(BuildContext context) {
    return TextField(
      style: const TextStyle(color: Colors.blue),

      // expands: true表示大小跟父容器一样
      // minLines: null,
      // maxLines: null,
      // expands: true,

      // 键盘类型
      keyboardType: TextInputType.number,
      // 修改右下角回车键
      textInputAction: TextInputAction.search,
      // 回车键按下触发
      onSubmitted: (value) {
        print(value);
      },

      // 光标
      cursorColor: Colors.grey,
      cursorWidth: 5,
      cursorRadius: const Radius.circular(5),

      // 输入用*显示  默认圆点
      obscureText: true,
      obscuringCharacter: "*",

      decoration: InputDecoration(
          // 前面的图标
          icon: const Icon(Icons.add),
          // 前缀图标
          prefixIcon: const Icon(Icons.lock),
          // 前缀文本
          prefixText: "https://",

          // 位置和counterText一样  可以是任意组件
          counter: const Text('任意组件'),
          // 计数器文本内容
          counterText: "0/100",
          // 后缀图标
          suffixIcon: const Icon(Icons.visibility),
          // 后缀组件
          suffix: GestureDetector(
            onTap: () {
              print("点击了clear");
            },
            child: const Icon(Icons.clear),
          ),
          labelText: "labelText",
          hintText: "提示文字",
          hintStyle: const TextStyle(color: Colors.red),
          helperText: 'helpText',

          // 自定义边框
          // enabledBorder: OutlineInputBorder(
          //     borderRadius: BorderRadius.circular(10),
          //     borderSide: BorderSide(width: 8, color: Colors.red)),

          border: const OutlineInputBorder(),

          // 输入框的背景色
          filled: true,
          fillColor: Colors.red,

          // 内容距离边框的距离
          contentPadding: const EdgeInsets.all(10)),
    );
  }
}

class TextField2 extends StatefulWidget {
  const TextField2({super.key});

  @override
  State<TextField2> createState() => _TextField2State();
}

class _TextField2State extends State<TextField2> {
  final _controller = TextEditingController();

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        TextField(
          controller: _controller,
        ),
        SizedBox(
          width: 200,
          height: 200,
          child: Center(
            child: GestureDetector(
              child: const Text("操作"),
              onTap: () {
                _controller.clear();
                _controller.value.toString();
              },
            ),
          ),
        )
      ],
    );
  }
}
