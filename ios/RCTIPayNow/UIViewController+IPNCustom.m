//
//  UIViewController+IPNCustom.m
//  heyteago
//
//  Created by marvin on 2018/11/21.
//  Copyright © 2018年 marvin. All rights reserved.
//

#import "UIViewController+IPNCustom.h"

@implementation UIViewController (IPNCustom)
- (void)navigationController:(UINavigationController*)navigationController willShowViewController:(UIViewController*)viewController animated:(BOOL)animated {
    
    if(viewController == self){
        [navigationController setNavigationBarHidden:YES animated:YES];
    }else{
        if ([navigationController isKindOfClass:[UIImagePickerController class]]) {
            return;
        }
        [navigationController setNavigationBarHidden:NO animated:YES];
        if(navigationController.delegate == self){
            navigationController.delegate = nil;
        }
    }
}


//获得当前导航控制器
+ (UINavigationController *)getRootNavVc {
    UIWindow * window = [[UIApplication sharedApplication] keyWindow];
    
    return (UINavigationController *)window.rootViewController;
}

//获取当前屏幕显示的viewcontroller
+ (UIViewController *)getCurrentVC {
    UIViewController *rootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
    
    UIViewController *currentVC = [self getCurrentVCFrom:rootViewController];
    
    return currentVC;
}

+ (UIViewController *)getCurrentVCFrom:(UIViewController *)rootVC {
    UIViewController *currentVC;
    
    if ([rootVC presentedViewController]) {
        
        rootVC = [rootVC presentedViewController];
    }
    
    if ([rootVC isKindOfClass:[UITabBarController class]]) {
        
        currentVC = [self getCurrentVCFrom:[(UITabBarController *)rootVC selectedViewController]];
        
    } else if ([rootVC isKindOfClass:[UINavigationController class]]){
        
        currentVC = [self getCurrentVCFrom:[(UINavigationController *)rootVC visibleViewController]];
        
    } else {
        
        currentVC = rootVC;
    }
    
    return currentVC;
}
@end
