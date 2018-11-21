//
//  RCCCustomTitleView.h
//  tebengan_apps
//
//  Created by yuda on 10/1/18.
//  Copyright © 2018 Tebengan. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <React/RCTRootView.h>
#import <React/RCTRootViewDelegate.h>

@interface RCCCustomTitleView : UIView <RCTRootViewDelegate>

- (instancetype)initWithFrame:(CGRect)frame subView:(RCTRootView*)subView alignment:(NSString*)alignment;
- (void)viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator;

@end

