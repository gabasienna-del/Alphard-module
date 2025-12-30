package com.laibandis.gaba;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("sinet.startup.inDriver")) return;

        XposedBridge.log("⚡ LATENCY-PROBE injected");

        XposedHelpers.findAndHookMethod(
                "okhttp3.internal.ws.RealWebSocket",
                lpparam.classLoader,
                "onReadMessage",
                Object.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object data = param.args[0];

                        try {
                            byte[] raw = (byte[]) XposedHelpers.callMethod(data, "toByteArray");
                            String payload = new String(raw);
                            XposedBridge.log("⚡ WS FRAME => " + payload);
                        } catch (Throwable t) {
                            XposedBridge.log("⚡ WS FRAME (binary)");
                        }
                    }
                }
        );
    }
}
