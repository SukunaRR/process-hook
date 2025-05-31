package com.yourpackage.hook;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PackageSpoofHook implements IXposedHookLoadPackage {

    private static final String ORIGINAL_PACKAGE = "com.pubg.imobile"; // or com.pubg.krmobile
    private static final String SPOOFED_PACKAGE = "com.tencent.ig";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // Only spoof to specific target apps (like system or GameSpace)
        if (!isTargetApp(lpparam.packageName)) return;

        XposedHelpers.findAndHookMethod(Context.class, "getPackageName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                String realPkg = ((Context) param.thisObject).getPackageName();
                if (ORIGINAL_PACKAGE.equals(realPkg)) {
                    param.setResult(SPOOFED_PACKAGE);
                }
            }
        });

        XposedHelpers.findAndHookMethod(Context.class, "getApplicationInfo", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                ApplicationInfo ai = (ApplicationInfo) param.getResult();
                if (ai != null && ORIGINAL_PACKAGE.equals(ai.packageName)) {
                    ai.packageName = SPOOFED_PACKAGE;
                    param.setResult(ai);
                }
            }
        });

        // If GameSpace or vendor code uses Application class directly
        XposedHelpers.findAndHookConstructor(Application.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                try {
                    Application app = (Application) param.thisObject;
                    if (ORIGINAL_PACKAGE.equals(app.getPackageName())) {
                        Field pkgField = Application.class.getDeclaredField("mBase");
                        pkgField.setAccessible(true);
                        Context base = (Context) pkgField.get(app);
                        XposedHelpers.setObjectField(base.getApplicationInfo(), "packageName", SPOOFED_PACKAGE);
                    }
                } catch (Exception ignored) {}
            }
        });
    }

    private boolean isTargetApp(String packageName) {
        return packageName.equals("com.oneplus.gamespace")
            || packageName.startsWith("com.android")
            || packageName.startsWith("com.oneplus")
            || packageName.contains("game");
    }
}
