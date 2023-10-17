import { NativeEventEmitter, NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-cellular-connectivity-manager' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const CellularConnectivityManager = NativeModules.CellularConnectivityManager
  ? NativeModules.CellularConnectivityManager
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function switchToCellularInternet() {
  CellularConnectivityManager?.forceDataFlowThroughCellularNetwork?.();
}

export function switchToDefaultInternet() {
  CellularConnectivityManager?.resetDataFlowToDefault?.();
}

export const MobileDataStatus = new NativeEventEmitter(
  CellularConnectivityManager
);

export const MOBILE_DATA_STATUS_EVENT = 'MobileDataStatus';

export function registerAirplaneModeListener() {
  CellularConnectivityManager?.registerAirplaneModeListener?.();
}

export function unregisterAirplaneModeListener() {
  CellularConnectivityManager?.unregisterAirplaneModeListener?.();
}

export const AIRPLANE_MODE_EVENT = 'EventAirplaneChange';
