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
exports.hookFileRead = void 0;
function hookFileRead() {
    Interceptor.attach(Module.findExportByName("libc.so", "fopen"), {
        onEnter: function (args) {
            let path = Memory.readUtf8String(args[0]);
            let mode = Memory.readUtf8String(args[1]);
            console.log("hookFileRead [fopen] path=" + path + " mode=" + mode);
            if (path.indexOf("aaa") != -1) {
                args[0] = Memory.allocUtf8String("xxx");
            }
        },
        onLeave: function (retval) {
        }
    });
    Interceptor.attach(Module.findExportByName("libc.so", "open"), {
        onEnter: function (args) {
            let path = Memory.readUtf8String(args[0]);
            let flag = args[1].toInt32();
            console.log("hookFileRead [open] path=" + path + " flag=" + flag);
        },
        onLeave: function (retval) {
        }
    });
    Interceptor.attach(Module.findExportByName("libc.so", "openat"), {
        onEnter: function (args) {
            let dirfd = args[0].toInt32();
            let path = Memory.readUtf8String(args[1]);
            let flag = args[2].toInt32();
            console.log("hookFileRead [fopen] path=" + path + " dirfd=" + dirfd + " flag=" + flag);
        },
        onLeave: function (retval) {
        }
    });
}
exports.hookFileRead = hookFileRead;
},{}],3:[function(require,module,exports){
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
},{"./java_util":4}],4:[function(require,module,exports){
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
},{"./context.js":1}],5:[function(require,module,exports){
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
    Interceptor.attach(strstrAddr, {
        onEnter: function (args) {
            let str1 = args[0].readCString;
            let str2 = args[1].readCString;
            if (str2.indexOf("tmp") != -1 ||
                str2.indexOf("frida" != -1) ||
                str2.indexOf("gdbus" != -1) ||
                str2.indexOf("gum-js-loop" != -1) ||
                str2.indexOf("gmain" != -1) ||
                str2.indexOf("linjector" != -1)) {
                console.log("strstr --> " + str1 + "  " + str2);
                this.hook = true;
            }
        },
        onLeave: function (retval) {
            if (this.hook) {
                retval.replace(0);
            }
        }
    });
    let strcmpAddr = Module.findExportByName("libc.so", "strcmp");
    Interceptor.attach(strcmpAddr, {
        onEnter: function (args) {
            let str1 = args[0].readCString;
            let str2 = args[1].readCString;
            if (str2.indexOf("tmp") != -1 ||
                str2.indexOf("frida" != -1) ||
                str2.indexOf("gdbus" != -1) ||
                str2.indexOf("gum-js-loop" != -1) ||
                str2.indexOf("gmain" != -1) ||
                str2.indexOf("linjector" != -1)) {
                console.log("strcmp --> " + str1 + "  " + str2);
                this.hook = true;
            }
        },
        onLeave: function (retval) {
            if (this.hook) {
                retval.replace(0);
            }
        }
    });
}
exports.passFrida = passFrida;
},{}],6:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.passProxy = void 0;
function passProxy() {
    Java.perform(function () {
        let clazz = Java.use("java.util.Properties");
        let overloadsArr = clazz.getProperty.overloads;
        for (let i = 0; i < overloadsArr.length; i++) {
            overloadsArr[i].implementation = function () {
                let params = "";
                for (let j = 0; j < arguments.length; j++) {
                    params += arguments[j] + " ";
                }
                console.log("clazz.getProperty is called! params is: ", params);
                let result = this.getProperty.apply(this, arguments);
                console.log("result:" + result);
                let key = arguments[0];
                if (key.indexOf("https.proxyHost") != -1 || key.indexOf("https.proxyPory") != -1 || key.indexOf("net.dns1") != -1) {
                    return null;
                }
                else {
                    return result;
                }
            };
        }
    });
}
exports.passProxy = passProxy;
},{}],7:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.passRoot = void 0;
const ROOT_CHECK_FULL_PATH = [
    "/data/local/bin/su",
    "/data/local/su",
    "/data/local/xbin/su",
    "/dev/com.koushikdutta.superuser.daemon/",
    "/sbin/su",
    "/sbin/supersu",
    "/sbin/magisk",
    "/sbin/magiskhide",
    "/system/app/Superuser.apk",
    "/system/bin/failsafe/su",
    "/system/bin/su",
    "/su/bin/su",
    "/system/etc/init.d/99SuperSUDaemon",
    "/system/sd/xbin/su",
    "/system/xbin/busybox",
    "/system/sbin/supersu",
    "/system/sbin/magisk",
    "/system/sbin/busybox",
    "/system/sbin/magiskhide",
    "/sbin/busybox",
    "/system/xbin/daemonsu",
    "/system/xbin/su",
    "/system/sbin/su",
    "/vendor/bin/su",
    "/cache/su",
    "/data/su",
    "/dev/su",
    "/system/bin/.ext/su",
    "/system/usr/we-need-root/su",
    "/system/app/Kinguser.apk",
    "/data/adb/magisk",
    "/sbin/.magisk",
    "/cache/.disable_magisk",
    "/dev/.magisk.unblock",
    "/cache/magisk.log",
    "/data/adb/magisk.img",
    "/data/adb/magisk.db",
    "/data/adb/magisk_simple",
    "/init.magisk.rc",
    "/system/xbin/ku.sud",
    "/product/bin/su",
    "/product/bin/magisk",
    "/product/bin/busybox",
];
const ROOT_CHECK_FILE_SUFFIX = [
// "/maps",
// "/status"
];
const ROOT_CHECK_DIR_SUFFIX = [
// "/fd",
// "/task"
];
const ROOT_CHECK_SU_CMD = [
    "su",
    "supersu",
    "busybox",
    "magisk",
    "magiskhide",
    "daemonsu",
    "Kinguser.apk",
    "Superuser.apk"
];
const ROOT_CHECK_MANAGEMENT_APP = [
    "com.noshufou.android.su",
    "com.noshufou.android.su.elite",
    "eu.chainfire.supersu",
    "com.koushikdutta.superuser",
    "com.thirdparty.superuser",
    "com.yellowes.su",
    "com.koushikdutta.rommanager",
    "com.koushikdutta.rommanager.license",
    "com.dimonvideo.luckypatcher",
    "com.chelpus.lackypatch",
    "com.ramdroid.appquarantine",
    "com.ramdroid.appquarantinepro",
    "com.topjohnwu.magisk",
    "com.devadvance.rootcloak",
    "com.devadvance.rootcloakplus",
    "de.robv.android.xposed.installer",
    "com.saurik.substrate",
    "com.zachspong.temprootremovejb",
    "com.amphoras.hidemyroot",
    "com.amphoras.hidemyrootadfree",
    "com.formyhm.hiderootPremium",
    "com.formyhm.hideroot",
    "me.phh.superuser",
    "eu.chainfire.supersu.pro",
    "com.kingouser.com",
    "com.topjohnwu.magisk"
];
// print java stack trace
function printStackTrace() {
    var Exception = Java.use('java.lang.Exception');
    var Log = Java.use('android.util.Log');
    var stackInfo = Log.getStackTraceString(Exception.$new());
    console.log(stackInfo);
}
// print native stack trace
function printStackTraceNative() {
    var backtrace = Thread.backtrace(this.context, Backtracer.ACCURATE).map(DebugSymbol.fromAddress).join("\n");
    console.log(backtrace);
}
// fgets: read line
// todo: get file name by fp and check it's /proc/xxx/status file to improve performance
function handleTracePidCheck() {
    var fgetsPtr = Module.findExportByName('libc.so', 'fgets');
    var fgets = new NativeFunction(fgetsPtr, 'pointer', ['pointer', 'int', 'pointer']);
    Interceptor.replace(fgetsPtr, new NativeCallback(function (buffer, size, fp) {
        var retval = fgets(buffer, size, fp);
        var bufStr = Memory.readUtf8String(buffer);
        if (bufStr.indexOf("TracerPid:") > -1) {
            Memory.writeUtf8String(buffer, "TracerPid:\t0");
            console.log("fake result for tracepid: " + Memory.readUtf8String(buffer));
        }
        return retval;
    }, "pointer", ["pointer", "int", "pointer"]));
}
;
function handleWithJavaFileCheck() {
    var File = Java.use("java.io.File");
    var UnixFileSystem = Java.use("java.io.UnixFileSystem");
    // handle file.exists
    File.exists.implementation = function () {
        var path = this.getAbsolutePath();
        console.log("java file exists method called for: " + path);
        if (ROOT_CHECK_FULL_PATH.indexOf(path) >= 0) {
            console.log("fake result for file exists: " + path);
            return false;
        }
        return this.exists();
    };
    // handle file real implementation for ops
    UnixFileSystem.checkAccess.implementation = function (file, access) {
        const filePath = file.getAbsolutePath();
        console.log("unix file system access called for: " + filePath);
        var fields = filePath.split("/");
        var exec = fields[fields.length - 1];
        if (ROOT_CHECK_SU_CMD.indexOf(exec) >= 0) {
            console.log("fake result for checkAccess: " + filePath);
            return false;
        }
        if (ROOT_CHECK_FULL_PATH.indexOf(filePath) >= 0) {
            console.log("fake result for checkAccess: " + filePath);
            return false;
        }
        for (let i = 0; i < ROOT_CHECK_FILE_SUFFIX.length; i++) {
            if (filePath.endsWith(ROOT_CHECK_FILE_SUFFIX[i])) {
                console.log("fake result for checkAccess: " + filePath);
                return false;
            }
        }
        for (let i = 0; i < ROOT_CHECK_DIR_SUFFIX.length; i++) {
            if (filePath.endsWith(ROOT_CHECK_DIR_SUFFIX[i])) {
                console.log("fake result for checkAccess: " + filePath);
                return false;
            }
        }
        return this.checkAccess(file, access);
    };
}
function handleWithNativeFileCheck() {
    var access = Module.findExportByName("libc.so", "access");
    var faccessat = Module.findExportByName("libc.so", "faccessat");
    var open = Module.findExportByName("libc.so", "open");
    var openat = Module.findExportByName("libc.so", "openat");
    var stat = Module.findExportByName("libc.so", "stat");
    var lstat = Module.findExportByName("libc.so", "lstat");
    var fstatat = Module.findExportByName("libc.so", "fstatat");
    var ls = Module.findExportByName("libc.so", "fstatat");
    var opendir = Module.findExportByName("libc.so", "opendir");
    Interceptor.attach(opendir, {
        onEnter: function (args) {
            this.inputPath = args[0].readUtf8String();
            console.log("opendir called: " + this.inputPath);
        },
        onLeave: function (retval) {
            if (retval.toInt32() != 0) {
                var fields = this.inputPath.split("/");
                var suffix = "/" + fields[fields.length - 1];
                if (ROOT_CHECK_DIR_SUFFIX.indexOf(suffix) >= 0) {
                    console.log("fake result for opendir: " + this.inputPath);
                    retval.replace(ptr(0x0));
                }
            }
        }
    });
    Interceptor.attach(stat, {
        onEnter: function (args) {
            this.inputPath = args[0].readUtf8String();
            console.log("stat called: " + this.inputPath);
        },
        onLeave: function (retval) {
            if (retval.toInt32() == 0) {
                var fields = this.inputPath.split("/");
                var exec = fields[fields.length - 1];
                if (ROOT_CHECK_FULL_PATH.indexOf(this.inputPath) >= 0 || ROOT_CHECK_SU_CMD.indexOf(exec) >= 0) {
                    console.log("fake result for stat: " + this.inputPath);
                    retval.replace(-1);
                }
            }
        }
    });
    Interceptor.attach(lstat, {
        onEnter: function (args) {
            this.inputPath = args[0].readUtf8String();
            console.log("lstat called: " + this.inputPath);
        },
        onLeave: function (retval) {
            if (retval.toInt32() == 0) {
                var fields = this.inputPath.split("/");
                var exec = fields[fields.length - 1];
                if (ROOT_CHECK_FULL_PATH.indexOf(this.inputPath) >= 0 || ROOT_CHECK_SU_CMD.indexOf(exec) >= 0) {
                    console.log("fake result for lstat: " + this.inputPath);
                    retval.replace(-1);
                }
            }
        }
    });
    Interceptor.attach(fstatat, {
        onEnter: function (args) {
            this.inputPath = args[1].readUtf8String();
            console.log("fstatat called: " + this.inputPath);
        },
        onLeave: function (retval) {
            if (retval.toInt32() == 0) {
                var fields = this.inputPath.split("/");
                var exec = fields[fields.length - 1];
                if (ROOT_CHECK_FULL_PATH.indexOf(this.inputPath) >= 0 || ROOT_CHECK_SU_CMD.indexOf(exec) >= 0) {
                    console.log("fake result for fstatat: " + this.inputPath);
                    retval.replace(-1);
                }
            }
        }
    });
    Interceptor.attach(open, {
        onEnter: function (args) {
            this.inputPath = args[0].readUtf8String();
            console.log("open called: " + this.inputPath);
        },
        onLeave: function (retval) {
            if (retval.toInt32() != 0) {
                var fields = this.inputPath.split("/");
                var exec = fields[fields.length - 1];
                if (ROOT_CHECK_FULL_PATH.indexOf(this.inputPath) >= 0 || ROOT_CHECK_SU_CMD.indexOf(exec) >= 0) {
                    console.log("fake result for open: " + this.inputPath);
                    retval.replace(-1);
                }
                for (let i = 0; i < ROOT_CHECK_FILE_SUFFIX.length; i++) {
                    if (this.inputPath.endsWith(ROOT_CHECK_FILE_SUFFIX[i])) {
                        console.log("fake result for open: " + this.inputPath);
                        retval.replace(-1);
                    }
                }
            }
        }
    });
    Interceptor.attach(openat, {
        onEnter: function (args) {
            this.inputPath = args[0].readUtf8String();
            console.log("openat called: " + this.inputPath);
        },
        onLeave: function (retval) {
            if (retval.toInt32() != 0) {
                var fields = this.inputPath.split("/");
                var exec = fields[fields.length - 1];
                if (ROOT_CHECK_FULL_PATH.indexOf(this.inputPath) >= 0 || ROOT_CHECK_SU_CMD.indexOf(exec) >= 0) {
                    console.log("fake result for openat: " + this.inputPath);
                    retval.replace(-1);
                }
            }
        }
    });
    Interceptor.attach(faccessat, {
        onEnter: function (args) {
            this.inputPath = args[1].readUtf8String();
            console.log("faccessat called: " + this.inputPath);
        },
        onLeave: function (retval) {
            if (retval.toInt32() == 0) {
                var fields = this.inputPath.split("/");
                var exec = fields[fields.length - 1];
                if (ROOT_CHECK_FULL_PATH.indexOf(this.inputPath) >= 0 || ROOT_CHECK_SU_CMD.indexOf(exec) >= 0) {
                    console.log("fake result for faccessat: " + this.inputPath);
                    retval.replace(-1);
                }
            }
        }
    });
    Interceptor.attach(access, {
        onEnter: function (args) {
            this.inputPath = args[0].readUtf8String();
            console.log("access called: " + this.inputPath);
        },
        onLeave: function (retval) {
            if (retval.toInt32() == 0) {
                var fields = this.inputPath.split("/");
                var exec = fields[fields.length - 1];
                if (ROOT_CHECK_FULL_PATH.indexOf(this.inputPath) >= 0 || ROOT_CHECK_SU_CMD.indexOf(exec) >= 0) {
                    console.log("fake result for access: " + this.inputPath);
                    retval.replace(-1);
                }
            }
        }
    });
}
// android.app.PackageManager
function handleWithRootAppCheck() {
    var ApplicationPackageManager = Java.use("android.app.ApplicationPackageManager");
    ApplicationPackageManager.getPackageInfo.overload('java.lang.String', 'int').implementation = function (str, i) {
        console.log("android application manager getPackageInfo: " + str);
        if (ROOT_CHECK_MANAGEMENT_APP.indexOf(str) >= 0) {
            console.log("fake result for getPackageInfo: " + str);
            str = "com.fake.fake.package";
        }
        return this.getPackageInfo(str, i);
    };
}
function handleWithShellCheck() {
    var String = Java.use('java.lang.String');
    var ProcessImpl = Java.use("java.lang.ProcessImpl");
    ProcessImpl.start.implementation = function (cmdArray, env, dir, redirects, redirectErrorStream) {
        var cmdline = cmdArray.toString();
        console.log("java ProcessImpl: " + cmdline);
        if (cmdline.endsWith("/su")) {
            console.log("fake result for process run: " + cmdline);
            arguments[0] = Java.array('java.lang.String', [String.$new("")]);
            return ProcessImpl.start.apply(this, arguments);
        }
        if (cmdArray[0] == "mount") {
            console.log("fake result for process mount: " + cmdline);
            arguments[0] = Java.array('java.lang.String', [String.$new("")]);
            return ProcessImpl.start.apply(this, arguments);
        }
        // getprop ro.secure
        if (cmdArray[0] == "getprop") {
            const prop = [
                "ro.secure",
                "ro.debuggable"
            ];
            if (prop.indexOf(cmdArray[1]) >= 0) {
                console.log("fake result for process getprop: " + cmdline);
                arguments[0] = Java.array('java.lang.String', [String.$new("")]);
                return ProcessImpl.start.apply(this, arguments);
            }
        }
        // which su
        if (cmdArray[0].indexOf("which") >= 0) {
            const prop = [
                "su"
            ];
            if (prop.indexOf(cmdArray[1]) >= 0) {
                console.log("fake result for which: " + cmdline);
                arguments[0] = Java.array('java.lang.String', [String.$new("")]);
                return ProcessImpl.start.apply(this, arguments);
            }
        }
        return ProcessImpl.start.apply(this, arguments);
    };
}
function fakeProp() {
    var Build = Java.use("android.os.Build");
    var Tags = Build.class.getDeclaredField("TAGS");
    Tags.setAccessible(true);
    Tags.set(null, "release-keys");
    var fingerprint = Build.class.getDeclaredField("FINGERPRINT");
    fingerprint.setAccessible(true);
    fingerprint.set(null, "OnePlus/OnePlus6T/OnePlus6T:9/PKQ1.180716.001/1812111152:user/release-keys");
    var system_property_get = Module.findExportByName("libc.so", "__system_property_get");
    Interceptor.attach(system_property_get, {
        onEnter(args) {
            this.key = args[0].readCString();
            this.ret = args[1];
        },
        onLeave(ret) {
            if (this.key == "ro.build.fingerprint") {
                var tmp = "OnePlus/OnePlus6T/OnePlus6T:9/PKQ1.180716.001/1812111152:user/release-keys";
                var p = Memory.allocUtf8String(tmp);
                Memory.copy(this.ret, p, tmp.length + 1);
            }
        }
    });
}
function passRoot() {
    Java.perform(function () {
        fakeProp();
        handleTracePidCheck();
        handleWithJavaFileCheck();
        handleWithNativeFileCheck();
        handleWithRootAppCheck();
        handleWithShellCheck();
    });
}
exports.passRoot = passRoot;
},{}],8:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.passVpn = void 0;
function passVpn() {
    Java.perform(function () {
        var can_hook = false;
        var ConnectivityManager = Java.use("android.net.ConnectivityManager");
        ConnectivityManager.getNetworkInfo.overload('int').implementation = function () {
            console.log("call getNetworkInfo function =========!!!");
            if (arguments[0] == 17) {
                can_hook = true;
            }
            var ret = this.getNetworkInfo(arguments[0]);
            return ret;
        };
        var NetworkInfo = Java.use("android.net.NetworkInfo");
        NetworkInfo.isConnected.implementation = function () {
            console.log("call isConnected ========= !!!");
            var ret = this.isConnected();
            if (can_hook) {
                ret = false;
                can_hook = false;
                console.log("call isConnected function========= !!!");
            }
            return ret;
        };
        var NetworkCapabilities = Java.use("android.net.NetworkCapabilities");
        NetworkCapabilities.hasTransport.implementation = function () {
            console.log("call hasTransport =========!!!");
            var ret = this.hasTransport(arguments[0]);
            if (arguments[0] == 4) {
                console.log("call hasTransport function =========!!!");
                ret = false;
            }
            return ret;
        };
        NetworkCapabilities.transportNameOf.overload('int').implementation = function () {
            console.log("call transportNameOf =========!!!");
            var ret = this.transportNameOf(arguments[0]);
            if (ret.indexOf("VPN") >= 0) {
                ret = "WIFI";
            }
            return ret;
        };
        NetworkCapabilities.appendStringRepresentationOfBitMaskToStringBuilder.implementation = function (sb, bitMask, nameFetcher, separator) {
            if (bitMask == 18) {
                console.log("bitMask", bitMask);
                sb.append("WIFI");
            }
            else {
                console.log(sb, bitMask);
                this.appendStringRepresentationOfBitMaskToStringBuilder(sb, bitMask, nameFetcher, separator);
            }
        };
        var NetworkInterface = Java.use("java.net.NetworkInterface");
        NetworkInterface.getAll.implementation = function () {
            var nis = this.getAll();
            console.log("call getAll =========!!!");
            nis.forEach(function (ni) {
                if (ni.name.value.indexOf("tun0") >= 0 || ni.name.value.indexOf("ppp0") >= 0) {
                    ni.name.value = "xxxx";
                    ni.displayName.value = "xxxx";
                }
            });
            return nis;
        };
        NetworkInterface.getName.implementation = function () {
            var name = this.getName();
            console.log("name: " + name);
            if (name == "tun0" || name == "ppp0") {
                return "rmnet_data0";
            }
            else {
                return name;
            }
        };
    });
}
exports.passVpn = passVpn;
},{}],9:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const http_1 = require("./code/http");
const java_util_1 = require("./code/java_util");
const passRoot_1 = require("./code/pass/passRoot");
const passProxy_1 = require("./code/pass/passProxy");
const passVpn_1 = require("./code/pass/passVpn");
const passFrida_1 = require("./code/pass/passFrida");
const file_1 = require("./code/file");
console.log("开始运行");
(0, passRoot_1.passRoot)();
(0, passProxy_1.passProxy)();
(0, passVpn_1.passVpn)();
(0, passFrida_1.passFrida)();
// hook_pthread_create();
// hookGson();
// hookSoOpen()
// checkEncrypt()
(0, file_1.hookFileRead)();
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
},{"./code/file":2,"./code/http":3,"./code/java_util":4,"./code/pass/passFrida":5,"./code/pass/passProxy":6,"./code/pass/passRoot":7,"./code/pass/passVpn":8}]},{},[9])
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIm5vZGVfbW9kdWxlcy9icm93c2VyLXBhY2svX3ByZWx1ZGUuanMiLCJjb2RlL2NvbnRleHQuanMiLCJjb2RlL2ZpbGUuanMiLCJjb2RlL2h0dHAuanMiLCJjb2RlL2phdmFfdXRpbC5qcyIsImNvZGUvcGFzcy9wYXNzRnJpZGEuanMiLCJjb2RlL3Bhc3MvcGFzc1Byb3h5LmpzIiwiY29kZS9wYXNzL3Bhc3NSb290LmpzIiwiY29kZS9wYXNzL3Bhc3NWcG4uanMiLCJpbmRleC50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTs7OztBQ0FBLFNBQVMsTUFBTTtJQUNYLElBQUksa0JBQWtCLEdBQUcsSUFBSSxDQUFDLEdBQUcsQ0FBQyw0QkFBNEIsQ0FBQyxDQUFDLGtCQUFrQixFQUFFLENBQUM7SUFDckYsSUFBSSxPQUFPLEdBQUcsa0JBQWtCLENBQUMscUJBQXFCLEVBQUUsQ0FBQztJQUN6RCxPQUFPLE9BQU8sQ0FBQztBQUNuQixDQUFDO0FBRVEsd0JBQU07Ozs7O0FDTmYsU0FBUyxZQUFZO0lBRWpCLFdBQVcsQ0FBQyxNQUFNLENBQUMsTUFBTSxDQUFDLGdCQUFnQixDQUFDLFNBQVMsRUFBRSxPQUFPLENBQUMsRUFBRTtRQUM1RCxPQUFPLEVBQUUsVUFBVSxJQUFJO1lBQ25CLElBQUksSUFBSSxHQUFHLE1BQU0sQ0FBQyxjQUFjLENBQUMsSUFBSSxDQUFDLENBQUMsQ0FBQyxDQUFDLENBQUE7WUFDekMsSUFBSSxJQUFJLEdBQUcsTUFBTSxDQUFDLGNBQWMsQ0FBQyxJQUFJLENBQUMsQ0FBQyxDQUFDLENBQUMsQ0FBQTtZQUN6QyxPQUFPLENBQUMsR0FBRyxDQUFDLDRCQUE0QixHQUFHLElBQUksR0FBRyxRQUFRLEdBQUcsSUFBSSxDQUFDLENBQUM7WUFFbkUsSUFBSSxJQUFJLENBQUMsT0FBTyxDQUFDLEtBQUssQ0FBQyxJQUFJLENBQUMsQ0FBQyxFQUFFO2dCQUMzQixJQUFJLENBQUMsQ0FBQyxDQUFDLEdBQUcsTUFBTSxDQUFDLGVBQWUsQ0FB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RXBCLElBQUEsbUJBQVEsR0FBRSxDQUFDO0FBQ1gsSUFBQSxxQkFBUyxHQUFFLENBQUM7QUFDWixJQUFBLGlCQUFPLEdBQUUsQ0FBQztBQUNWLElBQUEscUJBQVMsR0FBRSxDQUFBO0FBRVgseUJBQXlCO0FBQ3pCLGNBQWM7QUFDZCxlQUFlO0FBRWYsaUJBQWlCO0FBRWpCLElBQUEsbUJBQVksR0FBRSxDQUFBO0FBSWQsSUFBSSxDQUFDLE9BQU8sQ0FBQztJQUNYLElBQUksUUFBUSxHQUFHLElBQUEsdUJBQVcsR0FBRSxDQUFDO0lBQzdCLElBQUksR0FBRyxHQUFHLElBQUksQ0FBQyxHQUFHLENBQUMsa0JBQWtCLENBQUMsQ0FBQztJQUN2QyxHQUFHLENBQUMsR0FBRyxDQUFDLENBQUMsUUFBUSxDQUFDLGtCQUFrQixFQUFFLGtCQUFrQixDQUFDLENBQUMsY0FBYztRQUN0RSxVQUFVLEdBQVEsRUFBRSxHQUFRO1lBQzFCLElBQUksTUFBTSxHQUFHLElBQUksQ0FBQyxHQUFHLENBQUMsQ0FBQyxHQUFHLEVBQUUsR0FBRyxDQUFDLENBQUM7WUFDakMsSUFBSSxHQUFHLENBQUMsT0FBTyxDQUFDLHNDQUFzQyxDQUFDLElBQUksQ0FBQyxDQUFDLEVBQUU7Z0JBQzdELE9BQU8sQ0FBQyxHQUFHLENBQUMsR0FBRyxDQUFDLENBQUM7Z0JBQ2pCLFFBQVEsQ0FBQyxhQUFhLENBQUMsR0FBRyxDQUFDLENBQUM7YUFDN0I7WUFDRCxPQUFPLE1BQU0sQ0FBQztRQUNoQixDQUFDLENBQUM7QUFDTixDQUFDLENBQUMsQ0FBQztBQUlILHlCQUF5QjtBQUN6QixHQUFHLENBQUMsT0FBTyxHQUFHO0lBQ1osOEJBQThCO0lBQzlCLGNBQWM7SUFDZCxlQUFlO0lBQ2YsS0FBSztJQUVMLGVBQWU7UUFDYixJQUFBLHFCQUFjLEdBQUUsQ0FBQztJQUNuQixDQUFDO0NBQ0YsQ0FBQztBQUVGLHVDQUF1QztBQUN2Qyw2RUFBNkU7QUFFN0Usd0VBQXdFO0FBQ3hFLCtEQUErRDtBQUMvRCxxRUFBcUU7QUFDckUscUVBQXFFO0FBQ3JFLGlFQUFpRTtBQUVqRSx3QkFBd0I7QUFDeEIsZUFBZTtBQUVmLE9BQU87QUFFUCxTQUFTO0FBQ1QsOENBQThDO0FBQzlDLGlEQUFpRDtBQUNqRCxrSEFBa0g7QUFDbEgsMkRBQTJEO0FBQzNELFdBQVc7QUFDWCxvQkFBb0I7QUFDcEIsc0JBQXNCO0FBQ3RCLDhCQUE4QjtBQUM5Qix1QkFBdUI7QUFDdkIscUVBQXFFO0FBQ3JFLDBEQUEwRDtBQUUxRCxXQUFXO0FBQ1gsMEVBQTBFO0FBQzFFLG1CQUFtQjtBQUNuQixvREFBb0Q7QUFDcEQsbUZBQW1GO0FBQ25GLHNCQUFzQjtBQUN0QixhQUFhO0FBRWIsT0FBTztBQUVQLGlCQUFpQjtBQUVqQixXQUFXIiwiZmlsZSI6ImdlbmVyYXRlZC5qcyIsInNvdXJjZVJvb3QiOiIifQ==
