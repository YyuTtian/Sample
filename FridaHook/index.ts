import { hook_pthread_create } from "./code/native_thread";
import { checkHttpStack } from "./code/http";
import { hookGson } from "./code/gson";
import { getJavaUtil } from "./code/java_util";
import { checkEncrypt } from "./code/encrypt";
import { hookSoOpen } from "./code/so";
import { passRoot } from "./code/pass/passRoot";
import { passProxy } from "./code/pass/passProxy";
import { passVpn } from "./code/pass/passVpn";
import { passFrida } from "./code/pass/passFrida";
import { hooklibc } from "./code/cfile";

console.log("开始运行");

// passRoot();
// passProxy();
// passVpn();
passFrida()

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
    checkHttpStack();
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
