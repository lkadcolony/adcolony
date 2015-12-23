#import <Cordova/CDV.h>
#import <AdColony/AdColony.h>

@interface AdColonyPlugin : CDVPlugin

@property NSString *callbackIdKeepCallback;
//
@property NSString *appId;
@property NSString *fullScreenAdZoneId;
@property NSString *rewardedVideoAdZoneId;
	
- (void) showFullScreenAd:(CDVInvokedUrlCommand*)command;
- (void) showRewardedVideoAd:(CDVInvokedUrlCommand*)command;

@end

@interface MyAdColonyDelegate : NSObject <AdColonyDelegate>

@property AdColonyPlugin *adColonyPlugin;

- (id) initWithAdColonyPlugin:(AdColonyPlugin *)adColonyPlugin_ ;

@end

@interface AdColonyAdDelegateFullScreenAd : NSObject <AdColonyAdDelegate>

@property AdColonyPlugin *adColonyPlugin;

- (id) initWithAdColonyPlugin:(AdColonyPlugin *)adColonyPlugin_ ;

@end

@interface AdColonyAdDelegateRewardedVideoAd : NSObject <AdColonyAdDelegate>

@property AdColonyPlugin *adColonyPlugin;

- (id) initWithAdColonyPlugin:(AdColonyPlugin *)adColonyPlugin_ ;

@end
