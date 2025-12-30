package com.laibandis.gaba;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("sinet.startup.inDriver")) return;

        android.util.Log.e("ALPHARD", "LSPosed MODULE LOADED");
    }
}

        try {
            Class<?> ws = Class.forName("okhttp3.internal.ws.RealWebSocket", false, lpparam.classLoader);

            de.robv.android.xposed.XposedHelpers.findAndHookMethod(ws, "onReadMessage",
                    String.class,
                    new de.robv.android.xposed.XC_MethodHook() {
                        protected void afterHookedMethod(MethodHookParam param) {
                            String msg = (String) param.args[0];
                            if (msg.contains("order") || msg.contains("queue") || msg.contains("trip")) {
                                android.util.Log.e("ALPHARD-WS", msg);
                            }
                        }
                    });
        } catch (Throwable t) {}
