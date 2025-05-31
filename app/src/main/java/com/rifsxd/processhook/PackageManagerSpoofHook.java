package com.rifsxd.spoofhook;

import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PackageManagerSpoofHook {

    private static final String ORIGINAL_PKG = "com.pubg.imobile";
    private static final String SPOOFED_PKG = "com.tencent.ig";

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!"com.csdroid.pkg".equals(lpparam.packageName)) return;

        Class<?> pmClass = XposedHelpers.findClassIfExists("android.app.ApplicationPackageManager", lpparam.classLoader);
        if (pmClass == null) return;

        XposedHelpers.findAndHookMethod(pmClass, "getApplicationInfo",
                String.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        if (ORIGINAL_PKG.equals(param.args[0])) {
                            param.args[0] = SPOOFED_PKG;
                        }
                    }
                });

        XposedHelpers.findAndHookMethod(pmClass, "getPackageInfo",
                String.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        if (ORIGINAL_PKG.equals(param.args[0])) {
                            param.args[0] = SPOOFED_PKG;
                        }
                    }
                });

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
}
