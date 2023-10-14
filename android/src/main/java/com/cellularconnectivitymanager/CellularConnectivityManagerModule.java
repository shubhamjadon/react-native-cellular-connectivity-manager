package com.cellularconnectivitymanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

@ReactModule(name = CellularConnectivityManagerModule.NAME)
public class CellularConnectivityManagerModule extends ReactContextBaseJavaModule {
  public static final String NAME = "CellularConnectivityManager";
  private static ConnectivityManager mConnectivityManager;
  private static ConnectivityNetworkCallback mNetworkCallback;
  private ReactApplicationContext appContext;
  private static final int TIMEOUT = 1000;
  private int mobileDataStatusListenerCount = 0;
  private static final String EVENT_NAME = "MobileDataStatus";
  private boolean toRetryForceCellularOnUnavailable = true;
  private boolean isNetworkCallbackAlreadyActive = false;

  public CellularConnectivityManagerModule(ReactApplicationContext reactContext) {
    super(reactContext);

    appContext = reactContext;
    mConnectivityManager =
      (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    mNetworkCallback = new ConnectivityNetworkCallback();
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void forceDataFlowThroughCellularNetwork() {
    forceCellularNetwork();
  }

  @ReactMethod
  public void resetDataFlowToDefault() {
    mConnectivityManager.bindProcessToNetwork(null);

    try {
      mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
    } catch(Exception e) {}

    isNetworkCallbackAlreadyActive = false;
    toRetryForceCellularOnUnavailable = true;
  }

  @ReactMethod
  public void addListener(String eventName) {
    mobileDataStatusListenerCount += 1;
  }

  @ReactMethod
  public void removeListeners(Integer count) {
    mobileDataStatusListenerCount -= count;
  }

  private void forceCellularNetwork() {
    if(isNetworkCallbackAlreadyActive) {
      return;
    }

    isNetworkCallbackAlreadyActive = true;
    NetworkRequest request = new NetworkRequest.Builder()
      .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
      .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
      .build();

    if(toRetryForceCellularOnUnavailable) {
      mConnectivityManager.requestNetwork(request, mNetworkCallback, TIMEOUT);
    } else {
      mConnectivityManager.requestNetwork(request, mNetworkCallback);
    }
  }

  private void sendEvent(boolean isMobileDataOn) {
    if(mobileDataStatusListenerCount > 0) {
      appContext
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit(EVENT_NAME, isMobileDataOn);
    }
  }

  private class ConnectivityNetworkCallback extends ConnectivityManager.NetworkCallback {
    @Override
    public void onAvailable(Network network) {
      super.onAvailable(network);
      sendEvent(true);
      mConnectivityManager.bindProcessToNetwork(network);
    }

    @Override
    public void onLost(Network network) {
      super.onLost(network);
      sendEvent(false);
    }

    @Override
    public void onUnavailable() {
      super.onUnavailable();
      
      isNetworkCallbackAlreadyActive = false;
      sendEvent(false);

      if(toRetryForceCellularOnUnavailable) {
        toRetryForceCellularOnUnavailable = false;
        forceCellularNetwork();
      }
    }
  }
}
