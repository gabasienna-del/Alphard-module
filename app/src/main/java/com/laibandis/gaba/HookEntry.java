package com.laibandis.gaba;

import android.app.Application;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("sinet.startup.inDriver")) return;

        XposedBridge.log("🔥 Alphard module loaded into " + lpparam.packageName);

        // ───── FCM HOOK ─────
        XposedHelpers.findAndHookMethod(
                "com.google.firebase.messaging.FirebaseMessagingService",
                lpparam.classLoader,
                "onMessageReceived",
                "com.google.firebase.messaging.RemoteMessage",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        Object msg = param.args[0];
                        XposedBridge.log("📨 FCM PUSH => " + msg.toString());
                    }
                }
        );

        // ───── OKHTTP WS HOOK ─────
        XposedHelpers.findAndHookMethod(
                "okhttp3.internal.ws.RealWebSocket",
                lpparam.classLoader,
                "onReadMessage",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String frame = (String) param.args[0];
                        XposedBridge.log("🌐 WS FRAME => " + frame);
                    }
                }
        );
    }
}
