import { add } from './add/add.js'

console.log(add(1, 2));

// 把css文件加入打包
import css from '../css/index.css'

// 把less文件加入打包
import less from '../less/index.less'

// 把图片当作模块引入
import imgObj from '../assets/1.jpeg'

let img = document.createElement('img')
img.src = imgObj
document.body.appendChild(img)