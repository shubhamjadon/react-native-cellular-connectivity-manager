#import "CellularConnectivityManager.h"
#import <CoreTelephony/CTTelephonyNetworkInfo.h>

@interface CellularConnectivityManager ()

@property (nonatomic, strong) CTTelephonyNetworkInfo *telephonyInfo;

@end

@implementation CellularConnectivityManager
RCT_EXPORT_MODULE()

- (NSArray<NSString *> *) supportedEvents{
      return @[@"airplaneModeChanged"];
}

- (instancetype)init {
      self = [super init];
      if(self){
            self.telephonyInfo = [[CTTelephonyNetworkInfo alloc ] init];
            [self.telephonyInfo setDelegate:self];
      }
}

RCT_EXPORT_METHOD(isAirplaneEnabled:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
      NSDictionary<NSString *,NSString *> * radio = [[CTTelephonyNetworkInfo alloc] init].serviceCurrentRadioAccessTechnology;
      bool isEnabled = radio.count == 0;
      resolve([NSNumber numberWithBool:isEnabled]);
}

- (void)dataServiceIdentifierDidChange:(NSString *)identifier {
      NSString *currentRadioAccessTechnology = self.telephonyInfo.currentRadioAccessTechnology;

      if(currentRadioAccessTechnology == nil){
            // Airplane enabled
            [self sendDeviceEventWithName:@"airplaneModeChanged" body:@YES];
      }else{
            // Airplane disabled
            [self sendDeviceEventWithName:@"airplaneModeChanged" body:@NO];
      }
}

@end

