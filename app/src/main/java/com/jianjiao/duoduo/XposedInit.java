package com.jianjiao.duoduo;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedInit implements IXposedHookLoadPackage {
    public String TAG = "__尖叫__xp";
    Context applicationContext;
    Activity mActivity;
    String ACTIONR = "com.jianjiao.test.PDDGUANGBO";
    public static String uin = "";
    ClassLoader classLoader;

    //com.smile.gifmaker
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        classLoader = lpparam.classLoader;
        //Log.d(TAG, "加载apk: " + lpparam.packageName);
        if ("com.smile.gifmaker".equals(lpparam.packageName)) {
            XposedHelpers.findAndHookMethod("com.yxcorp.gifshow.HomeActivity", classLoader, "onCreate", android.os.Bundle.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    //Activity activity = (Activity) param.thisObject;
                    mActivity = (Activity) param.thisObject;
                    applicationContext = (Context) mActivity.getApplicationContext();
                    Toast.makeText(applicationContext, "插件已加载", Toast.LENGTH_SHORT);
                    sendIntent(1, "插件已加载");
                }
            });
            XposedHelpers.findAndHookMethod("jh4.j", classLoader, "onSuccess", java.lang.Object.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Log.d(TAG, "商品回调: " + param + "|" + param.args);
                    if (param.args[0] != null) {
                        String data = param.args[0].toString();
                        sendIntent(10, data);
                    }
                }
            });
        }
    }

    public void sendIntent(int code, String data) {
        Log.d(TAG, "开始发送数据: " + data.length() + "| " + data);
        sendIntentSliced(data, code, 1024 * 100);
    }

    public void sendIntentSliced(String data, int code, int sliceSize) {
        // 确定切片大小
        final int SLICE_SIZE = sliceSize;
        XposedBridge.log("开始发送数据11: " + data.length() + "| " + data);
        Log.d(TAG, "开始发送数据111: " + data.length() + "| " + data);
        // 分割数据
        List<String> slices = new ArrayList<>();
        for (int i = 0; i < data.length(); i += SLICE_SIZE) {
            slices.add(data.substring(i, Math.min(i + SLICE_SIZE, data.length())));
        }
        // 发送每个切片
        for (int i = 0; i < slices.size(); i++) {
            String sliceData = slices.get(i);
            // 创建 Intent 对象
            Intent intent = new Intent();
            intent.setAction(ACTIONR);
            // 添加切片索引和数据
            intent.putExtra("code", code);
            intent.putExtra("index", i);
            intent.putExtra("total", slices.size());
            intent.putExtra("data", sliceData);
            // 发送广播
            mActivity.sendBroadcast(intent);
            XposedBridge.log("发送广播: 使用原context " + slices.size() + mActivity);
            /*try {
                if (mActivity == null) {
                    // 获取 application context 并发送广播
                    Class<?> PddActivityThread = classLoader.loadClass("com.yxcorp.gifshow.HomeActivity");
                    Object pddActivityThread = XposedHelpers.newInstance(PddActivityThread);
                    Context appContext = (Context) XposedHelpers.callMethod(pddActivityThread, "getApplication");
                    appContext.sendBroadcast(intent);
                    XposedBridge.log("发送广播: 自行获取application " + appContext);
                } else {
                    // 使用现有的 Context 发送广播
                    mActivity.sendBroadcast(intent);
                    XposedBridge.log("发送广播: 使用原context " + slices.size() + mActivity);
                }
            } catch (Exception e) {
                XposedBridge.log("发送消息失败: " + e);
                Log.d(TAG, "发送消息失败: " + e);
            }*/
            // 可以在这里加入延时或其他逻辑，比如检查网络状态等
        }
    }

}













