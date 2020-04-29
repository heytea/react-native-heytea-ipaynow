//
//  RCTIPNCrossBorder.h
//  heyteago
//
//  Created by marvin on 2018/10/31.
//  Copyright © 2018年 marvin. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import "IPNCrossBorderPluginDelegate.h"

NS_ASSUME_NONNULL_BEGIN

@interface RCTIPNCrossBorder : RCTEventEmitter<RCTBridgeModule,IPNCrossBorderPluginDelegate>

@end

NS_ASSUME_NONNULL_END
