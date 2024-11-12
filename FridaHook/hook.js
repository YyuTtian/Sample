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
},{}],5:[function(require,module,exports){
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
},{}],6:[function(require,module,exports){
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
},{}],7:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const http_1 = require("./code/http");
const java_util_1 = require("./code/java_util");
const passRoot_1 = require("./code/pass/passRoot");
const passProxy_1 = require("./code/pass/passProxy");
const passVpn_1 = require("./code/pass/passVpn");
console.log("开始运行");
(0, passRoot_1.passRoot)();
(0, passProxy_1.passProxy)();
(0, passVpn_1.passVpn)();
// hook_pthread_create();
// hookGson();
// hookSoOpen()
// checkEncrypt()
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
},{"./code/http":2,"./code/java_util":3,"./code/pass/passProxy":4,"./code/pass/passRoot":5,"./code/pass/passVpn":6}]},{},[7])
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIm5vZGVfbW9kdWxlcy9icm93c2VyLXBhY2svX3ByZWx1ZGUuanMiLCJjb2RlL2NvbnRleHQuanMiLCJjb2RlL2h0dHAuanMiLCJjb2RlL2phdmFfdXRpbC5qcyIsImNvZGUvcGFzcy9wYXNzUHJveHkuanMiLCJjb2RlL3Bhc3MvcGFzc1Jvb3QuanMiLCJjb2RlL3Bhc3MvcGFzc1Zwbi5qcyIsImluZGV4LnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBOzs7O0FDQUEsU0FBUyxNQUFNO0lBQ1gsSUFBSSxrQkFBa0IsR0FBRyxJQUFJLENBQUMsR0FBRyxDQUFDLDRCQUE0QixDQUFDLENBQUMsa0JBQWtCLEVBQUUsQ0FBQztJQUNyRixJQUFJLE9BQU8sR0FBRyxrQkFBa0IsQ0FBQyxxQkFBcUIsRUFBRSxDQUFDO0lBQ3pELE9BQU8sT0FBTyxDQUFDO0FBQ25CLENBQUM7QUFFUSx3QkFBTTs7Ozs7QUNMZiwyQ0FBeUM7QUFFekMsU0FBUyxjQUFjO0lBQ25CLElBQUksQ0FBQyxPQUFPLENBQUM7UUFFVCxJQUFJLFFBQVEsR0FBRyxJQUFBLHVCQUFXLEdBQUUsQ0FBQTtRQUU1QixhQUFhO1FBQ2IsSUFBSSxXQUFXLEdBQUcsSUFBSSxDQUFDLEdBQUcsQ0FBQyw2QkFBNkIsQ0FBQyxDQUFDO1FBQzFELFdBQVcsQ0FBQyxZQUFZLENBQUMsY0FBYyxHQUFHLFVBQVUsRUFBRSxFQUFFLElBQUksRUFBRSxHQUFHLEVBQUUsR0FBRztZQUNsRSxPQUFPLElBQUksQ0FBQyxZQUFZLENBQUMsRUFBRSxFQUFFLElBQUksRUFBRSxHQUFHLEVBQUUsR0FBRyxDQUFDLENBQUM7UUFDakQsQ0FBQyxDQUFDO1FBQ0YsWUFBWTtRQUNaLElBQUksVUFBVSxHQUFHLElBQUksQ0FBQyxHQUFHLENBQUMsNEJBQTRCLENBQUMsQ0FBQztRQUN4RCxVQUFVLENBQUMsV0FBVyxDQUFDLGNBQWMsR0FBRyxVQUFVLEVBQUUsRUFBRSxJQUFJLEVBQUUsR0FBRyxFQUFFLEdBQUcsRUFBRSxP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
