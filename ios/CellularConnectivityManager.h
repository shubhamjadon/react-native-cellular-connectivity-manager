
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNCellularConnectivityManagerSpec.h"

@interface CellularConnectivityManager : NSObject <NativeCellularConnectivityManagerSpec>
#else
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <CoreTelephony/CTTelephonyNetworkInfo.h>

@interface CellularConnectivityManager : RCTEventEmitter <RCTBridgeModule, CTTelephonyNetworkInfoDelegate>
#endif

@end
