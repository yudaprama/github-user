//
//  RCCDrawerProtocol.h
//  tebengan_apps
//
//  Created by yuda on 10/1/18.
//  Copyright © 2018 Tebengan. All rights reserved.
//

@class RCTBridge;


@protocol RCCDrawerDelegate <NSObject>

@property (nonatomic, strong) UIButton *overlayButton;
@property (nonatomic, strong) NSDictionary *drawerStyle;

@required
- (instancetype)initWithProps:(NSDictionary *)props children:(NSArray *)children globalProps:(NSDictionary*)globalProps bridge:(RCTBridge *)bridge;
- (void)performAction:(NSString*)performAction actionParams:(NSDictionary*)actionParams bridge:(RCTBridge *)bridge;

@end
