
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNCellularConnectivityManagerSpec.h"

@interface CellularConnectivityManager : NSObject <NativeCellularConnectivityManagerSpec>
#else
#import <React/RCTBridgeModule.h>

@interface CellularConnectivityManager : NSObject <RCTBridgeModule>
#endif

@end
