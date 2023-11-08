
// DOM


let div1 = document.getElementById('div1')
// console.log(div1)
// console.dir(div1)

let divList = document.getElementsByTagName('div')

let one = document.getElementsByClassName('one')
// console.log(one);

// 根据选择器查询  返回符合的第一个元素
let calssOne = document.querySelector('.one')
console.log(calssOne);

// 根据选择器查询  返回所有符合的元素
let all = document.querySelectorAll('.one')

let body = document.body
let html = document.documentElement

console.log(`body=${body}`);
console.log(`html=${html}`);
console.dir(html)

/*
onclick	    鼠标点击左键触发
onmouseover	鼠标经过触发
onmouseout	鼠标离开触发
onfocus 	获得鼠标焦点触发
onblur	    失去鼠标焦点触发
onmousemove	鼠标移动触发
onmouseup	鼠标弹起触发
onmousedown	鼠标按下触发 
 */
let divObj = document.querySelector('#div1')
divObj.onclick = function (event) {
    console.log(`点击了`);
}
divObj.addEventListener('mouseover', function (event) {
    console.log(`经过了`);
})

divObj.innerText = '<strong>innerText</strong>' // 标签也当作文本显示
divObj.innerHTML = '<strong>innerHtml</strong>' // 标签会被理解成粗体

divObj.style.backgroundColor = '#ffff00' // 通过js改变背景色

divObj.className = "132" // 修改元素的类名


let classValue = divObj.getAttribute('class') // 获取属性
console.log(`classValue=${classValue}`);
divObj.setAttribute('class', 456) // 设置属性
divObj.removeAttribute('class') // 删除属性

// 自定义数据
divObj.dataset.customValue = "自定义数据"
let customValue = divObj.dataset.customValue
console.log(`customValue=${customValue}`);


// 父节点parentNode   子节点列表childNodes   子节点列表children
let div1Child = document.querySelector('#div1Child')
console.log(`div1Child=${div1Child}`);
let parentNode = div1Child.parentNode
let childNodes = div1Child.childNodes
let children = div1Child.children
console.log(`parentNode=${parentNode}`);
for (let index = 0; index < childNodes.length; index++) {
    console.log(`childNodes${index + 1}=${childNodes[index]}`);
}

// 第一个子节点
div1Child.children[0]

// 最后一个子节点
div1Child.children[div1Child.children.length - 1]

// 上一个兄弟节点
div1Child.previousSibling
// 下一个兄弟节点
div1Child.nextSibling

// 创建节点
let div = document.createElement('div')
// 添加节点  添加到子节点末尾
div1Child.appendChild(div)
// 添加到otherChild前面
div1Child.insertBefore(otherChild, div)
// 删除节点
div1Child.removeChild(node对象)
// 复制节点
div1Child.cloneNode(是否深度克隆)