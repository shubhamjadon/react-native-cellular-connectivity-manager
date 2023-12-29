import React, { useEffect } from 'react';

import {
  StyleSheet,
  View,
  Text,
  Pressable,
  ToastAndroid,
  Alert,
} from 'react-native';
import {
  AIRPLANE_MODE_EVENT,
  MOBILE_DATA_STATUS_EVENT,
  MobileDataStatus,
  registerAirplaneModeListener,
  switchToCellularInternet,
  switchToDefaultInternet,
  unregisterAirplaneModeListener,
} from 'react-native-cellular-connectivity-manager';

export default function App() {
  useEffect(() => {
    const mobileDataListener = MobileDataStatus.addListener(
      MOBILE_DATA_STATUS_EVENT,
      (event: boolean) => {
        console.log('Is mobile data on:', event);
        ToastAndroid.show(`Mobile Data is ${event ? 'ON' : 'OFF'}`, 1000);
      }
    );

    const airplaneModeListener = MobileDataStatus.addListener(
      AIRPLANE_MODE_EVENT,
      (event: boolean) => {
        console.log('Is airplane mode on:', event);
      }
    );

    return () => {
      mobileDataListener.remove();
      airplaneModeListener.remove();
    };
  }, []);

  const callApi1 = () => {
    console.log('Api called');
    fetch('https://jsonplaceholder.typicode.com/todos/1')
      .then((response) => response.json())
      .then((json) => {
        console.log('api result1: ', json);
        ToastAndroid.show('RESPNOSE1', 1000);
      })
      .catch((err) => {
        console.log(err);
        ToastAndroid.show('ERROR1', 1000);
      });
  };
  const callApi2 = () => {
    console.log('Api called');
    fetch('https://jsonplaceholder.typicode.com/todos/2')
      .then((response) => response.json())
      .then((json) => {
        console.log('api result2: ', json);
        ToastAndroid.show('RESPNOSE2', 1000);
      })
      .catch((err) => {
        console.log(err);
        ToastAndroid.show('ERROR2', 1000);
      });
  };

  return (
    <View style={styles.container}>
      <View style={styles.row}>
        <Pressable
          onPress={switchToCellularInternet}
          style={({ pressed }) => [
            styles.button,
            { opacity: pressed ? 0.5 : 1 },
          ]}
        >
          <Text style={styles.buttonText}>Switch To Cellular</Text>
        </Pressable>
        <Pressable
          onPress={switchToDefaultInternet}
          style={({ pressed }) => [
            styles.button,
            styles.blackButton,
            { opacity: pressed ? 0.5 : 1 },
          ]}
        >
          <Text style={styles.buttonText}>Switch To Default</Text>
        </Pressable>
      </View>
      <View style={styles.row}>
        <Pressable
          onPress={registerAirplaneModeListener}
          style={({ pressed }) => [
            styles.button,
            { opacity: pressed ? 0.5 : 1 },
          ]}
        >
          <Text style={styles.buttonText}>RegisterAirplaneListener</Text>
        </Pressable>
        <Pressable
          onPress={unregisterAirplaneModeListener}
          style={({ pressed }) => [
            styles.button,
            styles.blackButton,
            { opacity: pressed ? 0.5 : 1 },
          ]}
        >
          <Text style={styles.buttonText}>UnregisterAirplaneListener</Text>
        </Pressable>
      </View>
      <View style={styles.row}>
        <Pressable
          onPress={callApi1}
          style={({ pressed }) => [
            styles.button,
            { opacity: pressed ? 0.5 : 1 },
          ]}
        >
          <Text style={styles.buttonText}>Call api 1</Text>
        </Pressable>
        <Pressable
          onPress={callApi2}
          style={({ pressed }) => [
            styles.button,
            styles.blackButton,
            { opacity: pressed ? 0.5 : 1 },
          ]}
        >
          <Text style={styles.buttonText}>Call api 2</Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    gap: 16,
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-evenly',
  },
  button: {
    padding: 16,
    borderRadius: 8,
    backgroundColor: 'green',
  },
  blackButton: {
    backgroundColor: '#000',
  },
  buttonText: {
    color: '#fff',
  },
});
