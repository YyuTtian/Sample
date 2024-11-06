
function hook_pthread_create() {
    let addr = Module.findExportByName("libc.so", "pthread_create")
    Interceptor.attach(addr, {
        onEnter: function (args) {
            let funcAddr = args[2]
            let module = Process.findModuleByAddress(funcAddr)
            console.log(module.name + "开启子线程 函数地址是" + funcAddr.sub(module.base))

            if (module.name.indexOf("libmsaoaidsec.so") > -1) {
                Interceptor.replace(funcAddr, new NativeCallback(function () {
                    console.log("替换函数" + funcAddr)
                }, 'void', []))

            }
        }
    })
}

export { hook_pthread_create }