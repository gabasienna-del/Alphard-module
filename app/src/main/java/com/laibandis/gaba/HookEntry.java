package com.laibandis.gaba;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("sinet.startup.inDriver")) return;

        XposedBridge.log("🚗 Indriver Hook loaded");

        try {
            XposedHelpers.findAndHookMethod(
                    "okhttp3.RealWebSocket",
                    lpparam.classLoader,
                    "onReadMessage",
                    String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            String msg = (String) param.args[0];
                            if (msg != null && msg.contains("ORDER")) {
                                XposedBridge.log("📦 ORDER FRAME: " + msg);
                            }
                        }
                    }
            );
        } catch (Throwable t) {
            XposedBridge.log("Hook error: " + t);
        }
    }
}
