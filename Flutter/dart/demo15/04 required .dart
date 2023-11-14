/*
Null safety翻译成中文的意思是空安全。

required翻译成中文的意思是需要、依赖

required关键词:

    最开始 @required 是注解
    
    现在它已经作为内置修饰符。
    
    主要用于允许根据需要标记任何命名参数（函数或类），使得它们不为空。因为可选参数中必须有个 required 参数或者该参数有个默认值。

*/
String printUserInfo(String username, {int age = 10, String sex = "男"}) {
  //行参
  return "姓名:$username---性别:$sex--年龄:$age";
}

String printInfo(String username, {required int age, required String sex}) {
  //行参
  return "姓名:$username---性别:$sex--年龄:$age";
}

void main(args) {
  print(printUserInfo('张三'));

  print(printUserInfo('张三', age: 20, sex: "女"));

  //age 和 sex必须传入
  print(printInfo('张三', age: 22, sex: "女"));
}
