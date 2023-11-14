/*
Null safety翻译成中文的意思是空安全。

required翻译成中文的意思是需要、依赖

required关键词:

    最开始 @required 是注解
    
    现在它已经作为内置修饰符。
    
    主要用于允许根据需要标记任何命名参数（函数或类），使得它们不为空。因为可选参数中必须有个 required 参数或者该参数有个默认值。

*/

//表示 name 和age 是必须传入的命名参数
class Person {
  String name;
  int age;
  Person({required this.name,required this.age});  //表示 name 和age 必须传入

  String getName() {
    return "${this.name}---${this.age}";
  }
}


void main(args) {
   Person p=new Person(
     name: "张三",
     age: 20
   );
   print(p.getName());
}
