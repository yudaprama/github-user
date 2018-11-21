//
//  RCCTitleViewHelper.h
//  tebengan_apps
//
//  Created by yuda on 10/1/18.
//  Copyright © 2018 Tebengan. All rights reserved.
//

#ifndef RCCTitleViewHelper_h
#define RCCTitleViewHelper_h

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface RCCTitleView : UIView

@property (nonatomic, strong) UILabel *titleLabel;

@property (nonatomic, strong) UILabel *subtitleLabel;

@end

@interface RCCTitleViewHelper : NSObject


- (instancetype)init:(UIViewController*)viewController
navigationController:(UINavigationController*)navigationController
               title:(NSString*)title subtitle:(NSString*)subtitle
      titleImageData:(id)titleImageData
       isSetSubtitle:(BOOL)isSetSubtitle;

-(void)setup:(NSDictionary*)style;

@end

#endif /* RCCTitleViewHelper_h */
