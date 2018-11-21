//
//  RCCTheSideBarManagerViewController.h
//  tebengan_apps
//
//  Created by yuda on 10/1/18.
//  Copyright © 2018 Tebengan. All rights reserved.
//

#import "TheSidebarController.h"
#import "RCCDrawerProtocol.h"

typedef NS_ENUM(NSInteger,TheSideBarSide){
  TheSideBarSideNone = 0,
  TheSideBarSideLeft,
  TheSideBarSideRight,
};


@interface RCCTheSideBarManagerViewController : TheSidebarController <RCCDrawerDelegate>


@end
