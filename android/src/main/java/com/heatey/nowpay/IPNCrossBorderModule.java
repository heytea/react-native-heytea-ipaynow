package com.heatey.nowpay;


import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.alipay.sdk.app.PayTask;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.ipaynow.unionpay.plugin.api.CrossUnionPayPlugin;
import com.ipaynow.unionpay.plugin.conf.PluginConfig;
import com.ipaynow.unionpay.plugin.manager.route.dto.ResponseParams;
import com.ipaynow.unionpay.plugin.manager.route.impl.ReceivePayResult;
import com.ipaynow.unionpay.plugin.model.PayResult;
import com.ipaynow.unionpay.plugin.utils.PreSignMessageUtil;


import java.util.Map;

/**
 * package: com.heatey.nowpay
 * author: chengguang
 * created on: 2018/12/3
 * description:
 */
public class IPNCrossBorderModule extends ReactContextBaseJavaModule implements ReceivePayResult, LifecycleEventListener {


    private ReactApplicationContext mContext;
    private PreSignMessageUtil mPreSign = new PreSignMessageUtil();
    private Promise mPromise;

    public IPNCrossBorderModule(ReactApplicationContext reactContext) {
        super(reactContext);
        PluginConfig.configDebugMode(true, false, 0);
        this.mContext = reactContext;
//        PluginConfig.configIpaynowEnv(PluginConfig.IpaynowEnv.ReleaseGlobal); // 生产
//        PluginConfig.configIpaynowEnv(PluginConfig.IpaynowEnv.ReleaseCn); // 生产
//        PluginConfig.configIpaynowEnv(PluginConfig.IpaynowEnv.PreRelease);// 预发布
//        PluginConfig.configIpaynowEnv(PluginConfig.IpaynowEnv.Test);// 测试
    }

    @Override
    public String getName() {
        return "IPNCrossBorder";
    }

    @ReactMethod
    public void pay(ReadableMap map, Promise promise) {
        mPromise = promise;
        //创建订单
        boolean result = createPayMessage(map);
        if (!result) {
            StringBuilder temp = new StringBuilder();
            WritableMap m = Arguments.createMap();
            temp.append("交易状态:失败").append("\n").append("错误码:").append("50000").append("原因: 后台返回字段缺失");
            m.putString("result", "failed");
            m.putString("msg", temp.toString());
            mPromise.resolve(m);
            mPromise = null;
        }
        // 生成请求参数并调起支付
        GetMessage gm = new GetMessage();
        gm.execute();
    }

    /**
     * 创建订单
     *
     * @param map 商户订单号
     */
    private boolean createPayMessage(ReadableMap map) {
        try {
            mPreSign.appId = map.getString("appId");
            mPreSign.mhtOrderNo = map.getString("mhtOrderNo");
            mPreSign.mhtOrderName = map.getString("mhtOrderName");
            mPreSign.mhtOrderAmt = map.getString("mhtOrderAmt");
            mPreSign.mhtOrderDetail = map.getString("mhtOrderDetail");
            mPreSign.mhtOrderStartTime = map.getString("mhtOrderStartTime");
            mPreSign.mhtReserved = map.getString("mhtReserved");
            mPreSign.notifyUrl = map.getString("notifyUrl");
            mPreSign.mhtOrderType = map.getString("mhtOrderType");
            mPreSign.mhtCurrencyType = map.getString("mhtCurrencyType");
            mPreSign.mhtOrderTimeOut = map.getString("mhtOrderTimeOut");
            mPreSign.mhtCharset = map.getString("mhtCharset");
            mPreSign.payChannelType = map.getString("payChannelType");  // 80 微信跨境支付 |  90 支付宝跨境支付
            mPreSign.mhtSubAppId = map.getString("mhtSubAppId");
            mPreSign.mhtSignature = map.getString("iPaySign");
            mPreSign.mhtAmtCurrFlag = map.getString("mhtAmtCurrFlag");
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public class GetMessage extends AsyncTask<String, Integer, Void> {
        protected Void doInBackground(String... params) {
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            CrossUnionPayPlugin.getInstance().setCallResultReceiver(IPNCrossBorderModule.this)// 传入继承了通知接口的类
                    .pay(mPreSign, getCurrentActivity());// 传入请求数据
        }
    }


    @Override
    public void onIpaynowTransResult(ResponseParams responseParams) {
        if (mPromise == null) {
            return;
        }
        String respCode = responseParams.respCode;
        String errorCode = responseParams.errorCode;
        String errorMsg = responseParams.respMsg;
        StringBuilder temp = new StringBuilder();
        WritableMap map = Arguments.createMap();

        if ("00".equals(respCode)) {
            temp.append("交易状态:成功");
            map.putString("result", "success");
            map.putString("msg", temp.toString());
        } else if ("02".equals(respCode)) {
            temp.append("交易状态:取消");
            map.putString("result", "cancel");
            map.putString("msg", temp.toString());
        } else if ("01".equals(respCode)) {
            temp.append("交易状态:失败").append("\n").append("错误码:").append(errorCode).append("原因:" + errorMsg);
            map.putString("result", "failed");
            map.putString("msg", temp.toString());
        } else if ("03".equals(respCode)) {
            temp.append("交易状态:未知").append("\n").append("原因:" + errorMsg);
            map.putString("result", "unknow");
            map.putString("msg", temp.toString());
        } else {
            temp.append("respCode=").append(respCode).append("\n").append("respMsg=").append(errorMsg);
            map.putString("result", "other error");
            map.putString("msg", temp.toString());
        }

        mPromise.resolve(map);
        mPromise = null;
    }


    @ReactMethod
    public void aliPay(final String orderInfo,Callback callback) {
        callback.invoke("");
        // 异步任务
        IPNCrossBorderModule.AliPayTask aliPayTask = new IPNCrossBorderModule.AliPayTask();
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
            System.out.println("alipay call2222------- "+payResult.toString());
            WritableMap map = Arguments.createMap();
            map.putString("mome",memo);
            map.putString("result",result);
            map.putString("status",resultStatus);
            System.out.println("alipay call33333------- "+payResult.toString());

            sendEvent(mContext,"aliPayCallback",map);
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



    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {
        CrossUnionPayPlugin.getInstance().onActivityDestroy();
    }


}
