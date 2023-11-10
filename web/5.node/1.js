// 使用es6的导入方式  目录下要有一个package.json文件 type的值为module
import fs from 'fs'

fs.access('test.txt', err => {
    if (err) {
        console.log('文件不存在');
    } else {
        console.log('文件存在');


        fs.writeFile('test.txt', 'hello', err => {
            if (err) {
                console.log('写入失败');
            } else {
                console.log('写入成功');

                fs.readFile('test.txt', (err, data) => {
                    console.log('读取 ' + data.toString());
                })
            }
        })
    }
})