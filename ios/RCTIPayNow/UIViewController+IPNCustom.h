//
//  UIViewController+Custom.h
//  heyteago
//
//  Created by marvin on 2018/11/21.
//  Copyright © 2018年 marvin. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIViewController (IPNCustom)<UINavigationControllerDelegate>
//获得当前导航控制器
+ (UINavigationController *)getRootNavVc;

//获取当前屏幕显示的控制器
+ (UIViewController *)getCurrentVC;


@end

NS_ASSUME_NONNULL_END
