package com.rifsxd.processhook;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PackageNameSpoofHook {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedHelpers.findAndHookMethod(
                "android.app.Application",              // Class
                lpparam.classLoader,
                "getPackageName",                       // Method
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        return "com.tencent.ig";        // Spoofed package name
                    }
                }
            );
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
