
import { getJavaUtil } from "./java_util"

function checkHttpStack() {
    Java.perform(function () {

        let javaUtil = getJavaUtil()

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

        let nativeCrypto = Java.use("com.android.org.conscrypt.NativeCrypto")
        // https write
        nativeCrypto.SSL_write.implementation = function (ssl, ssl_holder, fd, shc, b, off, len, writeTimeoutMillis) {
            console.log("SSL_write")
            // 打印堆栈 
            // 看哪里在写数据 哪里就是请求发送的地方
            console.log(javaUtil.showStack())
            return this.SSL_write(ssl, ssl_holder, fd, shc, b, off, len, writeTimeoutMillis)
        }
        // https read
        nativeCrypto.SSL_read.implementation = function (ssl, ssl_holder, fd, shc, b, off, len, readTimeoutMillis) {
            // console.log("SSL_read")
            // 打印堆栈 
            // 看哪里在读取数据 哪里就是请求返回的地方
            // console.log(javaUtil.showStack())
            return this.SSL_read(ssl, ssl_holder, fd, shc, b, off, len, readTimeoutMillis)
        }
    })
}

function exportKeyStore() {
    Java.perform(function () {
        function storeP12(pri, p7, p12Path, p12Password) {
            var X509Certificate = Java.use("java.security.cert.X509Certificate")
            var p7X509 = Java.cast(p7, X509Certificate);
            var chain = Java.array("java.security.cert.X509Certificate", [p7X509])
            var ks = Java.use("java.security.KeyStore").getInstance("PKCS12", "BC");
            ks.load(null, null);
            ks.setKeyEntry("client", pri, Java.use('java.lang.String').$new(p12Password).toCharArray(), chain);
            try {
                var out = Java.use("java.io.FileOutputStream").$new(p12Path);
                ks.store(out, Java.use('java.lang.String').$new(p12Password).toCharArray())
            } catch (exp) {
                console.log(exp)
            }
        }
        //在服务器校验客户端的情形下，帮助dump客户端证书，并保存为p12的格式，证书密码为r0ysue
        Java.use("java.security.KeyStore$PrivateKeyEntry").getPrivateKey.implementation = function () {
            var result = this.getPrivateKey()
            var packageName = Java.use("android.app.ActivityThread").currentApplication().getApplicationContext().getPackageName();
            storeP12(this.getPrivateKey(), this.getCertificate(), '/sdcard/Download/' + packageName + uuid(10, 16) + '.p12', 'r0ysue');
            var message = {};
            message["function"] = "dumpClinetCertificate=>" + '/sdcard/Download/' + packageName + uuid(10, 16) + '.p12' + '   pwd: r0ysue';
            message["stack"] = Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new());
            var data = Memory.alloc(1);
            send(message, Memory.readByteArray(data, 1))
            return result;
        }
        Java.use("java.security.KeyStore$PrivateKeyEntry").getCertificateChain.implementation = function () {
            var result = this.getCertificateChain()
            var packageName = Java.use("android.app.ActivityThread").currentApplication().getApplicationContext().getPackageName();
            storeP12(this.getPrivateKey(), this.getCertificate(), '/sdcard/Download/' + packageName + uuid(10, 16) + '.p12', 'r0ysue');
            var message = {};
            message["function"] = "dumpClinetCertificate=>" + '/sdcard/Download/' + packageName + uuid(10, 16) + '.p12' + '   pwd: r0ysue';
            message["stack"] = Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new());
            var data = Memory.alloc(1);
            send(message, Memory.readByteArray(data, 1))
            return result;
        }

        //SSLpinning helper 帮助定位证书绑定的关键代码a
        Java.use("java.io.File").$init.overload('java.io.File', 'java.lang.String').implementation = function (file, cert) {
            var result = this.$init(file, cert)
            var stack = Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new());
            if (file.getPath().indexOf("cacert") >= 0 && stack.indexOf("X509TrustManagerExtensions.checkServerTrusted") >= 0) {
                var message = {};
                message["function"] = "SSLpinning position locator => " + file.getPath() + " " + cert;
                message["stack"] = stack;
                var data = Memory.alloc(1);
                send(message, Memory.readByteArray(data, 1))
            }
            return result;
        }
    })
}

export { checkHttpStack, exportKeyStore }