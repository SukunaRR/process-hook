package com.yourpackage.hook;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PackageManagerSpoofHook implements IXposedHookLoadPackage {

    private static final String ORIGINAL_PKG = "com.pubg.imobile";  // or com.pubg.krmobile
    private static final String SPOOFED_PKG = "com.tencent.ig";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        // Only spoof to system apps that query package info
        if (!isSystemOrVendorApp(lpparam.packageName)) return;

        Class<?> pmClass = XposedHelpers.findClassIfExists("android.app.ApplicationPackageManager", lpparam.classLoader);
        if (pmClass == null) return;

        // Hook getApplicationInfo
        XposedHelpers.findAndHookMethod(pmClass, "getApplicationInfo",
                String.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        if (ORIGINAL_PKG.equals(param.args[0])) {
                            param.args[0] = SPOOFED_PKG;
                        }
                    }
                });

        // Hook getPackageInfo
        XposedHelpers.findAndHookMethod(pmClass, "getPackageInfo",
                String.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        if (ORIGINAL_PKG.equals(param.args[0])) {
                            param.args[0] = SPOOFED_PKG;
                        }
                    }
                });

        // Hook getInstallerPackageName (if GameSpace checks it)
        XposedHelpers.findAndHookMethod(pmClass, "getInstallerPackageName",
                String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        if (ORIGINAL_PKG.equals(param.args[0])) {
                            param.args[0] = SPOOFED_PKG;
                        }
                    }
                });
    }

    private boolean isSystemOrVendorApp(String pkg) {
        return pkg != null &&
               (pkg.contains("oneplus") || pkg.contains("gamespace") || pkg.contains("system") || pkg.contains("android"));
    }
}
