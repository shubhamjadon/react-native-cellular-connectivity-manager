#import "CellularConnectivityManager.h"
#import <CoreTelephony/CTTelephonyNetworkInfo.h>

@implementation CellularConnectivityManager
RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(isAirplaneEnabled:(RCTPromiseResolveBlock)resolve (RCTPromiseRejectBlock)reject){
      NSDictionary<NSString *,NSString *> * radio = [[CTTelephonyNetworkInfo alloc] init].serviceCurrentRadioAccessTechnology;
      bool isEnabled = radio == nil;
      resolve([NSNumber numberWithBool:isEnabled]);
}


@end
