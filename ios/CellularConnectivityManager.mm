#import "CellularConnectivityManager.h"
#import <CoreTelephony/CTTelephonyNetworkInfo.h>

@implementation CellularConnectivityManager
RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(isAirplaneEnabled:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
      NSDictionary<NSString *,NSString *> * radio = [[CTTelephonyNetworkInfo alloc] init].serviceCurrentRadioAccessTechnology;
      bool isEnabled = radio.count == 0;
      resolve([NSNumber numberWithBool:isEnabled]);
}

@end

