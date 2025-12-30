package com.laibandis.gaba;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okio.ByteString;

public class HookEntry implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("sinet.startup.inDriver")) return;

        XposedBridge.log("🔥 LATENCY-PROBE injected");

        // TEXT FRAMES
        XposedHelpers.findAndHookMethod(
                "okhttp3.internal.ws.RealWebSocket",
                lpparam.classLoader,
                "onReadMessage",
                String.class,
                new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String msg = (String) param.args[0];
                        XposedBridge.log("🟢 WS TEXT => " + msg);
                    }
                }
        );

        // BINARY FRAMES
        XposedHelpers.findAndHookMethod(
                "okhttp3.internal.ws.RealWebSocket",
                lpparam.classLoader,
                "onReadMessage",
                ByteString.class,
                new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) {
                        ByteString bs = (ByteString) param.args[0];
                        XposedBridge.log("🟡 WS BIN => " + bs.hex());
                    }
                }
        );

        // FIREBASE PUSH (FCM)
        XposedHelpers.findAndHookMethod(
                "com.google.firebase.messaging.FirebaseMessagingService",
                lpparam.classLoader,
                "onMessageReceived",
                "com.google.firebase.messaging.RemoteMessage",
                new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) {
                        Object rm = param.args[0];
                        XposedBridge.log("🔔 FCM PUSH => " + rm.toString());
                    }
                }
        );
    }
}
