package com.heatey.nowpay;

import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.alipay.sdk.app.PayTask;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.ipaynow.unionpay.plugin.api.CrossUnionPayPlugin;
import com.ipaynow.unionpay.plugin.conf.CallMhtStatusCode;
import com.ipaynow.unionpay.plugin.manager.cache.MessageCache;
import com.ipaynow.unionpay.plugin.manager.route.dto.ResponseParams;
import com.ipaynow.unionpay.plugin.manager.route.impl.ReceivePayResult;
import com.ipaynow.unionpay.plugin.model.PayResult;

import java.util.Map;

/**
 * Package     ：com.heyteago.module
 * Description ：
 * Company     ：Heytea
 * Author      ：Created by ChengGuang
 * CreateTime  ：2020/6/10.
 */
public class AlipayModule  extends ReactContextBaseJavaModule {
    private Promise mPromise;
    private ReactApplicationContext mReactContext;
    public AlipayModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mReactContext =reactContext;
    }


    @Override
    public String getName() {
        return "Alipay";
    }

    @ReactMethod
    public void pay(final String orderInfo, Promise promise) {
        this.mPromise = promise;
        // 异步任务
        AliPayTask aliPayTask = new AliPayTask();
        aliPayTask.execute(orderInfo);
    }

    public class AliPayTask extends AsyncTask<String,  Integer, Map<String, String>> {
        protected  Map<String, String> doInBackground(String... params) {
            PayTask alipay = new PayTask(getCurrentActivity());
            Map<String, String> result = alipay.payV2(params[0],true);
            return result;
        }

        @Override
        protected void onPostExecute(Map<String, String> obj) {
            super.onPostExecute(obj);
            PayResult payResult = new PayResult((Map<String, String>) obj);
            System.out.println("alipay call0000------- "+payResult.toString());
            String result = payResult.getResult();
            String resultStatus = payResult.getResultStatus();
            String memo = payResult.getMemo();
            /**
             * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
             */
            // 判断resultStatus 为9000则代表支付成功
            System.out.println("alipay call1111------- "+payResult.toString());

            if (TextUtils.equals(resultStatus, "9000")) {
                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
            } else {
                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
            }
            System.out.println("alipay call2222------- "+payResult.toString());
            WritableMap map = Arguments.createMap();
            map.putString("mome",memo);
            map.putString("result",result);
            map.putString("status",resultStatus);
            System.out.println("alipay call33333------- "+payResult.toString());
//            if (mPromise != null) {
//                mPromise.resolve(map);
//                mPromise = null;
//            }
            sendEvent(mReactContext,"aliPayCallback",map);
        }

    }



    //定义发送事件的函数
    public  void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params)
    {
        System.out.println("reactContext="+reactContext);
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName,params);
    }

}
