package com.heatey.nowpay;


import androidx.annotation.NonNull;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.heatey.nowpay.IPNCrossBorderModule;
import com.heatey.nowpay.WeChatModule;

/**
 * Package     ：com.heatey.nowpay
 * Description ：
 * Company     ：Heytea
 * Author      ：Created by ChengGuang
 * CreateTime  ：2020/5/21.
 */
public class IPNCrossBorderPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Arrays.asList(new NativeModule[]{
                new IPNCrossBorderModule(reactContext), // 现在支付插件
                new WeChatModule(reactContext),
        });
    }
    
    @NonNull
    @Override
    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}
