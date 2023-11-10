
// 引入其他js文件 然后起个别名
import common from './规范.js'
// 使用的时候用 别名.属性 别名.方法 可以防止多个js文件中的重名问题
console.log(common.commonField);
console.log(common.sum(1, 2));