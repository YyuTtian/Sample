function hooklibc() {
    var libcmodule = Process.getModuleByName("libc.so");
    libcmodule.enumerateSymbols().forEach(function (symbol) {
      if (symbol.name == "__openat") {
        //int openat(int dirfd, const char *pathname, int flags, mode_t mode);
        Interceptor.attach(symbol.address, {
          onEnter: function (args) {
            this.filepath = ptr(args[1]).readCString();
            // 先看读取了哪些文件
            // 然后正常启动一次app 把这些文件都复制到sd卡中
            // 再启动frida的时候 把路径重定向到sd卡中
            console.log("go into __openat,path:" + this.filepath);
  
  
            // if (this.filepath.indexOf("/maps") != -1) {
            //     ptr(args[1]).writeUtf8String("/sdcard/maps");
            //     console.warn("redirect to /sdcard/maps");
            // }
            // if (this.filepath.indexOf("/status") != -1) {
            //     ptr(args[1]).writeUtf8String("/sdcard/status");
            //     console.warn("redirect to /sdcard/status");
            // }
  
          }, onLeave: function (retval) {
            console.log("leave __openat,path:" + this.filepath);
          }
        })
      }
      if (symbol.name == "readlinkat") {
        // ssize_t readlinkat(int fd,const char * pathname,char * buf,size_t bufsize);
        Interceptor.attach(symbol.address, {
          onEnter: function (args) {
            this.filepath = args[1];
            this.buf = args[2];
            // 先看读取了哪些文件
            // 然后正常启动一次app 把这些文件都复制到sd卡中
            // 再启动frida的时候 把路径重定向到sd卡中
            console.log("go into readlinkat,path:" + ptr(this.filepath).readCString());
  
            // ptr(this.filepath).writeUtf8String("/proc/6836/fd/0");
          }, onLeave: function (retval) {
            console.log("leave readlinkat,:" + ptr(this.filepath).readCString() + "," + ptr(this.buf).readCString());
          }
        })
      }
    })
  }

export { hooklibc }
