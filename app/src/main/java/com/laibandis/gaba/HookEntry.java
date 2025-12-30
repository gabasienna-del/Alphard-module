package com.laibandis.gaba;

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

        try {
            Class<?> fcmService = XposedHelpers.findClassIfExists(
                    "com.google.firebase.messaging.FirebaseMessagingService",
                    lpparam.classLoader
            );

            if (fcmService != null) {
                XposedHelpers.findAndHookMethod(
                        fcmService,
                        "onMessageReceived",
                        Object.class,
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) {
                                Object msg = param.args[0];
                                XposedBridge.log("📨 FCM RAW => " + msg);
                            }
                        }
                );
            } else {
                XposedBridge.log("❗ FirebaseMessagingService NOT FOUND in target APK");
            }

        } catch (Throwable t) {
            XposedBridge.log("FCM hook error: " + t);
        }
    }
}
