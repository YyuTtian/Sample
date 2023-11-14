/*
dart:core 库中identical 函数的用法介绍如下。

用法:
bool identical(
   Object? a,    
   Object? b   
)
检查两个引用是否指向同一个对象。

var o = new Object();

  var isIdentical = identical(o, new Object()); // false, different objects.
  print(isIdentical);

  isIdentical = identical(o, o); // true, same object
  print(isIdentical);

  isIdentical = identical(const Object(), const Object()); // true, const canonicalizes
  print(isIdentical);

  isIdentical = identical([1], [1]); // false
  print(isIdentical);

  isIdentical = identical(const [1], const [1]); // true
  print(isIdentical);

  isIdentical = identical(const [1], const [2]); // false
  print(isIdentical);
  
  isIdentical = identical(2, 1 + 1); // true, integers canonicalizes
  print(isIdentical);
*/


void main(){
  
  // var o1 = new Object();
  // var o2 = new Object();

  // print(identical(o1,o2));  //false  不共享存储空间

  // print(identical(o1,o1));   //true 共享存储空间



  // var o1 = Object();
  // var o2 = Object();
  // print(identical(o1,o2));  //false
  // print(identical(o1,o1));  //true



  //表示实例化常量构造函数
  //o1 和 o2共享了存储空间
  // var o1 = const Object();
  // var o2 = const Object();
  // print(identical(o1,o2));  //true 共享存储空间
  // print(identical(o1,o1));  //true 共享存储空间


  // print(identical([2],[2])); //false

  // var a=[2];
  // var b=[2];
  // print(identical(a,b)); //false 不共享存储空间



  // print(identical(const [2],const [2])); //true

  const a=[2];
  const b=[2];
  print(identical(a,b)); //true 共享存储空间



  const c=[2];
  const d=[3];
  print(identical(c,d)); //false  不共享存储空间


}
// 发现：const关键词在多个地方创建相同的对象的时候，内存中只保留了一个对象


// 共享存储空间条件：1、常量   2、值相等