# react-native-cellular-connectivity-manager

This is used to get Status of Mobile Data irrespective of Wifi is on or not and allows functionality to switch data flow through Celluar when both Wifi and Cellular are on in Android.

## Installation

```sh
npm install react-native-cellular-connectivity-manager
```

## Android

In AndroidManifest.xml add following permission:

```
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
```

In build.gradle of app make sure to have minSdkVersion 26 or above

## Usage

For usage see <i>example/App.tsx</i>

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
