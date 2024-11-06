
import { getJavaUtil } from "./java_util"

function findInterfaceInstance(interfaceName) {
    Java.perform(function () {
        let javaUtil = getJavaUtil()
        Java.enumerateClassLoaders({
            onMatch: function (loader) {
                try {
                    // 将loader赋值给系统loader
                    Java.classFactory.loader = loader;

                    console.log("loader=" + loader)

                    let classes = Java.enumerateLoadedClassesSync();
                    for (const index in classes) {
                        let className = classes[index];
                        try {
                            let clazz = Java.use(className);
                            let interfaceList = clazz.class.getInterfaces();
                            if (interfaceList.length === 0) continue;
                            for (let i = 0; i < interfaceList.length; i++) {
                                if (javaUtil.haveStr(interfaceList[i], interfaceName)) {
                                    console.log(className + " implements " + interfaceName);
                                }
                            }
                        } catch (e) {
                            // console.log("Didn't find class: " + className + " " + e);
                        }
                    }

                } catch (e) {
                    console.log("fail " + loader + " " + e);
                }
            },
            onComplete: function () { }
        });

    })
}

export { findInterfaceInstance }