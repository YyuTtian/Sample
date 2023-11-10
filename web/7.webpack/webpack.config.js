// 最新 node 核心包的导入写法
import { fileURLToPath } from 'node:url'
import { dirname } from 'node:path'

import path from 'node:path'

import HtmlWebpackPlugin from 'html-webpack-plugin'


// 获取 __filename 的 ESM 写法
const __filename = fileURLToPath(import.meta.url)
// 获取 __dirname 的 ESM 写法
const __dirname = dirname(fileURLToPath(import.meta.url))

export default {
    mode: 'development', // 开发模式(不会极致压缩)    production 生产模式 (极致压缩)


    devServer: {
        port: 3000, // 端口号
        open: true // 启动后自动打开浏览器
    },

    entry: './src/index.js', // 修改打包的入口
    output: {
        path: path.resolve(__dirname, 'dist'), // 修改打包的输出目录
        filename: 'index.js', // 修改打包的输出文件的名字
    },
    plugins: [new HtmlWebpackPlugin({ // 生成html插件
        template: 'public/index.html', // 原来的html路径  参考的模板
    })],
    module: {
        rules: [
            // 把css文件加入打包
            {
                test: /\.css$/i, // 以.css结尾的文件   i表示不区分大小写
                use: ["style-loader", "css-loader"], // 加载器的使用顺序 数组里从后往前用
            },
            // 把less文件加入打包
            {
                test: /\.less$/i,
                use: ["style-loader", "css-loader", "less-loader"],
            },
            // webpack5 把图片加载器内置了
            {
                test: /\.(png|jpg|jpeg|gif)$/i,
                type: 'asset'
                // 8kb以下 转成base64字符串
                // 8kb以上 把图片复制到dist目录下  重新命名 防止重名
            },
            // 加载字体
            {
                test: /\.(eot|svg|ttf|woff|woff2)$/i,
                type: 'asset/resource',// 当作静态资源直接复制过去
                generator: {
                    filename: 'font/[name].[hash:6][ext]'
                    // [name] 表示原来的名字
                    // [hash:6] 随机生成6位hash值  防止重名
                    // [ext] 用原来的扩展名
                }
            }
        ],
    },

};