//
//  RCTHelpers.h
//  tebengan_apps
//
//  Created by yuda on 10/1/18.
//  Copyright © 2018 Tebengan. All rights reserved.
//

#ifndef RCTHelpers_h
#define RCTHelpers_h

#import <Foundation/Foundation.h>
#import <React/RCTRootView.h>

@interface RCTHelpers : NSObject

+(BOOL)removeYellowBox:(RCTRootView*)reactRootView;

+ (NSMutableDictionary *)textAttributesFromDictionary:(NSDictionary *)dictionary
                                           withPrefix:(NSString *)prefix;

+ (NSMutableDictionary *)textAttributesFromDictionary:(NSDictionary *)dictionary
                                           withPrefix:(NSString *)prefix baseFont:(UIFont *)font;

+ (NSString *)getTimestampString;

@end

#endif /* RCTHelpers_h */
