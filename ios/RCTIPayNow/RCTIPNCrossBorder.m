
//
//  RCTIPNCrossBorder.m
//  heyteago
//
//  Created by marvin on 2018/10/31.
//  Copyright © 2018年 marvin. All rights reserved.
//

#import "RCTIPNCrossBorder.h"
#import "IPNCrossBorderPreSignUtil.h"
#import "IPNCrossBorderPluginAPi.h"
#import "IPNDESUtil.h"
#import "UIViewController+IPNCustom.h"
#import <UIKit/UIKit.h>
#import <AlipaySDK/AlipaySDK.h>

#define INVOKE_FAILED (@"IPNCrossBoder API invoke failed")
#define RCTIPNEventName @"IPN_Resp"
#define IPNCNotification @"IPNCNotification"

@interface RCTIPNCrossBorder ()

@end


static RCTPromiseResolveBlock _resolve;
static RCTPromiseRejectBlock _reject;

@implementation RCTIPNCrossBorder

RCT_EXPORT_MODULE()

- (instancetype)init {
    
    if (self = [super init]) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(sendEventWithInfo:) name:IPNCNotification object:nil];
    }
    return self;
}


- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}




+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (NSArray<NSString *> *)supportedEvents {
    
    return @[RCTIPNEventName];
    
}


// 参数
RCT_EXPORT_METHOD(pay:(NSDictionary *)data
                  :(RCTResponseSenderBlock)callback
                  ) {
    
    BOOL isSuccess = [self payWithParam:data];
    callback(@[isSuccess ? [NSNull null] : INVOKE_FAILED]);
    
}

- (BOOL)payWithParam:(NSDictionary *)paramDict {
    
    [IPNCrossBorderPluginAPi setBeforeReturnLoadingHidden:YES];
    IPNCrossBorderPreSignUtil *preSign = [[IPNCrossBorderPreSignUtil alloc] init];
    preSign.appId = paramDict[@"appId"]; //appid
    preSign.mhtOrderNo = paramDict[@"mhtOrderNo"]; //订单id
    preSign.mhtOrderName = paramDict[@"mhtOrderName"]; //订单名称
    preSign.mhtOrderType = paramDict[@"mhtOrderType"]; //01普通消费
    preSign.mhtCurrencyType =  paramDict[@"mhtCurrencyType"];//"商户订单币种类型"
    preSign.payChannelType =  paramDict[@"payChannelType"];;//支付宝支付:90 ，微信支付:80
    preSign.mhtOrderAmt = paramDict[@"mhtOrderAmt"];//  @"商户订单交易金额";
    preSign.mhtOrderDetail = paramDict[@"mhtOrderDetail"];// @"商户订单详情"
    preSign.mhtOrderStartTime = paramDict[@"mhtOrderStartTime"];//商户订单开始时间
    preSign.notifyUrl = paramDict[@"notifyUrl"];//"商户后台通知URL"HTTPS协议
    preSign.mhtCharset =  paramDict[@"mhtCharset"];// @"UTF-8";商户字符编码
    preSign.mhtOrderTimeOut = paramDict[@"mhtOrderTimeOut"];//  @商户订单超时时间
    preSign.mhtAmtCurrFlag = paramDict[@"mhtAmtCurrFlag"];//金额币种单位标记   @"mhtAmtCurrFlag"; //0订单金额单位为人民币  // 1商户申请的结算币种类型
    preSign.mhtSubAppId =  paramDict[@"mhtSubAppId"];// 微信子商户appid;
    preSign.mhtReserved = paramDict[@"mhtReserved"];//  商户保留域,商户可以对交易进行标记， 现在支付将原样返回给商户
    
    NSString *backendSign = paramDict[@"iPaySign"];
    NSString *iOSScheme = @"IPaynowCrossBorder9832d23hd23";
    
    NSString *preSignStr = [preSign generatePresignMessage];
    
    NSString* payData=[preSignStr stringByAppendingString:@"&"];
    NSString *md5 = [NSString stringWithFormat:@"mhtSignType=MD5&mhtSignature=%@",backendSign];
    payData=[payData stringByAppendingString:md5];
   BOOL isSuccess = [IPNCrossBorderPluginAPi pay:payData AndScheme:iOSScheme viewController:[UIViewController getCurrentVC] delegate:self universalLink:@"https://ewbuq.share2dlink.com/"];
    return isSuccess;
    
}

//现在支付的代理回调
- (void)iPNCrossBorderPluginResult:(IPNCrossBorderPayResult)result erroCode:(NSString *)erroCode erroInfo:(NSString *)erroInfo {
    
    NSString *resultStr = @"unknown";
    switch (result) {
        case IPNCrossBorderPayResultFail:
            resultStr = @"fail";
            break;
        case IPNCrossBorderPayResultCancel:
            resultStr = @"cancel";
            break;
        case IPNCrossBorderPayResultSuccess:
            resultStr = @"success";
            break;
        case  IPNCrossBorderPayResultUnknown:
            resultStr = @"unknown";
        default:
            break;
    }
    
     NSDictionary *body = @{
                               @"errCode":erroCode ? erroCode:@"",
                               @"errInfo":erroInfo ? erroInfo:@"",
                               @"result":resultStr
                               };

    [[NSNotificationCenter defaultCenter] postNotificationName:IPNCNotification object:nil userInfo:body];
    
}

- (void)sendEventWithInfo:(NSNotification *)notification {
    
    if (notification.userInfo) {
        [self sendEventWithName:RCTIPNEventName body:notification.userInfo];
    }
}

RCT_REMAP_METHOD(aliPay, payInfo:(NSString *)payInfo resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  NSArray *urls = [[NSBundle mainBundle] infoDictionary][@"CFBundleURLTypes"];
  NSMutableString *appScheme = [NSMutableString string];
  BOOL multiUrls = [urls count] > 1;
  for (NSDictionary *url in urls) {
    NSArray *schemes = url[@"CFBundleURLSchemes"];
    if (!multiUrls ||
        (multiUrls && [@"alipay" isEqualToString:url[@"CFBundleURLName"]])) {
      [appScheme appendString:schemes[0]];
      break;
    }
  }
  
  if ([appScheme isEqualToString:@""]) {
    NSString *error = @"scheme cannot be empty";
    reject(@"10000", error, [NSError errorWithDomain:error code:10000 userInfo:NULL]);
    return;
  }
  
  _resolve = resolve;
  _reject = reject;
  
  
  [[AlipaySDK defaultService] payOrder:payInfo fromScheme:appScheme callback:^(NSDictionary *resultDic) {
    [RCTIPNCrossBorder handleResult:resultDic];
  }];
}

+(void) handleResult:(NSDictionary *)resultDic
{
  NSString *status = resultDic[@"resultStatus"];
  _resolve(@{@"status":status,@"mome":resultDic[@"memo"]});
  
}


- (void)dealloc {
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
}


@end

