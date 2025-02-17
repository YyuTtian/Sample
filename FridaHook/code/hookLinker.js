function LogPrint(info) {
    var theDate = new Date();
    var hour = theDate.getHours();
    var minute = theDate.getMinutes();
    var second = theDate.getSeconds();
    var mSecond = theDate.getMilliseconds();
    hour < 10 ? hour = "0" + hour : hour;
    minute < 10 ? minute = "0" + minute : minute;
    second < 10 ? second = "0" + second : second;
    mSecond < 10 ? mSecond = "00" + mSecond : mSecond < 100 ? mSecond = "0" + mSecond : mSecond;
    var time = hour + ":" + minute + ":" + second + ":" + mSecond;
    var threadid = Process.getCurrentThreadId();
    console.log("[" + time + "]" + "->threadid:" + threadid + "--" + info.toString());
    // var logfile = new File("/sdcard/log.txt", "ab");
    // logfile.write("[" + time + "]" + "->threadid:" + threadid + "--" + info.toString() + "\n");
    // logfile.close();
}

function disassemble(address, count) {
    var nowaddr = ptr(address).sub(8);
    LogPrint(DebugSymbol.fromAddress(ptr(address)));
    for (var i = 0; i < count; i++) {
        try {
            var ins = Instruction.parse(ptr(nowaddr));
            LogPrint("" + ptr(nowaddr) + ": " + ins.mnemonic + " " + ins.opStr + '->' + DebugSymbol.fromAddress(nowaddr));
        } catch (e) {

        }
        nowaddr = ptr(nowaddr).add(4);
    }

}

function findsyscall(base, size) {
    //console.log(hexdump(base,{length:size}));
    console.log("start search svc begin:" + base + ",size:" + size);
    var matchlist = Memory.scanSync(base, size, "01 00 00 d4");
    matchlist.forEach(function (match) {
        console.log(JSON.stringify(match) + DebugSymbol.fromAddress(match.address));

        //disassemble(match.address, 4);
        hooksvcsyscall(match.address);
    });
    console.log("end search svc begin:" + base + ",size:" + size);
}

function findsyscallbyenumeraterange() {
    LogPrint("go into findsyscallbyenumeraterange");
    Process.enumerateRanges('r-x').forEach(function (range) {
        if (JSON.stringify(range).indexOf("/data/app") != -1) {
            console.error(JSON.stringify(range));
            findsyscall(range.base, range.size);
        }
    });
}

function sleep(time) {
    var sleepaddr = Module.getExportByName("libc.so", "sleep");
    LogPrint("libc.so:sleep addr:" + sleepaddr);
    var sleep = new NativeFunction(sleepaddr, 'pointer', ['pointer']);
    Interceptor.attach(sleepaddr, {
        onEnter: function (args) {
            LogPrint(Process.getCurrentThreadId() + "--go into sleep,time:" + args[0]);
        }, onLeave: function (retval) {
            LogPrint(Process.getCurrentThreadId() + "--leave sleep");
        }
    });
    sleep(ptr(time));

}

function hooksvcsyscall(syscalladdr) {
    try {
        Interceptor.attach(syscalladdr, {
            onEnter: function (args) {
                this.syscallnum = this.context.x8;
                console.warn(syscalladdr + "->" + this.syscallnum);
                this.buf = this.context.x1;
                if (this.syscallnum == 0x38) {
                    this.filepath = ptr(this.context.x1).readCString();
                    if (this.filepath.indexOf("/status") != -1) {
                        ptr(this.context.x1).writeUtf8String("/status");
                    }
                    console.warn(syscalladdr + ":open->" + ptr(this.context.x1).readCString());
                }
                if (this.syscallnum == 0x4e) {
                    this.filepath = this.context.x1.readCString();
                    console.warn(syscalladdr + ":readlinkat->" + this.filepath);
                    this.context.x1.writeUtf8String("/proc/self/fd/0");
                }


            }, onLeave: function (retval) {
                console.error("leave syscall->" + this.syscallnum);

            }
        })
        console.warn("hook->" + syscalladdr + "->success");
    } catch (e) {
        console.error(e)
    }

}

