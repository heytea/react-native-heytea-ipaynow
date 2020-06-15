"use strict";
import { NativeModules, NativeEventEmitter, Platform } from 'react-native';
const { IPNCrossBorder } = NativeModules;
const IPNEmitter = new NativeEventEmitter(IPNCrossBorder);
export const ipnPay = param => {
    if (Platform.OS === 'ios') {
      return new Promise((resolve, reject) => {
        IPNCrossBorder.pay(param, err => {
          if (err) {
            reject(err);
          } else {
            IPNEmitter.addListener('IPN_Resp', resp => {
              resolve(resp);
            });
          }
        });
      });
    }
    return IPNCrossBorder.pay(param);
  };

  export const aliPay = orderInfo => {

    return IPNCrossBorder.aliPay(orderInfo)

  }
  