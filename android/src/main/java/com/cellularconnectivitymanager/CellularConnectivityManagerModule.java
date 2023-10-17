package com.cellularconnectivitymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

@ReactModule(name = CellularConnectivityManagerModule.NAME)
public class CellularConnectivityManagerModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
  public static final String NAME = "CellularConnectivityManager";
  private ReactApplicationContext appContext;

  private static ConnectivityManager mConnectivityManager;
  private static ConnectivityNetworkCallback mNetworkCallback;
  private static final int NETWORK_CALLBACK_TIMEOUT = 1000;
  private static final String MOBILE_DATA_CHANGE_EVENT = "MobileDataStatus";
  private boolean toRetryForceCellularOnUnavailable = true;
  private boolean isNetworkCallbackAlreadyActive = false;

  private BroadcastReceiver airplaneModeReceiver = new AirplaneModeReceiver();
  private boolean airplaneModeListenerAlreadyRegistered = false;
  private static final String AIRPLANE_MODE_CHANGE_EVENT = "EventAirplaneChange";


  public CellularConnectivityManagerModule(ReactApplicationContext reactContext) {
    super(reactContext);

    reactContext.addLifecycleEventListener(this);

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
  public void registerAirplaneModeListener() {
    if(!airplaneModeListenerAlreadyRegistered) {
      airplaneModeListenerAlreadyRegistered = true;
     sendInitialAirplaneModeStatus();
      appContext.registerReceiver(airplaneModeReceiver, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
    }
  }

  @ReactMethod
  public void unregisterAirplaneModeListener() {
    try{
      airplaneModeListenerAlreadyRegistered = false;
      appContext.unregisterReceiver(airplaneModeReceiver);
    } catch (Exception e) {
      Log.d("exception", e.toString());
    }
  }

  @ReactMethod
  public void addListener(String eventName) {}

  @ReactMethod
  public void removeListeners(Integer count) {}

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
      mConnectivityManager.requestNetwork(request, mNetworkCallback, NETWORK_CALLBACK_TIMEOUT);
    } else {
      mConnectivityManager.requestNetwork(request, mNetworkCallback);
    }
  }

  private void sendInitialAirplaneModeStatus() {
    String val = Settings.Global.getString(appContext.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON);
    sendEvent(AIRPLANE_MODE_CHANGE_EVENT, val.equals("1"));
  }

  private void sendEvent(String eventName, boolean currentValue) {
    appContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, currentValue);
  }

  @Override
  public void onHostResume() {}

  @Override
  public void onHostPause() {}
  
  @Override
  public void onHostDestroy() {
    resetDataFlowToDefault();
    unregisterAirplaneModeListener();
  }

  private class ConnectivityNetworkCallback extends ConnectivityManager.NetworkCallback {
    @Override
    public void onAvailable(Network network) {
      super.onAvailable(network);
      sendEvent(MOBILE_DATA_CHANGE_EVENT, true);
      mConnectivityManager.bindProcessToNetwork(network);
    }

    @Override
    public void onLost(Network network) {
      super.onLost(network);
      sendEvent(MOBILE_DATA_CHANGE_EVENT, false);
    }

    @Override
    public void onUnavailable() {
      super.onUnavailable();

      isNetworkCallbackAlreadyActive = false;
      sendEvent(MOBILE_DATA_CHANGE_EVENT, false);

      if(toRetryForceCellularOnUnavailable) {
        toRetryForceCellularOnUnavailable = false;
        forceCellularNetwork();
      }
    }
  }

  private class AirplaneModeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      try {
        String val = Settings.Global.getString(appContext.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON);
        sendEvent(AIRPLANE_MODE_CHANGE_EVENT, val.equals("1"));
      } catch (Exception e) {}
    }
  }
}