function hooklibc() {
    var libcmodule = Process.getModuleByName("libc.so");
    var inet_aton = libcmodule.getExportByName("inet_aton");
    console.warn("inet_aton->" + inet_aton);
    Interceptor.attach(inet_aton, {
        onEnter: function (args) {
            var ip = ptr(args[0]).readCString();
            console.warn("go into inet_aton->" + ip)
            ptr(args[0]).writeUtf8String("192.6.0.1");
            console.warn("go into replace inet_aton->" + ptr(args[0]).readCString())
        }, onLeave: function (retval) {
            console.warn("leave inet_aton")
        }
    })
    libcmodule.enumerateSymbols().forEach(function (symbol) {
        if (symbol.name == "__openat") {
            LogPrint("find __openat->" + JSON.stringify(symbol));
            Interceptor.attach(symbol.address, {
                onEnter: function (args) {
                    this.filepath = args[1].readCString();
                    LogPrint("start __openat:" + this.filepath);
                }, onLeave: function (retval) {
                    LogPrint("end __openat:" + this.filepath + ",retval:" + retval);
                }
            })
        }
        if (symbol.name == "readlinkat") {
            console.error("readlinkat->" + JSON.stringify(symbol));
            Interceptor.attach(symbol.address, {
                    onEnter: function (args) {
                        this.aaa = args[0];
                        this.bbb = args[1];
                        this.ccc = args[2];
                    }, onLeave: function (retval) {
                        LogPrint('\nreadlinkat(' + 's2="' + this.bbb.readCString() + '"' + ', s3="' + this.ccc + '"' + ')');
                        var s2str = this.bbb.readCString();
                        LogPrint('\nreadlinkat(' + ', s2="' + s2str + '"' + ', s3="' + this.ccc.readCString() + '"' + ')');
                    }
                }
            );
        }
    });
}

// function hooklibc() {
//     var libcmodule = Process.getModuleByName("libc.so");
//     var dlsym = libcmodule.getExportByName("dlsym");
//     Interceptor.attach(dlsym, {
//         onEnter: function (args) {
//             this.symbolname = args[1].readCString();


//         }, onLeave: function (retval) {
//             if (this.symbolname == "JNI_OnLoad") {
//                 if (retval != 0) {
//                     var symbol = DebugSymbol.fromAddress(retval)
//                     console.warn("end dlsym->" + symbol);
//                     Interceptor.attach(ptr(retval), {
//                         onEnter: function (args) {
//                             console.warn("go into->" + symbol);
//                         }, onLeave: function (retval) {
//                             console.warn("leave->" + symbol);
//                         }
//                     })
//                 }

//             }
//         }
//     })

// }

