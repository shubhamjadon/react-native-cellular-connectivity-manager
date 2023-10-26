#import "CellularConnectivityManager.h"
#import <CoreTelephony/CTTelephonyNetworkInfo.h>

@implementation CellularConnectivityManager
RCT_EXPORT_MODULE()

@interface CellularConnectivityManagerDelegate : NSObject <CTTelephonyNetworkInfoDelegate>

- (void)dataServiceIdentifierDidChange:(NSString *)identifier;

@end

@property (nonatomic, strong) CellularConnectivityManagerDelegate *delegate;


RCT_EXPORT_METHOD(isAirplaneEnabled:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
      NSDictionary<NSString *,NSString *> * radio = [[CTTelephonyNetworkInfo alloc] init].serviceCurrentRadioAccessTechnology;
      bool isEnabled = radio.count == 0;
      resolve([NSNumber numberWithBool:isEnabled]);
}

- (void)setDataServiceIdentifierDidChangeDelegate:(CellularConnectivityManagerDelegate *)delegate {
    self.delegate = delegate;
}

@end

@implementation CellularConnectivityManagerDelegate

- (void)dataServiceIdentifierDidChange:(NSString *)identifier {
  // Get the new radio access technology for the service.
  CTRadioAccessTechnology RAT = [CTTelephonyNetworkInfo serviceCurrentRadioAccessTechnology:identifier];

  // Send an event to React Native with the new RAT.
  NSDictionary *eventData = @{@"RAT": CTGetRadioAccessTechnologyString(RAT)};
  [RCTDeviceEventEmitter sendDeviceEventWithName:@"cellularConnectivityDidChange" body:eventData];
}

@end
