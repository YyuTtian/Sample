function sum(params1, params2) {
    return params1 + params2
}

console.log(sum(2, 3));


function f() {
    // arguments里面存着所有的参数
    // arguments是伪数组  没有push pop方法
    console.log(`参数的个数=${arguments.length}`);
    for (let index = 0; index < arguments.length; index++) {
        let params = arguments[index];
        console.log(`参数${index + 1}=${params}`);
    }
}

f(1, 2, 3, 4, 5)

let f2 = function (params1, params2) {
    return params1 + params2
}

console.log(f2(1, 2));

// 闭包
function out() {
    let num = 1
    function inner() {
        // 内部函数引用了外部函数的num
        console.log(`闭包 num=${num}`);
    }
    return inner // 返回内部函数
}

let func = out()
func() // 调用内部函数


// 剩余参数都给more
function fun2(a, b, ...more) {
    console.log(`a=${a} b=${b} more=${more}`);
}

fun2(1, 2, 3, 4, 5, 6, 7)


// 展开运算符
let a1 = [3, 4]
let a2 = [1, 2, ...a1] // a2的值是1 2 3 4
console.log(a2);

// b=10 参数的默认值
function f3(a, b = 10) {
}
f3(1, 2)
f3(1)



// 箭头函数
let f4 = (a, b) => { console.log(`箭头函数 a+b=${a + b}`); }
f4(1, 2)
// 只有一个参数和一句函数体的时候 (){}都可以省略
let f5 = x => x * x
console.log(f5(10))

setTimeout(() => { console.log(`延迟1s输出`); }, 1000)

// 如果函数体只有一句，返回的是对象的话 {}有歧义 需要加()
let f6 = () => ({ name: 'qgl', age: 18 })


// 解构赋值 把数组中的4个值赋值给四个变量
let [a11, b1, c1, d1] = [1, 2, 3, 4]
console.log(a11, b1, c1, d1);

// key一样就可以 顺序无所谓
// dog的name和前面的name重名  :重命名为dname
let { age, name, dog: { name: dname } } = { name: 'qgl', age: 18, dog: { name: '狗子' } }
console.log(`name=${name} age=${age} dog=${dname}`);



// 对象
let person = {
    name: '李四',
    age: 19,
    sayhi: function () {
        console.log(`hi`)
    }
}

console.log(person.name);
console.log(person['age']);
person.sayhi()

// 直接new Object()创建对象
var obj = new Object()
obj.name = '张三';
obj.age = 18;
obj.sex = '男';
obj.sayHi = function () {
    console.log('hi~');
}
console.log(person.name);
console.log(person['age']);
person.sayhi()

delete person.sayhi // 删除对象的方法


// 构造函数
function Person(name, age) {
    this.name = name
    this.age = age
}
let p1 = new Person('张三', 18)
console.log(`name=${p1.name} age=${p1.age}`);
// 把方法的定义从构造函数中抽出来
Person.prototype.sayHello = function () {
    console.log(`hello name=${this.name} age=${this.age}`);
}
p1.sayHello()



for (const key in p1) {
    console.log(`for in key=${key} value=${p1[key]}`);
}


// 随机数 包含min和max
function getRandom(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}
let l1 = 0
do {
    l1++
    console.log(`getRandom=${getRandom(1, 3)}`);
} while (l1 < 10);


let date = new Date()
console.log(`时间戳 date.getTime=${date.getTime()}`);



function countDown(time) {
    var nowTime = +new Date(); //没有参数，返回的是当前时间总的毫秒数
    var inputTime = +new Date(time); // 有参数，返回的是用户输入时间的总毫秒数
    var times = (inputTime - nowTime) / 1000; //times就是剩余时间的总的秒数

    var d = parseInt(times / 60 / 60 / 24); //天数
    d < 10 ? '0' + d : d;
    var h = parseInt(times / 60 / 60 % 24); //小时
    h < 10 ? '0' + h : h;
    var m = parseInt(times / 60 % 60); //分
    m < 10 ? '0' + m : m;
    var s = parseInt(times % 60); //秒
    s < 10 ? '0' + s : s;
    return d + '天' + h + '时' + m + '分' + s + '秒';
}
console.log(countDown('2024-11-09 18:29:00'));



let arr1 = [1, 2, 3, 4, 5, 6]
let arr2 = new Array()
// 检测类型是否为数组
console.log(arr1 instanceof Array);
arr2.push(7) // 添加到最后
arr2.pop() // 删除最后一个
arr2.unshift(0) // 添加到第一个
arr2.shift() // 删除第一个
arr2.splice(1, 1) // 从下标1开始删除 删除一个

arr1.reverse() // 颠倒数组
arr1.sort() // 对数组进行排序

arr1.sort(function (a, b) {
    return b - a;  //降序 
    // return a - b; //升序
}
)
console.log(arr1);

// 数组中的每个元素用 - 拼接 返回一个新的字符串
console.log(arr1.join('-'));


[1, 2, 3, 4, 5, 6, 7].forEach((item, index, self) => { console.log(`item=${item} index=${index} self=${self}`); })
// let newArr = [1,2,3,4,5,6,7].filter((item,index,self)=>{return item>3})
let newArr = [1, 2, 3, 4, 5, 6, 7].filter(item => item > 3)
newArr.forEach((item) => { console.log(`newArr item=${item}`); })

let result11 = [2, 4, 6, 8].map((item, index, self) => { return item * item })
console.log(`result=` + result11);

// 有满足的就返回true
let b11 = [1, 2, 3].some((item, index, self) => { return item > 2 })
// 都满足才返回true
let b12 = [1, 2, 3].every((item, index, self) => { return item > 2 })
console.log(`b11=${b11} b12=${b12}`);



