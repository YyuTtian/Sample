/*
Dart中一个类实现多个接口：
*/

abstract class A {
  late String name;
  printA();
}

abstract class B {
  printB();
}

class C implements A, B {
  @override
  late String name;
  @override
  printA() {
    print('printA');
  }

  @override
  printB() {
    // TODO: implement printB
    return null;
  }
}

void main() {
  C c = new C();
  c.printA();
}
