/*
Null safety翻译成中文的意思是空安全。

late 关键字主要用于延迟初始化。

*/
class Person {
  late String name;
  late int age;
  void setName(String name, int age) {
    this.name = name;
    this.age = age;
  }

  String getName() {
    return "${this.name}---${this.age}";
  }
}

void main(args) {
  Person p = new Person();
  p.setName("张三", 20);
  print(p.getName());
}
