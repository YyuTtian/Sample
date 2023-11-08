// alert('弹框')

console.log('打印日志')

// let inputText = prompt("输入姓名")
// console.log(typeof (inputText)); // inputText类型是string
// console.log(inputText.length);
// console.log(inputText)

let age = 18
// NaN not a number
console.log('是不是数字' + !isNaN(age))

let str = `模板字符串  age=${age} `
console.log(str);

// 格式转换
Number('546465')
String(123)
parseInt('5454')
parseFloat('6465.256')
age.toString()

console.log(`最大值=${Number.MAX_VALUE} 最小值=${Number.MIN_VALUE}`);

// 小数运算的结果是不精准的  0.30000000000000004
console.log(`小数相加 0.1+0.2=${0.1 + 0.2}`);
// 先将小数转成整数 算完之后再除以10转成小数  0.3
console.log(`小数相加 0.1+0.2=${(0.1 * 10 + 0.2 * 10) / 10}`);

// =	赋值	把右边给左边
// ==	判断	判断两边值是否相等(注意此时有隐士转换)
// ===	全等	判断两边的值和数据类型是否完全相同
console.log(18 == '18');		//true
console.log(18 === '18');		//false

if (2 > 1) {

}
else if (3 > 2) {

}
else {

}

let r = 1 > 2 ? 3 : 4

let key = 'key'
switch (key) {
    case 'key':
        console.log('key key key ');
        break;

    default:
        break;
}

for (let index = 0; index < 10; index++) {
    console.log(`for ${index}`);
}

let while1 = 0
while (while1 < 100) {
    while1++
}

do {
    while1++
    if (while1 > 150) {
        break
    } else if (while1 > 120) {
        continue
    }
} while (while1 < 200);

let array1 = [231, 6546, 31, 3, 13, 1, 3]
let array2 = new Array()
for (let index = 0; index < array1.length; index++) {
    const element = array1[index];
    console.log(`数组元素${element}`)
}

let arr1 = []
arr1.push('sdfasfads')
arr1.push(446) // 加在后面
arr1.unshift(11) // 加在前面

arr1.pop() // 删除最后一个
arr1.shift() // 删除第一个
arr1.splice(1, 1) // 从下标1开始删除 删除一个

for (let i = 0; i < arr1.length; i++) {
    console.log(arr1[i])
}

let sortList = [3, 2, 1, 5, 4, 0]
sortList.sort(function (a, b) {
    return a - b // 升序

    // return b-a // 降序
})
console.log(sortList)