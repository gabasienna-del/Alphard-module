package com.laibandis.gaba;

import android.app.AndroidAppHelper;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("sinet.startup.inDriver")) return;

        XposedBridge.log("🔥 Alphard module injected into " + lpparam.packageName);

        /* =========================
           FCM PUSH INTERCEPTOR
        ========================== */
        try {
            XposedHelpers.findAndHookMethod(
                    "com.google.firebase.messaging.FirebaseMessagingService",
                    lpparam.classLoader,
                    "onMessageReceived",
                    "com.google.firebase.messaging.RemoteMessage",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            Object msg = param.args[0];
                            XposedBridge.log("🔥 FCM MESSAGE => " + msg);

                            try {
                                Object data = XposedHelpers.callMethod(msg, "getData");
                                XposedBridge.log("📦 FCM DATA => " + data.toString());
                            } catch (Throwable t) {
                                XposedBridge.log("FCM data parse error: " + t);
                            }
                        }
                    }
            );
        } catch (Throwable t) {
            XposedBridge.log("FCM hook failed: " + t);
        }

        /* =========================
           OKHTTP HEADER LOGGER
        ========================== */
        try {
            XposedHelpers.findAndHookMethod(
                    "okhttp3.Request$Builder",
                    lpparam.classLoader,
                    "addHeader",
                    String.class,
                    String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            String k = (String) param.args[0];
                            String v = (String) param.args[1];

                            if (k.equalsIgnoreCase("Authorization")
                                    || k.equalsIgnoreCase("x-auth")
                                    || k.toLowerCase().contains("jwt")) {

                                XposedBridge.log("🔐 JWT HEADER => " + k + " : " + v);
                            }
                        }
                    }
            );
        } catch (Throwable t) {
            XposedBridge.log("OkHttp header hook failed: " + t);
        }

        /* =========================
           WEBSOCKET FRAME LOGGER
        ========================== */
        try {
            XposedHelpers.findAndHookMethod(
                    "okhttp3.internal.ws.RealWebSocket",
                    lpparam.classLoader,
                    "onReadMessage",
                    String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            String msg = (String) param.args[0];
                            XposedBridge.log("📡 WS FRAME => " + msg);
                        }
                    }
            );
        } catch (Throwable t) {
            XposedBridge.log("WS hook failed: " + t);
        }
    }
}
