
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
                if (path != null && path.indexOf(soName) >= 0) {
                    hookSoOpenNext(soName)
                }
            }
        }
    });
}

let soName = "libutils.so"

function hookSoOpenNext(soName) {
    let call_constructors_addr = null

    let m = Process.findModuleByName("linker64")
    console.log(m)
    let symnols = Process.getModuleByName("linker64").enumerateSymbols()


    for (let index = 0; index < symnols.length; index++) {
        const element = symnols[index];
        if (element.name.indexOf("call_constructors") >= 0) {
            call_constructors_addr = element.address;
        }
    }

    console.log("call_constructors_addr=" + call_constructors_addr)

    let canReplace = true

    Interceptor.attach(call_constructors_addr, {
        onEnter: function () {
            if (canReplace) {
                canReplace = false
                let soAddr = Module.findBaseAddress(soName)
                console.log("soAddr=" + soAddr)

                let funcAddr = soAddr.add(0x12f90)
                Interceptor.replace(funcAddr, new NativeCallback(function () {
                    console.log("替换函数" + funcAddr)
                }, 'void', []))

                // let funcAddr1 = soAddr.add(0x10975)
                // Interceptor.replace(funcAddr1, new NativeCallback(function () {
                //     console.log("替换函数" + funcAddr1)
                // }, 'void', []))
            }
        }
    })
}


export { hookSoOpen }