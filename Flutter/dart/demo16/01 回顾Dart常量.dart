/*
Dart 常量: final 和 const修饰符  


  const 声明的常量是在编译时确定的，永远不会改变

  final 声明的常量允许声明后再赋值，赋值后不可改变，final 声明的变量是在运行时确定的;

  final不仅有const的编译时常量的特性，最重要的它是运行时常量，并且final是惰性初始化，即在运行时第一次使用前才初始化


*/

void main() {
//const常量

//const PI=3.14;
// PI=3.14159;  //const定义的常量没法改变
//print(PI);

// final 常量

// final PI=3.14;
// print(PI);

//final和const区别：final 可以开始不赋值 只能赋一次 ; 而final不仅有const的编译时常量的特性，最重要的它是运行时常量，并且final是惰性初始化，即在运行时第一次使用前才初始化

  final a;
  a = 13;
//  a=14;
  print(a);

  final d = new DateTime.now();
}
