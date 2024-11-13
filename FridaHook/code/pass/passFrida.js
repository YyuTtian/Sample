
// 看hookSoOpen的输出
// 用pthread替换看能不能过掉检测
// 端口检测 不要用默认端口


// 情况1：开子线程循环检测
// 情况2：不会开子线程检测，ptrace占坑，一开始就检测  

function passFrida() {

    let strstrAddr = Module.findExportByName("libc.so", "strstr")
    Interceptor.attach(strstrAddr, {
        onEnter: function (args) {
            let str1 = args[0].readCString
            let str2 = args[1].readCString

            if (str2.indexOf("tmp") != -1 ||
                str2.indexOf("frida" != -1) ||
                str2.indexOf("gdbus" != -1) ||
                str2.indexOf("gum-js-loop" != -1) ||
                str2.indexOf("gmain" != -1) ||
                str2.indexOf("linjector" != -1)
            ) {
                console.log("strstr --> " + str1 + "  " + str2)
                this.hook = true
            }
        },
        onLeave: function (retval) {
            if(this.hook){
                retval.replace(0)
            }
        }
    })

    let strcmpAddr = Module.findExportByName("libc.so", "strcmp")
    Interceptor.attach(strcmpAddr, {
        onEnter: function (args) {
            let str1 = args[0].readCString
            let str2 = args[1].readCString

            if (str2.indexOf("tmp") != -1 ||
                str2.indexOf("frida" != -1) ||
                str2.indexOf("gdbus" != -1) ||
                str2.indexOf("gum-js-loop" != -1) ||
                str2.indexOf("gmain" != -1) ||
                str2.indexOf("linjector" != -1)
            ) {
                console.log("strcmp --> " + str1 + "  " + str2)
                this.hook = true
            }
        },
        onLeave: function (retval) {
            if(this.hook){
                retval.replace(0)
            }
        }
    })

}

export { passFrida }