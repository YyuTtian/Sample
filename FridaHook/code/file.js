function hookFileRead() {

    Interceptor.attach(Module.findExportByName("libc.so", "fopen"), {
        onEnter: function (args) {
            let path = Memory.readUtf8String(args[0])
            let mode = Memory.readUtf8String(args[1])
            console.log("hookFileRead [fopen] path=" + path + " mode=" + mode);

            if (path.indexOf("aaa") != -1) {
                args[0] = Memory.allocUtf8String("xxx")
            }

        },
        onLeave: function (retval) {
        }
    });

    Interceptor.attach(Module.findExportByName("libc.so", "open"), {
        onEnter: function (args) {
            let path = Memory.readUtf8String(args[0])
            let flag = args[1].toInt32()
            console.log("hookFileRead [open] path=" + path + " flag=" + flag);
        },
        onLeave: function (retval) {
        }
    });

    Interceptor.attach(Module.findExportByName("libc.so", "openat"), {
        onEnter: function (args) {
            let dirfd = args[0].toInt32()
            let path = Memory.readUtf8String(args[1]);
            let flag = args[2].toInt32()
            console.log("hookFileRead [fopen] path=" + path + " dirfd=" + dirfd + " flag=" + flag);
        },
        onLeave: function (retval) {
        }
    });

}

export { hookFileRead }
