package com.laibandis.gaba;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.internal.ws.RealWebSocket;
import okio.ByteString;

public class HookEntry implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("sinet.startup.inDriver")) return;

        XposedBridge.log("⚡ LATENCY-PROBE injected");

        XposedHelpers.findAndHookMethod(
                "okhttp3.internal.ws.RealWebSocket",
                lpparam.classLoader,
                "onReadMessage",
                ByteString.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ByteString data = (ByteString) param.args[0];
                        byte[] raw = data.toByteArray();

                        String payload = new String(raw);
                        XposedBridge.log("⚡ WS FRAME => " + payload);

                        // Здесь появляется ORDER JSON в 1–30 мс
                    }
                }
        );
    }
}
