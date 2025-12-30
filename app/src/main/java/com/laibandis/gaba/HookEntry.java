package com.laibandis.gaba;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class HookEntry implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        // Фильтруем по пакету
        if (!lpparam.packageName.equals("sinet.startup.inDriver")) return;

        // ===== MASTER JWT =====
        XposedHelpers.findAndHookMethod(
                "com.sinet.startup.auth.internal.AuthRepository",
                lpparam.classLoader,
                "getSession",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        Object session = param.getResult();
                        if (session != null) {
                            try {
                                Object token = XposedHelpers.getObjectField(session, "sessionToken");
                                XposedBridge.log("MASTER JWT => " + token);
                            } catch (Throwable t) {
                                XposedBridge.log("JWT parse error: " + t);
                            }
                        }
                    }
                }
        );

        // ===== LINK TOKEN =====
        XposedHelpers.findAndHookMethod(
                "com.sinet.startup.linking.internal.LinkingService",
                lpparam.classLoader,
                "linkDevice",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        Object result = param.getResult();
                        if (result != null) {
                            try {
                                Object linkToken = XposedHelpers.getObjectField(result, "linkToken");
                                XposedBridge.log("LINK TOKEN => " + linkToken);
                            } catch (Throwable t) {
                                XposedBridge.log("Link token error: " + t);
                            }
                        }
                    }
                }
        );

        // ===== DEVICE MASTER ID =====
        XposedHelpers.findAndHookMethod(
                "com.sinet.startup.device.internal.DeviceRepository",
                lpparam.classLoader,
                "getDeviceMasterId",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        XposedBridge.log("DEVICE MASTER ID => " + param.getResult());
                    }
                }
        );

        // ===== WebSocket SEND =====
        XposedHelpers.findAndHookMethod(
                "okhttp3.RealWebSocket",
                lpparam.classLoader,
                "send",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String payload = (String) param.args[0];
                        if (payload.contains("auth") || payload.contains("queue")) {
                            XposedBridge.log("WS SEND => " + payload);
                        }
                    }
                }
        );

        // ===== WebSocket RECEIVE =====
        XposedHelpers.findAndHookMethod(
                "okhttp3.RealWebSocket",
                lpparam.classLoader,
                "onMessage",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String msg = (String) param.args[0];
                        if (msg.contains("queue") || msg.contains("order")) {
                            XposedBridge.log("WS RECV => " + msg);
                        }
                    }
                }
        );
    }
}
