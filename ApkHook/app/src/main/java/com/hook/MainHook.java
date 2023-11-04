package com.hook;

import android.app.Application;
import android.content.Context;

import com.hook_okhttp.OkHttpHooker;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    public static Context context;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        String pkgName = loadPackageParam.packageName;
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                context = (Context) param.args[0];//在这里获取到Context

                OkHttpHooker.attach(loadPackageParam.classLoader);

//                MethodHook.hook(loadPackageParam);
            }
        });
    }
}
