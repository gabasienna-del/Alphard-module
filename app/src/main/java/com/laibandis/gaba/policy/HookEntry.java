package com.laibandis.gaba.policy;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.os.PowerManager;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage {

    public static final String TARGET = "sinet.startup.inDriver";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals(TARGET)) return;

        XposedBridge.log("🔥 Alphard policy ACTIVE for inDriver");

        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {

                Context ctx = (Context) XposedHelpers.callMethod(param.thisObject, "getApplicationContext");
                PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);

                try {
                    PowerManager.WakeLock wl =
                            pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Alphard:inDriver");
                    wl.acquire();
                } catch (Throwable t) {}
            }
        });

        XposedHelpers.findAndHookMethod(AlarmManager.class, "set",
                int.class, long.class, PendingIntent.class,
                new XC_MethodHook() {
                    @Override protected void beforeHookedMethod(MethodHookParam param) {
                        param.args[0] = AlarmManager.RTC_WAKEUP;
                    }
                });

        XposedHelpers.findAndHookMethod(AlarmManager.class, "setExact",
                int.class, long.class, PendingIntent.class,
                new XC_MethodHook() {
                    @Override protected void beforeHookedMethod(MethodHookParam param) {
                        param.args[0] = AlarmManager.RTC_WAKEUP;
                    }
                });
    }
}
