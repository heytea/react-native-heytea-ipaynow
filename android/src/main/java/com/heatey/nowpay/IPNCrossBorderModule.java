package com.heatey.nowpay;


import android.os.AsyncTask;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.ipaynow.unionpay.plugin.api.CrossUnionPayPlugin;
import com.ipaynow.unionpay.plugin.conf.PluginConfig;
import com.ipaynow.unionpay.plugin.manager.route.dto.ResponseParams;
import com.ipaynow.unionpay.plugin.manager.route.impl.ReceivePayResult;
import com.ipaynow.unionpay.plugin.utils.PreSignMessageUtil;


import java.net.URLEncoder;

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
//        PluginConfig.configDebugMode(true, false, 0);
        this.mContext = reactContext;
    }

    @Override
    public String getName() {
        return "IPNCrossBorder";
    }

    @ReactMethod
    public void pay(ReadableMap map, Promise promise) {
        this.mPromise = promise;
        //创建订单
        creatPayMessage(map);
        // 生成请求参数并调起支付
        GetMessage gm = new GetMessage();
        gm.execute();
    }


    /**
     * 创建订单
     *
     * @param map 商户订单号
     */
    private void creatPayMessage(ReadableMap map) {
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
        mPreSign.payChannelType = map.getString("payChannelType");
        mPreSign.mhtSubAppId = map.getString("mhtSubAppId");
        mPreSign.mhtSignature = map.getString("iPaySign");
        mPreSign.mhtAmtCurrFlag = map.getString("mhtAmtCurrFlag");
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
        String respCode = responseParams.respCode;
        String errorCode = responseParams.errorCode;
        String errorMsg = responseParams.respMsg;
        StringBuilder temp = new StringBuilder();
        WritableMap map = Arguments.createMap();

        if (respCode.equals("00")) {
            temp.append("交易状态:成功");
            map.putString("result","success");
            mPromise.resolve(map);
        } else if (respCode.equals("02")) {
            temp.append("交易状态:取消");
            map.putString("result","cancel");
            mPromise.resolve(map);
        } else if (respCode.equals("01")) {
            temp.append("交易状态:失败").append("\n").append("错误码:").append(errorCode).append("原因:" + errorMsg);
            mPromise.reject("failed");
        } else if (respCode.equals("03")) {
            temp.append("交易状态:未知").append("\n").append("原因:" + errorMsg);
            mPromise.reject("unknow");
        } else {
            temp.append("respCode=").append(respCode).append("\n").append("respMsg=").append(errorMsg);
            mPromise.reject("other error");
        }
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
