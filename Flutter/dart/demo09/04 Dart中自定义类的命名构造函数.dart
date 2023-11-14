
/*
dart里面构造函数可以写多个

注意：最新版本的dart中需要初始化不可为null的实例字段，如果不初始化的话需要在属性前面加上late
*/
class Person {
  late String name;
  late int age;
  //默认构造函数的简写
  Person(this.name, this.age);
  Person.now() {
    print('我是命名构造函数');
  }
  Person.setInfo(String name, int age) {
    this.name = name;
    this.age = age;
  }
  void printInfo() {
    print("${this.name}----${this.age}");
  }
}

void main() {
  // var d=new DateTime.now();   //实例化DateTime调用它的命名构造函数
  // print(d);

  //Person p1=new Person('张三', 20);   //默认实例化类的时候调用的是 默认构造函数

  //Person p1=new Person.now();   //命名构造函数

  Person p1 = new Person.setInfo('李四', 30);
  p1.printInfo();
}
