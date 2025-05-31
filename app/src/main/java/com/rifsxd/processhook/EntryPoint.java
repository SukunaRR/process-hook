package com.rifsxd.spoofhook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class EntryPoint implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        new PackageManagerSpoofHook().handleLoadPackage(lpparam);
    }
}
