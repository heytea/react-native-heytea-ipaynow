declare module "@heytea/react-native-ipaynow" {

    export interface IIPayNowPayParam {
      appId: string; //商户应用唯一标识
      mhtOrderNo: string; //商户订单号
      mhtOrderName: string; //商户商品名称
      mhtOrderType: string; //01 普通消费  04退货
      mhtCurrencyType: string; //货币类型  USD HKD
      payChannelType:  '80' | '90'; // 80 微信跨境 90支付宝跨境
      mhtOrderAmt: string; //商户订单交易金额
      mhtOrderDetail: string; //商户订单详情
      mhtOrderStartTime: string;//商户订单开始时间
      notifyUrl: string; //商户后台通知URL
      mhtCharset: string; //商户字符编码
      mhtOrderTimeOut: number; //商户订单超时时间 默认3600s
      mhtAmtCurrFlag: string; //金额币种单位标记 0 人民币  1 商户申请的结算币种类 型
      mhtSubAppId: string; // 子商户应用id
      mhtReserved: string; //商户保留域
      iPaySign: string; //服务端返回的签名
      iOSScheme: string; //iOS的scheme (跳转用)
    }

    export interface IIPayNowPayResponse {
        errCode:string;
        errInfo:string;
        result:string;
    }


    export function ipnPay(param:IIPayNowPayParam):Promise<IIPayNowPayResponse>  
    export function aliPay(orderInfo:string):Promise<{status: string; mome: string}>

}