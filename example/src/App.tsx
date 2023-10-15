import React, { useEffect } from 'react';

import { StyleSheet, View, Text, Pressable, ToastAndroid } from 'react-native';
import {
  MOBILE_DATA_STATUS_EVENT,
  MobileDataStatus,
  switchToCellularInternet,
  switchToDefaultInternet,
} from 'react-native-cellular-connectivity-manager';

export default function App() {
  useEffect(() => {
    const listener = MobileDataStatus.addListener(
      MOBILE_DATA_STATUS_EVENT,
      (event: boolean) => {
        console.log('Is mobile data on:', event);
        ToastAndroid.show(`Mobile Data is ${event ? 'ON' : 'OFF'}`, 1000);
      }
    );

    () => listener.remove();
  }, []);

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
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
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
