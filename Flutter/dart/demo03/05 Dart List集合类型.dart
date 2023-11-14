/*
Dart数据类型： List（数组/集合）
*/
void main() {
  //1、第一种定义List的方式

  // var l1=["张三",20,true];
  // print(l1);  //[张三, 20, true]
  // print(l1.length);  //3
  // print(l1[0]); //张三
  // print(l1[1]); //20

  //2、第二种定义List的方式 指定类型

  // var l2=<String>["张三","李四"];
  // print(l2);

  // var l3 = <int>[12, 30];
  // print(l3);

  //3、第三种定义List的方式  增加数据 ,通过[]创建的集合它的容量可以变化

  // var l4 = [];
  // print(l4);
  // print(l4.length);

  // l4.add("张三");
  // l4.add("李四");
  // l4.add(20);
  // print(l4);
  // print(l4.length);

  // var l5 = ["张三", 20, true];
  // l5.add("李四");
  // l5.add("zhaosi");
  // print(l5);

  //4、第四种定义List的方式

  //  var l6=new List();  //在新版本的dart里面没法使用这个方法了

  // var l6=List.filled(2, "");  //创建一个固定长度的集合
  // print(l6);
  // print(l6[0]);

  // l6[0]="张三";   //修改集合的内容
  // l6[1]="李四";
  // print(l6);  //[张三, 李四]
  // l6.add("王五");  //错误写法  通过List.filled创建的集合长度是固定  没法增加数据

  // 通过List.filled创建的集合长度是固定
  // var l6=List.filled(2, "");
  // print(l6.length);
  // l6.length=0;  //修改集合的长度   报错

  // var l7=<String>["张三","李四"];
  // print(l7.length);  //2
  // l7.length=0;  //可以改变的
  // print(l7);  //[]

  var l8 = List<String>.filled(2, "");
  l8[0] = "string";
  // l8[0]=222;
  print(l8);
}
