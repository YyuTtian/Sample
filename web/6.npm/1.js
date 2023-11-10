import axiosUtil from "./axiosUtil.js";

import moment from "moment/moment.js";

async function test() {
    // let result = await axiosUtil.get('http://ajax-api.itheima.net/api/province')
    let result = await axiosUtil.get('https://www.baidu.com')
    console.log(result.data);
}

test()

let date = new Date()
console.log(moment(date).format('YYYY-MM-DD'));
console.log(moment(date).format('YYYY-MM-DD HH:mm:ss.SSS'));