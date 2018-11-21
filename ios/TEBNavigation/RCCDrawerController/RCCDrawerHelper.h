//
//  RCCDrawerHelper.h
//  tebengan_apps
//
//  Created by yuda on 10/1/18.
//  Copyright © 2018 Tebengan. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#define DRAWER_DEFAULT_WIDTH_PERCENTAGE 80

typedef NS_ENUM(NSInteger,RCCDrawerSide){
  RCCDrawerSideLeft,
  RCCDrawerSideRight
};


@interface RCCDrawerHelper : NSObject

+(UIButton*)createOverlayButton:(id)target;

+(void)addOverlayButtonToScreen:(UIButton*)buttonToAdd
                    contextView:(UIView*)view
                           side:(RCCDrawerSide)side
                  sideMenuWidth:(CGFloat)sideMenuWidth
              animationDuration:(CGFloat)duration;

+(void)overlayButtonPressed:(UIButton*)button animationDuration:(CGFloat)duration;
+(UIImage *)imageWithImage:(UIImage *)image scaledToSize:(CGSize)newSize;



@end

