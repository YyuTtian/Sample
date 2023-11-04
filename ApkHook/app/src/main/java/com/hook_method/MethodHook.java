package com.hook_method;

import static de.robv.android.xposed.XposedHelpers.*;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MethodHook {

    public static void hook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
//        XposedHelpers.findAndHookMethod()

        String className = "";
        String methodName = "";
        Class<?> hookClass = findClass(className, loadPackageParam.classLoader);
        XposedBridge.hookAllMethods(hookClass, methodName, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                // 如果有多个方法匹配 根据参数的个数和类型区分
                // param.args.length
            }
        });
    }
}
