(function(){function r(e,n,t){function o(i,f){if(!n[i]){if(!e[i]){var c="function"==typeof require&&require;if(!f&&c)return c(i,!0);if(u)return u(i,!0);var a=new Error("Cannot find module '"+i+"'");throw a.code="MODULE_NOT_FOUND",a}var p=n[i]={exports:{}};e[i][0].call(p.exports,function(r){var n=e[i][1][r];return o(n||r)},p,p.exports,r,e,n,t)}return n[i].exports}for(var u="function"==typeof require&&require,i=0;i<t.length;i++)o(t[i]);return o}return r})()({1:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.getCtx = void 0;
function getCtx() {
    let currentApplication = Java.use('android.app.ActivityThread').currentApplication();
    let context = currentApplication.getApplicationContext();
    return context;
}
exports.getCtx = getCtx;
},{}],2:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.exportKeyStore = exports.checkHttpStack = void 0;
const java_util_1 = require("./java_util");
function checkHttpStack() {
    Java.perform(function () {
        let javaUtil = (0, java_util_1.getJavaUtil)();
        // http write
        let outputClass = Java.use("java.net.SocketOutputStream");
        outputClass.socketWrite0.implementation = function (fd, buff, off, len) {
            return this.socketWrite0(fd, buff, off, len);
        };
        // http read
        let inputClass = Java.use("java.net.SocketInputStream");
        inputClass.socketRead0.implementation = function (fd, buff, off, len, timeout) {
            // console.log("Http_Read")
            // console.log(javaUtil.showStack())
            return this.socketRead0(fd, buff, off, len, timeout);
        };
        let nativeCrypto = Java.use("com.android.org.conscrypt.NativeCrypto");
        // https write
        nativeCrypto.SSL_write.implementation = function (ssl, ssl_holder, fd, shc, b, off, len, writeTimeoutMillis) {
            console.log("SSL_write");
            // 打印堆栈 
            // 看哪里在写数据 哪里就是请求发送的地方
            console.log(javaUtil.showStack());
            return this.SSL_write(ssl, ssl_holder, fd, shc, b, off, len, writeTimeoutMillis);
        };
        // https read
        nativeCrypto.SSL_read.implementation = function (ssl, ssl_holder, fd, shc, b, off, len, readTimeoutMillis) {
            // console.log("SSL_read")
            // 打印堆栈 
            // 看哪里在读取数据 哪里就是请求返回的地方
            // console.log(javaUtil.showStack())
            return this.SSL_read(ssl, ssl_holder, fd, shc, b, off, len, readTimeoutMillis);
        };
    });
}
exports.checkHttpStack = checkHttpStack;
function exportKeyStore() {
    Java.perform(function () {
        function storeP12(pri, p7, p12Path, p12Password) {
            var X509Certificate = Java.use("java.security.cert.X509Certificate");
            var p7X509 = Java.cast(p7, X509Certificate);
            var chain = Java.array("java.security.cert.X509Certificate", [p7X509]);
            var ks = Java.use("java.security.KeyStore").getInstance("PKCS12", "BC");
            ks.load(null, null);
            ks.setKeyEntry("client", pri, Java.use('java.lang.String').$new(p12Password).toCharArray(), chain);
            try {
                var out = Java.use("java.io.FileOutputStream").$new(p12Path);
                ks.store(out, Java.use('java.lang.String').$new(p12Password).toCharArray());
            }
            catch (exp) {
                console.log(exp);
            }
        }
        //在服务器校验客户端的情形下，帮助dump客户端证书，并保存为p12的格式，证书密码为r0ysue
        Java.use("java.security.KeyStore$PrivateKeyEntry").getPrivateKey.implementation = function () {
            var result = this.getPrivateKey();
            var packageName = Java.use("android.app.ActivityThread").currentApplication().getApplicationContext().getPackageName();
            storeP12(this.getPrivateKey(), this.getCertificate(), '/sdcard/Download/' + packageName + uuid(10, 16) + '.p12', 'r0ysue');
            var message = {};
            message["function"] = "dumpClinetCertificate=>" + '/sdcard/Download/' + packageName + uuid(10, 16) + '.p12' + '   pwd: r0ysue';
            message["stack"] = Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new());
            var data = Memory.alloc(1);
            send(message, Memory.readByteArray(data, 1));
            return result;
        };
        Java.use("java.security.KeyStore$PrivateKeyEntry").getCertificateChain.implementation = function () {
            var result = this.getCertificateChain();
            var packageName = Java.use("android.app.ActivityThread").currentApplication().getApplicationContext().getPackageName();
            storeP12(this.getPrivateKey(), this.getCertificate(), '/sdcard/Download/' + packageName + uuid(10, 16) + '.p12', 'r0ysue');
            var message = {};
            message["function"] = "dumpClinetCertificate=>" + '/sdcard/Download/' + packageName + uuid(10, 16) + '.p12' + '   pwd: r0ysue';
            message["stack"] = Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new());
            var data = Memory.alloc(1);
            send(message, Memory.readByteArray(data, 1));
            return result;
        };
        //SSLpinning helper 帮助定位证书绑定的关键代码a
        Java.use("java.io.File").$init.overload('java.io.File', 'java.lang.String').implementation = function (file, cert) {
            var result = this.$init(file, cert);
            var stack = Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new());
            if (file.getPath().indexOf("cacert") >= 0 && stack.indexOf("X509TrustManagerExtensions.checkServerTrusted") >= 0) {
                var message = {};
                message["function"] = "SSLpinning position locator => " + file.getPath() + " " + cert;
                message["stack"] = stack;
                var data = Memory.alloc(1);
                send(message, Memory.readByteArray(data, 1));
            }
            return result;
        };
    });
}
exports.exportKeyStore = exportKeyStore;
},{"./java_util":3}],3:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.getJavaUtil = void 0;
const context_js_1 = require("./context.js");
let instance = null;
function getJavaUtil() {
    if (instance == null) {
        // chmod 777 data/local/tmp/dex
        let dexFile = Java.openClassFile("data/local/tmp/dex");
        dexFile.load();
        let classList = dexFile.getClassNames();
        instance = Java.use("com.qgl.JavaUtil");
        instance.initLogFile((0, context_js_1.getCtx)());
    }
    return instance;
}
exports.getJavaUtil = getJavaUtil;
},{"./context.js":1}],4:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const http_1 = require("./code/http");
const java_util_1 = require("./code/java_util");
console.log("开始运行");
// hook_pthread_create();
// hookGson();
Java.perform(function () {
    let javeUtil = (0, java_util_1.getJavaUtil)();
    let Log = Java.use("android.util.Log");
    Log["d"].overload("java.lang.String", "java.lang.String").implementation =
        function (tag, log) {
            let result = this["d"](tag, log);
            if (log.indexOf("DramaDetailActivity 开始播放 mCurItem = ") != -1) {
                console.log(log);
                javeUtil.parseDramaLog(log);
            }
            return result;
        };
});
// hookSoOpen()
// checkEncrypt()
// 主动调用的方法写在rpc.exports里面
rpc.exports = {
    // 在控制台输入rpc.exports.test()来调用
    checkHttpStack1() {
        (0, http_1.checkHttpStack)();
    },
    // test(log) {
    //   test(log);
    // },
};
// 附加方式 frida -UF -l hook.js -o log.txt
// 启动方式 frida -U --no-pause -f com.android.miaoa.achai  -l hook.js -o log.txt
// frida -U --no-pause -f com.android.miaoa.achai  -l hook.js -o log.txt
// frida -U --no-pause -f com.aai.scanner -l hook.js -o log.txt
// frida -U --no-pause -f com.shorts.wave.drama -l hook.js -o log.txt
// frida -U --no-pause -f com.storymatrix.drama -l hook.js -o log.txt
// frida -U --no-pause -f live.shorttv.apps -l hook.js -o log.txt
// hook_pthread_create()
// hookSoOpen()
// 抓包开始
// 1.代理抓包
// 在sdcard/download目录下会自动保存客户端证书 在抓包工具中导入客户端证书
// adb shell // 执行下面的命令直接设置代理ip和端口 可绕过app不让设代理的检测
// iptables -t nat -A OUTPUT -p tcp ! -d 127.0.0.1 -m multiport --dports 80,443 -j DNAT --to-destination 代理ip:代理端口
// 看app里哪里实现了javax.net.ssl.X509TrustManager接口 可能是校验服务器证书的地方
// 在jadx中搜索
// 		X509Certificate
//		checkServerTrusted
// 		HostnameVerifier的verify方法
// 看哪里有使用 可能是校验服务器证书的地方
// OkHttp主要看三个方法  sslSocketFactory hostnameVerifier certificatePinner
// findInterfaceInstance("javax.net.ssl.X509TrustManager")
// 2.hook抓包
// 通过checkHttpStack定位发包位置  看堆栈找到RealCall的getResponseWithInterceptorChain方法
// checkHttpStack()
// hook getResponseWithInterceptorChain方法 解析response
// 通过输出的bufferClassName  调用findInterfaceInstance方法找到实现类 把实现类传给parseOkHttpResponse方法
// hookOkHttpRequest()
// hookGson()
// 抓包结束
// checkEncrypt()
// myFunc()
},{"./code/http":2,"./code/java_util":3}]},{},[4])
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIm5vZGVfbW9kdWxlcy9icm93c2VyLXBhY2svX3ByZWx1ZGUuanMiLCJjb2RlL2NvbnRleHQuanMiLCJjb2RlL2h0dHAuanMiLCJjb2RlL2phdmFfdXRpbC5qcyIsImluZGV4LnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBOzs7O0FDQUEsU0FBUyxNQUFNO0lBQ1gsSUFBSSxrQkFBa0IsR0FBRyxJQUFJLENBQUMsR0FBRyxDQUFDLDRCQUE0QixDQUFDLENBQUMsa0JBQWtCLEVBQUUsQ0FBQztJQUNyRixJQUFJLE9BQU8sR0FBRyxrQkFBa0IsQ0FBQyxxQkFBcUIsRUFBRSxDQUFDO0lBQ3pELE9BQU8sT0FBTyxDQUFDO0FBQ25CLENBQUM7QUFFUSx3QkFBTTs7Ozs7QUNMZiwyQ0FBeUM7QUFFekMsU0FBUyxjQUFjO0lBQ25CLElBQUksQ0FBQyxPQUFPLENBQUM7UUFFVCxJQUFJLFFBQVEsR0FBRyxJQUFBLHVCQUFXLEdBQUUsQ0FBQTtRQUU1QixhQUFhO1FBQ2IsSUFBSSxXQUFXLEdBQUcsSUFBSSxDQUFDLEdBQUcsQ0FBQyw2QkFBNkIsQ0FBQyxDQUFDO1FBQzFELFdBQVcsQ0FBQyxZQUFZLENBQUMsY0FBYyxHQUFHLFVBQVUsRUFBRSxFQUFFLElBQUksRUFBRSxHQUFHLEVBQUUsR0FBRztZQUNsRSxPQUFPLElBQUksQ0FBQyxZQUFZLENBQUMsRUFBRSxFQUFFLElBQUksRUFBRSxHQUFHLEVBQUUsR0FBRyxDQUFDLENBQUM7UUFDakQsQ0FBQyxDQUFDO1FBQ0YsWUFBWTtRQUNaLElBQUksVUFBVSxHQUFHLElBQUksQ0FBQyxHQUFHLENBQUMsNEJBQTRCLENBQUMsQ0FBQztRQUN4RCxVQUFVLENBQUMsV0FBVyxDQUFDLGNBQWMsR0FBRyxVQUFVLEVBQUUsRUFBRSxJQUFJLEVBQUUsR0FBRyxFQUFFLEdBQUcsRUFBRSxPQUFPO1lBQ3pFLDJCQUEyQjtZQUMzQixvQ0FBb0M7WUFDcEMsT0FBTyxJQUFJLENBQUMsV0FBVyxDQUFDLEVBQUUsRUFBRSxJ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QUMsQ0FBQTtRQUN2QyxRQUFRLENBQUMsV0FBVyxDQUFDLElBQUEsbUJBQU0sR0FBRSxDQUFDLENBQUE7S0FDakM7SUFFRCxPQUFPLFFBQVEsQ0FBQTtBQUNuQixDQUFDO0FBRVEsa0NBQVc7Ozs7QUNuQnBCLHNDQUE2QztBQUU3QyxnREFBK0M7QUFJL0MsT0FBTyxDQUFDLEdBQUcsQ0FBQyxNQUFNLENBQUMsQ0FBQztBQUVwQix5QkFBeUI7QUFDekIsY0FBYztBQUVkLElBQUksQ0FBQyxPQUFPLENBQUM7SUFDWCxJQUFJLFFBQVEsR0FBRyxJQUFBLHVCQUFXLEdBQUUsQ0FBQztJQUM3QixJQUFJLEdBQUcsR0FBRyxJQUFJLENBQUMsR0FBRyxDQUFDLGtCQUFrQixDQUFDLENBQUM7SUFDdkMsR0FBRyxDQUFDLEdBQUcsQ0FBQyxDQUFDLFFBQVEsQ0FBQyxrQkFBa0IsRUFBRSxrQkFBa0IsQ0FBQyxDQUFDLGNBQWM7UUFDdEUsVUFBVSxHQUFRLEVBQUUsR0FBUTtZQUMxQixJQUFJLE1BQU0sR0FBRyxJQUFJLENBQUMsR0FBRyxDQUFDLENBQUMsR0FBRyxFQUFFLEdBQUcsQ0FBQyxDQUFDO1lBQ2pDLElBQUksR0FBRyxDQUFDLE9BQU8sQ0FBQyxzQ0FBc0MsQ0FBQyxJQUFJLENBQUMsQ0FBQyxFQUFFO2dCQUM3RCxPQUFPLENBQUMsR0FBRyxDQUFDLEdBQUcsQ0FBQyxDQUFDO2dCQUNqQixRQUFRLENBQUMsYUFBYSxDQUFDLEdBQUcsQ0FBQyxDQUFDO2FBQzdCO1lBQ0QsT0FBTyxNQUFNLENBQUM7UUFDaEIsQ0FBQyxDQUFDO0FBQ04sQ0FBQyxDQUFDLENBQUM7QUFFSCxlQUFlO0FBRWYsaUJBQWlCO0FBRWpCLHlCQUF5QjtBQUN6QixHQUFHLENBQUMsT0FBTyxHQUFHO0lBQ1osOEJBQThCO0lBQzlCLGVBQWU7UUFDYixJQUFBLHFCQUFjLEdBQUUsQ0FBQztJQUNuQixDQUFDO0lBQ0QsY0FBYztJQUNkLGVBQWU7SUFDZixLQUFLO0NBQ04sQ0FBQztBQUVGLHVDQUF1QztBQUN2Qyw2RUFBNkU7QUFFN0Usd0VBQXdFO0FBQ3hFLCtEQUErRDtBQUMvRCxxRUFBcUU7QUFDckUscUVBQXFFO0FBQ3JFLGlFQUFpRTtBQUVqRSx3QkFBd0I7QUFDeEIsZUFBZTtBQUVmLE9BQU87QUFFUCxTQUFTO0FBQ1QsOENBQThDO0FBQzlDLGlEQUFpRDtBQUNqRCxrSEFBa0g7QUFDbEgsMkRBQTJEO0FBQzNELFdBQVc7QUFDWCxvQkFBb0I7QUFDcEIsc0JBQXNCO0FBQ3RCLDhCQUE4QjtBQUM5Qix1QkFBdUI7QUFDdkIscUVBQXFFO0FBQ3JFLDBEQUEwRDtBQUUxRCxXQUFXO0FBQ1gsMEVBQTBFO0FBQzFFLG1CQUFtQjtBQUNuQixvREFBb0Q7QUFDcEQsbUZBQW1GO0FBQ25GLHNCQUFzQjtBQUN0QixhQUFhO0FBRWIsT0FBTztBQUVQLGlCQUFpQjtBQUVqQixXQUFXIiwiZmlsZSI6ImdlbmVyYXRlZC5qcyIsInNvdXJjZVJvb3QiOiIifQ==
