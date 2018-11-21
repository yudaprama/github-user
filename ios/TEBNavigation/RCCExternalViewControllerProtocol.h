//
//  RCCExternalViewControllerProtocol.h
//  tebengan_apps
//
//  Created by yuda on 10/1/18.
//  Copyright © 2018 Tebengan. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCCViewController.h"

@protocol RCCExternalViewControllerProtocol <NSObject>

@property (nullable, nonatomic, weak) id <RCCViewControllerDelegate> controllerDelegate;

-(void)setProps:(nullable NSDictionary*)props;

@end

