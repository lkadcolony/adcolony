#import "AdColonyPlugin.h"
#import <objc/runtime.h>
#import <objc/message.h>

@implementation AdColonyPlugin

@synthesize callbackIdKeepCallback;
//
@synthesize appId;
@synthesize fullScreenAdZoneId;
@synthesize rewardedVideoAdZoneId;

- (void) pluginInitialize {
    [super pluginInitialize];
}

- (void) setUp: (CDVInvokedUrlCommand*)command {
	NSString* appId = [command.arguments objectAtIndex:0];
	NSString* fullScreenAdZoneId = [command.arguments objectAtIndex:1];
	NSString* rewardedVideoAdZoneId = [command.arguments objectAtIndex:2];
	
    self.callbackIdKeepCallback = command.callbackId;
	[self _setUp:appId aFullScreenAdZoneId:fullScreenAdZoneId aRewardedVideoAdZoneId:rewardedVideoAdZoneId];
}

- (void) showFullScreenAd: (CDVInvokedUrlCommand*)command {

    [self.commandDelegate runInBackground:^{
		[self _showFullScreenAd];
    }];
}

- (void) showRewardedVideoAd: (CDVInvokedUrlCommand*)command {

    [self.commandDelegate runInBackground:^{
		[self _showRewardedVideoAd];
    }];
}

- (void) _setUp:(NSString *)appId aFullScreenAdZoneId:(NSString *)fullScreenAdZoneId aRewardedVideoAdZoneId:(NSString *)rewardedVideoAdZoneId {
	self.appId = appId;
	self.fullScreenAdZoneId = fullScreenAdZoneId;
	self.rewardedVideoAdZoneId = rewardedVideoAdZoneId;
	//
    BOOL debug = NO;

	NSArray* zoneIds = [NSArray arrayWithObjects: self.fullScreenAdZoneId, self.rewardedVideoAdZoneId, nil];
	
	[AdColony configureWithAppID:self.appId 
		zoneIDs:zoneIds
		delegate:[[MyAdColonyDelegate alloc] initWithAdColonyPlugin:self]
		logging:debug
	];
}

-(void) _showFullScreenAd {
    if (![AdColony videoAdCurrentlyRunning]) {
        [AdColony playVideoAdForZone:fullScreenAdZoneId 
			withDelegate:[[AdColonyAdDelegateFullScreenAd alloc] initWithAdColonyPlugin:self]
		];
    }	
}

-(void) _showRewardedVideoAd {

    if (![AdColony videoAdCurrentlyRunning]) {
        [AdColony playVideoAdForZone:rewardedVideoAdZoneId
			withDelegate:[[AdColonyAdDelegateRewardedVideoAd alloc] initWithAdColonyPlugin:self]
		];
    }
}

@end

@implementation MyAdColonyDelegate

@synthesize adColonyPlugin;

- (id) initWithAdColonyPlugin:(AdColonyPlugin *)adColonyPlugin_ {
    self = [super init];
    if (self) {
        self.adColonyPlugin = adColonyPlugin_;
    }
    return self;
}	

- (void)onAdColonyAdAvailabilityChange:(BOOL)available inZone:(NSString *)zoneId {
	if (available) {	
        if ([zoneId isEqualToString:self.adColonyPlugin.fullScreenAdZoneId]) {
			CDVPluginResult* pr = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"onFullScreenAdLoaded"];
			[pr setKeepCallbackAsBool:YES];
			[adColonyPlugin.commandDelegate sendPluginResult:pr callbackId:adColonyPlugin.callbackIdKeepCallback];	
		}
        else if ([zoneId isEqualToString:self.adColonyPlugin.rewardedVideoAdZoneId]) {
			CDVPluginResult* pr = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"onRewardedVideoAdLoaded"];
			[pr setKeepCallbackAsBool:YES];
			[adColonyPlugin.commandDelegate sendPluginResult:pr callbackId:adColonyPlugin.callbackIdKeepCallback];
		}		
	}
}

- (void)onAdColonyV4VCReward:(BOOL)success currencyName:(NSString *)currencyName currencyAmount:(int)amount inZone:(NSString *)zoneId {
    if (success) {
    	CDVPluginResult* pr = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"onRewardedVideoAdCompleted"];
		[pr setKeepCallbackAsBool:YES];
		[adColonyPlugin.commandDelegate sendPluginResult:pr callbackId:adColonyPlugin.callbackIdKeepCallback];
    } 
}

@end

@implementation AdColonyAdDelegateFullScreenAd

@synthesize adColonyPlugin;

- (id) initWithAdColonyPlugin:(AdColonyPlugin *)adColonyPlugin_ {
    self = [super init];
    if (self) {
        self.adColonyPlugin = adColonyPlugin_;
    }
    return self;
}

- (void)onAdColonyAdStartedInZone:(NSString *)zoneId {
	CDVPluginResult* pr = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"onFullScreenAdShown"];
	[pr setKeepCallbackAsBool:YES];
	[adColonyPlugin.commandDelegate sendPluginResult:pr callbackId:adColonyPlugin.callbackIdKeepCallback];
}

- (void)onAdColonyAdAttemptFinished:(BOOL)shown inZone:(NSString *)zoneId {
    if (shown) {
		CDVPluginResult* pr = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"onFullScreenAdHidden"];
		[pr setKeepCallbackAsBool:YES];
		[adColonyPlugin.commandDelegate sendPluginResult:pr callbackId:adColonyPlugin.callbackIdKeepCallback];
    } 
}

@end

@implementation AdColonyAdDelegateRewardedVideoAd

@synthesize adColonyPlugin;

- (id) initWithAdColonyPlugin:(AdColonyPlugin *)adColonyPlugin_ {
    self = [super init];
    if (self) {
        self.adColonyPlugin = adColonyPlugin_;
    }
    return self;
}

- (void)onAdColonyAdStartedInZone:(NSString *)zoneId
{
	CDVPluginResult* pr = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"onRewardedVideoAdShown"];
	[pr setKeepCallbackAsBool:YES];
	[adColonyPlugin.commandDelegate sendPluginResult:pr callbackId:adColonyPlugin.callbackIdKeepCallback];
}

- (void)onAdColonyAdAttemptFinished:(BOOL)shown inZone:(NSString *)zoneId {
    if (shown) {
		CDVPluginResult* pr = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"onRewardedVideoAdHidden"];
		[pr setKeepCallbackAsBool:YES];
		[adColonyPlugin.commandDelegate sendPluginResult:pr callbackId:adColonyPlugin.callbackIdKeepCallback];
    } 
}

@end
