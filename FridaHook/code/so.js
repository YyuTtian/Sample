
function hookSoOpen() {
    console.log("运行 hookSoOpen")
    Interceptor.attach(Module.findExportByName(null, "dlopen"), {
        onEnter: function (args) {
            let pathptr = args[0];
            if (pathptr !== undefined && pathptr != null) {
                let path = ptr(pathptr).readCString();
                console.log("dlopen " + path);
                // if (path != null && path.indexOf(soName) >= 0) {
                //     hookSoOpenNext(soName)
                // }
            }
        }
    });

    Interceptor.attach(Module.findExportByName(null, "android_dlopen_ext"), {
        onEnter: function (args) {
            let pathptr = args[0];
            if (pathptr !== undefined && pathptr != null) {
                let path = ptr(pathptr).readCString();
                console.log("android_dlopen_ext " + path);
                // if (path != null && path.indexOf(soName) >= 0) {
                //     hookSoOpenNext(soName)
                // }
            }
        }
    });
}

function getModuleBaseAndSize(soName) {

    let addr_fopen = Module.findExportByName("libc.so", "fopen");
    let addr_fgets = Module.findExportByName("libc.so", "fgets");
    let addr_fclose = Module.findExportByName("libc.so", "fclose");

    let fopen = new NativeFunction(addr_fopen, "pointer", ["pointer", "pointer"]);
    let fgets = new NativeFunction(addr_fgets, "pointer", ["pointer", "int", "pointer"]);
    let fclose = new NativeFunction(addr_fclose, "int", ["pointer"]);

    let filename = Memory.allocUtf8String("/proc/" + Process.id + "/maps");
    let open_mode = Memory.allocUtf8String("r");
    let file = fopen(filename, open_mode);

    let buffer = Memory.alloc(1000)
    let base, size
    while (fgets(buffer, 999, file) != 0) {
        let text = buffer.readCString()
        if (text.indexOf(soName) != -1) {
            if (!base) {
                base = '0x' + text.substring(0, text.indexOf("-"))
            }
            let end = '0x'.text.substring(text.indexOf("-") + 1, text.indexOf(" "))
            size = Number(end) - Number(base)
        }
    }
    fclose(file)
    return { base: base, size: size }
}

// libxxx.so
function dumpSo(soName) {
    Java.perform(function () {
        let module = getModuleBaseAndSize(soName)

        let dstPath = getCtx().getExternalCacheDir() + "/" + soName + "_" + module.base + "_" + module.size + ".so"
        let fileHandle = new File(dstPath, "wb")
        
        if (fileHandle && fileHandle != null) {
            // 修改内存的权限
            Memory.protect(ptr(module.base), module.size, "rwx")
            // 读取内存中的so到buffer中
            let buffer = ptr(module.base).readByteArray(module.size)
            fileHandle.write(buffer)
            fileHandle.flush()
            fileHandle.close()
            console.log("dumoSo " + soName + " path=" + dstPath)
        }
    })
}


export { hookSoOpen, dumpSo }