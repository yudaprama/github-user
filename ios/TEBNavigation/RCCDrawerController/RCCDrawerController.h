//
//  RCCDrawerController.h
//  tebengan_apps
//
//  Created by yuda on 10/1/18.
//  Copyright © 2018 Tebengan. All rights reserved.
//

#ifndef RCCDrawerController_h
#define RCCDrawerController_h

#import <UIKit/UIKit.h>
#import <React/RCTBridge.h>
#import "MMDrawerController.h"
#import "RCCDrawerProtocol.h"


@interface RCCDrawerController : MMDrawerController <RCCDrawerDelegate>


@end

#endif /* RCCDrawerController_h */
