//
//  UIViewController+Rotation.h
//  tebengan_apps
//
//  Created by yuda on 24/3/18.
//  Copyright © 2018 Tebengan. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIViewController (Rotation)


-(void)setRotation:(NSDictionary*)style;
-(UIInterfaceOrientationMask)supportedControllerOrientations;

@end