function hook_constructor() {
    if (Process.pointerSize == 4) {
        var linker = Process.findModuleByName("linker");
    } else {
        var linker = Process.findModuleByName("linker64");
    }
    var addr_call_function = null;
    var addr_g_ld_debug_verbosity = null;
    var addr_async_safe_format_log = null;
    var addr_linker_log = null;
    if (linker) {
        var symbols = linker.enumerateSymbols();
        for (var i = 0; i < symbols.length; i++) {
            var name = symbols[i].name;
            if (name.indexOf("linker_log") != -1 && symbols[i].type == "function" && name.indexOf("linker_log_va_list") == -1) {
                addr_linker_log = symbols[i].address;
                console.log("linker_log", JSON.stringify(symbols[i]));
            }
            if (name.indexOf("call_function") >= 0) {
                addr_call_function = symbols[i].address;
                //console.log("call_function", JSON.stringify(symbols[i]));
            } else if (name.indexOf("g_ld_debug_verbosity") >= 0) {
                addr_g_ld_debug_verbosity = symbols[i].address;
                //console.warn("g_ld_debug_verbosity", JSON.stringify(symbols[i]));
                ptr(addr_g_ld_debug_verbosity).writeInt(2);

            } else if (name.indexOf("async_safe_format_log") >= 0 && name.indexOf('va_list') < 0) {
                console.log("async_safe_format_log", JSON.stringify(symbols[i]));
                addr_async_safe_format_log = symbols[i].address;

            }

        }
    }
    if (addr_linker_log) {
        Interceptor.attach(addr_linker_log, {
            onEnter: function (args) {
                this.log_level = args[0];
                this.fmt = ptr(args[1]).readCString()
                //470    TRACE("[ Calling c-tor %s @ %p for '%s' ]", function_name, function, realpath);
                //471    function(g_argc, g_argv, g_envp);
                //472    TRACE("[ Done calling c-tor %s @ %p for '%s' ]", function_name, function, realpath);
                if (this.fmt.indexOf("c-tor") >= 0 && this.fmt.indexOf('Done') < 0) {
                    //console.warn("go into addr_linker_log->" + this.fmt)
                    this.so_path = ptr(args[4]).readCString();
                    var strs = new Array(); //定义一数组
                    strs = this.so_path.split("/"); //字符分割
                    this.so_name = strs.pop();
                    this.func_realoffset = ptr(args[3]);
                    this.func_offset = ptr(args[3]).sub(Module.findBaseAddress(this.so_name))

                    console.log(
                        '\nso_name:', this.so_name,
                        '\nso_path:', this.so_path,
                        '\nfunc_offset:', this.func_offset
                    );
                    if (this.so_name == "libbypassfridadetection.so") {
                        /*                     var thismodule=Process.getModuleByName("libbypassfridadetection.so");
                                             thismodule.getExportByName("JNI_OnLoad");*/
                        var symbol = DebugSymbol.fromAddress(this.func_realoffset);
                        if (this.func_offset == 0x10B30) {
                            Interceptor.attach(this.func_realoffset, {
                                onEnter: function (args) {
                                    console.warn("go into ->" + symbol);
                                    /* Process.enumerateRanges("--x").forEach(function (range) {
                                         //console.warn(JSON.stringify(range));
                                         if (true) {
                                             console.warn("search svc:"+JSON.stringify(range));
                                             console.log(hexdump(range.base,{
                                                 length:range.size
                                             }))
                                             Memory.scanSync(range.base, range.size, "01 00 00 D4").forEach(function (match) {
                                                 console.log(JSON.stringify(match));
                                             })
                                         }
                                     })*/
                                    this.threadid = Process.getCurrentThreadId();
                                    var svclist = [0x027D4
                                        , 0x04174
                                        , 0x04D34
                                        , 0x05560
                                        , 0x063EC
                                        , 0x06BF8
                                        , 0x077F4
                                        , 0x07D60
                                        , 0x08404
                                        , 0x091EC
                                        , 0x09E1C
                                        , 0x0AAC0
                                        , 0x0B338
                                        , 0x0F3CC
                                        , 0x0F938
                                        , 0x0FF64
                                        , 0x116A0
                                        , 0x11EA8
                                        , 0x161D0
                                        , 0x16704
                                        , 0x16CF4
                                        , 0x17984
                                        , 0x1818C
                                        , 0x1BD48
                                        , 0x1C27C
                                        , 0x1C86C
                                        , 0x1D4FC
                                        , 0x1DD04
                                        , 0x22460
                                        , 0x22994
                                        , 0x22F84
                                        , 0x23DA0
                                        , 0x24618
                                        , 0x28740
                                        , 0x28CAC
                                        , 0x292D8
                                        , 0x2A00C
                                        , 0x2A884
                                        , 0x2ED90
                                        , 0x2F304
                                        , 0x2F920
                                        , 0x307A4
                                        , 0x31010
                                        , 0x35CDC
                                        , 0x36248
                                        , 0x36864];
                                    var thismodule = Process.getModuleByName("libbypassfridadetection.so");

                                    svclist.forEach(function (offset) {
                                        var targetaddr = thismodule.base.add(offset);
                                        var svcsymbol = DebugSymbol.fromAddress(targetaddr);
                                        Interceptor.attach(targetaddr, {
                                            onEnter: function (args) {
                                                this.syscallnum = this.context.x8;
                                                console.error("go into " + svcsymbol + "->" + this.syscallnum);

                                            }, onLeave: function (retval) {
                                                console.error("leave " + svcsymbol + "->" + this.syscallnum);

                                            }
                                        })
                                    })
                                    //Stalker.trustThreshold = -1;
                                    //exclude();
                                    /*          Stalker.follow(this.threadid, {
                                                  events: {
                                                      call: false, // CALL instructions: yes please
                                                      // Other events:
                                                      ret: false, // RET instructions
                                                      exec: false, // all instructions: not recommended as it's a lot of data
                                                      block: false, // block executed: coarse execution trace
                                                      compile: false // block compiled: useful for coverage
                                                  },
                                                  transform: function (iterator) {
                                                      var instruction;
                                                      var basicblockstart = false;
                                                      while ((instruction = iterator.next()) !== null) {
                                                          console.log(Process.getCurrentThreadId() + "compile---addr:" + DebugSymbol.fromAddress(instruction.address) + "->" + instruction + "");
                                                          /!* iterator.putCallout(function (context) {
                                                               var inst = Instruction.parse(context.pc).toString();
                                                               if (inst.toString().indexOf("svc")!=-1) {
                                                                   var syscallnumber=context.x8;
                                                                   var moduleinfo = DebugSymbol.fromAddress(context.pc).toString();
                                                                   console.log(Process.getCurrentThreadId() + "---run svc sn:"+syscallnumber+"," + moduleinfo + " addr:" + context.pc + "---" + inst);
                                                               }

                                                           })*!/
                                                          iterator.keep();

                                                      }
                                                  }
                                              });*/
                                }, onLeave: function (retval) {
                                    /*                   Stalker.unfollow(this.threadid);
                                                       Stalker.garbageCollect();*/
                                    console.warn("leave ->" + symbol);
                                }
                            })
                        }

                    }
                }
            },
            onLeave: function (retval) {
            }
        })
    }
    if (addr_async_safe_format_log) {
        Interceptor.attach(addr_async_safe_format_log, {
            onEnter: function (args) {
                this.log_level = args[0];
                this.tag = ptr(args[1]).readCString()
                this.fmt = ptr(args[2]).readCString()
                if (this.fmt.indexOf("c-tor") >= 0 && this.fmt.indexOf('Done') < 0) {
                    this.function_type = ptr(args[3]).readCString();
                    this.so_path = ptr(args[5]).readCString();
                    var strs = new Array();
                    strs = this.so_path.split("/");
                    this.so_name = strs.pop();
                    this.func_realoffset = ptr(args[4]);
                    this.func_offset = ptr(args[4]).sub(Module.findBaseAddress(this.so_name));
                    console.log(
                        '\nso_name:', this.so_name,
                        '\nso_path:', this.so_path,
                        '\nfunc_offset:', this.func_offset
                    );
                    if (this.so_name == "libbypassfridadetection.so") {
                        var symbol = DebugSymbol.fromAddress(this.func_realoffset);
                        Interceptor.attach(this.func_realoffset, {
                            onEnter: function (args) {
                                console.warn("go into->" + symbol);
                            }, onLeave: function (retval) {
                                console.warn("leave->" + symbol);
                            }
                        });

                    }
                }
            },
            onLeave: function (retval) {
            }
        })
    }


}

function main() {
    hooklibc();
    hook_constructor();
}

setImmediate(main)