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
// 看hookSoOpen的输出
// 用pthread替换看能不能过掉检测
// 端口检测 不要用默认端口
Object.defineProperty(exports, "__esModule", { value: true });
exports.passFrida = void 0;
// 情况1：开子线程循环检测
// 情况2：不会开子线程检测，ptrace占坑，一开始就检测  
function passFrida() {
    let strstrAddr = Module.findExportByName("libc.so", "strstr");
    console.log("strstrAddr=" + strstrAddr);
    Interceptor.attach(strstrAddr, {
        onEnter: function (args) {
            let str1 = args[0].readCString;
            let str2 = args[1].readCString;
            console.log("strstr --> " + str1 + "  " + str2);
            if (str2.indexOf("tmp") !== -1 ||
                str2.indexOf("frida" !== -1) ||
                str2.indexOf("gdbus" !== -1) ||
                str2.indexOf("gum-js-loop" !== -1) ||
                str2.indexOf("gmain" !== -1) ||
                str2.indexOf("linjector" !== -1)) {
                this.hook = true;
            }
        },
        onLeave: function (retval) {
            if (this.hook) {
                retval.replace(0);
            }
        }
    });
    // let strcmpAddr = Module.findExportByName("libc.so", "strcmp")
    // Interceptor.attach(strcmpAddr, {
    //     onEnter: function (args) {
    //         let str1 = args[0].readCString
    //         let str2 = args[1].readCString
    //         if (str2.indexOf("tmp") != -1 ||
    //             str2.indexOf("frida" != -1) ||
    //             str2.indexOf("gdbus" != -1) ||
    //             str2.indexOf("gum-js-loop" != -1) ||
    //             str2.indexOf("gmain" != -1) ||
    //             str2.indexOf("linjector" != -1)
    //         ) {
    //             console.log("strcmp --> " + str1 + "  " + str2)
    //             this.hook = true
    //         }
    //     },
    //     onLeave: function (retval) {
    //         if(this.hook){
    //             retval.replace(0)
    //         }
    //     }
    // })
}
exports.passFrida = passFrida;
},{}],5:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const http_1 = require("./code/http");
const passFrida_1 = require("./code/pass/passFrida");
console.log("开始运行");
// passRoot();
// passProxy();
// passVpn();
(0, passFrida_1.passFrida)();
// hook_pthread_create();
// hookGson();
// hookSoOpen()
// checkEncrypt()
// hooklibc()
// 主动调用的方法写在rpc.exports里面
rpc.exports = {
    // 在控制台输入rpc.exports.test()来调用
    // test(log) {
    //   test(log);
    // },
    checkHttpStack1() {
        (0, http_1.checkHttpStack)();
    },
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
},{"./code/http":2,"./code/pass/passFrida":4}]},{},[5])
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIm5vZGVfbW9kdWxlcy9icm93c2VyLXBhY2svX3ByZWx1ZGUuanMiLCJjb2RlL2NvbnRleHQuanMiLCJjb2RlL2h0dHAuanMiLCJjb2RlL2phdmFfdXRpbC5qcyIsImNvZGUvcGFzcy9wYXNzRnJpZGEuanMiLCJpbmRleC50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTs7OztBQ0FBLFNBQVMsTUFBTTtJQUNYLElBQUksa0JBQWtCLEdBQUcsSUFBSSxDQUFDLEdBQUcsQ0FBQyw0QkFBNEIsQ0FBQyxDQUFDLGtCQUFrQixFQUFFLENBQUM7SUFDckYsSUFBSSxPQUFPLEdBQUcsa0JBQWtCLENBQUMscUJBQXFCLEVBQUUsQ0FBQztJQUN6RCxPQUFPLE9BQU8sQ0FBQztBQUNuQixDQUFDO0FBRVEsd0JBQU07Ozs7O0FDTGYsMkNBQXlDO0FBRXpDLFNBQVMsY0FBYztJQUNuQixJQUFJLENBQUMsT0FBTyxDQUFDO1FBRVQsSUFBSSxRQUFRLEdBQUcsSUFBQSx1QkFBVyxHQUFFLENBQUE7UUFFNUIsYUFBYTtRQUNiLElBQUksV0FBVyxHQUFHLElBQUksQ0FBQyxHQUFHLENBQUMsNkJBQTZCLENBQUMsQ0FBQztRQUMxRCxXQUFXLENBQUMsWUFBWSxDQUFDLGNBQWMsR0FBRyxVQUFVLEVBQUUsRUFBRSxJQUFJLEVBQUUsR0FBRyxFQUFFLEdBQUc7WUFDbEUsT0FBTyxJQUFJLENBQUMsWUFBWSxDQUFDLEVBQUUsRUFBRSxJQUFJLEVBQUUsR0FBRyxFQUFFLEdBQUcsQ0FBQyxDQUFDO1FBQ2pELENBQUMsQ0FBQztRQUNGLFlBQVk7UUFDWixJQUFJLFVBQVUsR0FBRyxJQUFJLENBQUMsR0FBRyxDQUFDLDRCQUE0QixDQUFDLENBQUM7UUFDeEQsVUFBVSxDQUFDLFdBQVcsQ0FBQyxjQUFjLEdBQUcsVUFBVSxFQUFFLEVBQUUsSUFBSSxFQUFFLEdBQUcsRUFBRSxHQUFHLEVBQUUsT0FBTztZQUN6RSwyQkFBMkI7WUFDM0Isb0NBQW9DO1lBQ3BDLE9BQU8sSUFBSSxD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QUVFLElBQUksQ0FBQyxJQUFJLEdBQUcsSUFBSSxDQUFBO2FBQ25CO1FBQ0wsQ0FBQztRQUNELE9BQU8sRUFBRSxVQUFVLE1BQU07WUFDckIsSUFBSSxJQUFJLENBQUMsSUFBSSxFQUFFO2dCQUNYLE1BQU0sQ0FBQyxPQUFPLENBQUMsQ0FBQyxDQUFDLENBQUE7YUFDcEI7UUFDTCxDQUFDO0tBQ0osQ0FBQyxDQUFBO0lBRUYsZ0VBQWdFO0lBQ2hFLG1DQUFtQztJQUNuQyxpQ0FBaUM7SUFDakMseUNBQXlDO0lBQ3pDLHlDQUF5QztJQUV6QywyQ0FBMkM7SUFDM0MsNkNBQTZDO0lBQzdDLDZDQUE2QztJQUM3QyxtREFBbUQ7SUFDbkQsNkNBQTZDO0lBQzdDLDhDQUE4QztJQUM5QyxjQUFjO0lBQ2QsOERBQThEO0lBQzlELCtCQUErQjtJQUMvQixZQUFZO0lBQ1osU0FBUztJQUNULG1DQUFtQztJQUNuQyx5QkFBeUI7SUFDekIsZ0NBQWdDO0lBQ2hDLFlBQVk7SUFDWixRQUFRO0lBQ1IsS0FBSztBQUVULENBQUM7QUFFUSw4QkFBUzs7OztBQ2hFbEIsc0NBQTZDO0FBUTdDLHFEQUFrRDtBQUdsRCxPQUFPLENBQUMsR0FBRyxDQUFDLE1BQU0sQ0FBQyxDQUFDO0FBRXBCLGNBQWM7QUFDZCxlQUFlO0FBQ2YsYUFBYTtBQUNiLElBQUEscUJBQVMsR0FBRSxDQUFBO0FBRVgseUJBQXlCO0FBQ3pCLGNBQWM7QUFDZCxlQUFlO0FBRWYsaUJBQWlCO0FBRWpCLGFBQWE7QUFFYix5QkFBeUI7QUFDekIsR0FBRyxDQUFDLE9BQU8sR0FBRztJQUNaLDhCQUE4QjtJQUM5QixjQUFjO0lBQ2QsZUFBZTtJQUNmLEtBQUs7SUFFTCxlQUFlO1FBQ2IsSUFBQSxxQkFBYyxHQUFFLENBQUM7SUFDbkIsQ0FBQztDQUNGLENBQUM7QUFFRix1Q0FBdUM7QUFDdkMsNkVBQTZFO0FBRTdFLHdFQUF3RTtBQUN4RSwrREFBK0Q7QUFDL0QscUVBQXFFO0FBQ3JFLHFFQUFxRTtBQUNyRSxpRUFBaUU7QUFFakUsd0JBQXdCO0FBQ3hCLGVBQWU7QUFFZixPQUFPO0FBRVAsU0FBUztBQUNULDhDQUE4QztBQUM5QyxpREFBaUQ7QUFDakQsa0hBQWtIO0FBQ2xILDJEQUEyRDtBQUMzRCxXQUFXO0FBQ1gsb0JBQW9CO0FBQ3BCLHNCQUFzQjtBQUN0Qiw4QkFBOEI7QUFDOUIsdUJBQXVCO0FBQ3ZCLHFFQUFxRTtBQUNyRSwwREFBMEQ7QUFFMUQsV0FBVztBQUNYLDBFQUEwRTtBQUMxRSxtQkFBbUI7QUFDbkIsb0RBQW9EO0FBQ3BELG1GQUFtRjtBQUNuRixzQkFBc0I7QUFDdEIsYUFBYTtBQUViLE9BQU87QUFFUCxpQkFBaUI7QUFFakIsV0FBVyIsImZpbGUiOiJnZW5lcmF0ZWQuanMiLCJzb3VyY2VSb290IjoiIn0=
