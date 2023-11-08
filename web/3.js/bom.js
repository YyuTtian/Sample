var num = 10
// 全局属性会变成window的一个属性
console.log(window.num);

function f() {
    console.log(`f函数`);
}
// 全局函数会变成window的一个方法
window.f()


window.onload = () => {
    console.log(`页面全部加载完毕`);

    console.log(`屏幕宽度=${window.innerWidth} 屏幕高度=${window.innerHeight}`);
}

window.addEventListener('DOMContentLoaded', () => {
    // DOMContentLoaded 比 window.onload 要快
    console.log('dom加载完毕');
})


window.onresize = () => {
    console.log(`widnow resize`);
}

let id1 = setTimeout(() => {
    console.log(`2s后执行`);
}, 2000);
// 取消timeout
clearTimeout(id1)



let id2 = setInterval(() => {
    console.log(`每秒都执行一次`);
}, 1000);
// 取消interval
clearInterval(id2)

/**

属性
location.href	获取或者设置整个URL
location.host	返回主机（域名）www.baidu.com
location.port	返回端口号，如果未写返回空字符串
location.pathname	返回路径
location.search	返回参数
location.hash	返回片段 #后面内容常见于链接 锚点

方法
location.assign()	跟href一样，可以跳转页面（也称为重定向页面）
location.replace()	替换当前页面，因为不记录历史，所以不能后退页面
location.reload()	重新加载页面，相当于刷新按钮或者 f5 ，如果参数为true 强制刷新 ctrl+f5
*/
// location.href = "https://www.baidu.com"

let id3 = setTimeout(() => {
    // 3秒后页面刷新一次
    // location.reload(是否强制刷新)
    location.reload()
    clearTimeout(id3)
}, 3000);


let userAgent = navigator.userAgent
console.log(`userAgent=${userAgent}`);

/**
back()	    可以后退功能
forward()	前进功能
go(参数)	前进后退功能，参数如果是 1 前进1个页面 如果是 -1 后退1个页面
 */
history.back()