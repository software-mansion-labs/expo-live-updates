# expo-live-updates

Library based on expo modules for Android Live Updates

# How to run example

To run example app:
1. Prepare Android emulator with `Android Baklava Preview` SDK. Just `Android 16.0 ("Baklava")` won't allow to run Live Updates.
2. `npm i`
3. Go to `example/` directory and run `npm i` & `npm run android`.
4. Run `npm run android` (or `npx expo run:android --device` to select proper emulator) in `example/` directory again.

# API documentation

- [Documentation for the latest stable release](https://docs.expo.dev/versions/latest/sdk/live-updates/)
- [Documentation for the main branch](https://docs.expo.dev/versions/unversioned/sdk/live-updates/)

# Installation in managed Expo projects

For [managed](https://docs.expo.dev/archive/managed-vs-bare/) Expo projects, please follow the installation instructions in the [API documentation for the latest stable release](#api-documentation). If you follow the link and there is no documentation available then this library is not yet usable within managed projects &mdash; it is likely to be included in an upcoming Expo SDK release.

# Installation in bare React Native projects

For bare React Native projects, you must ensure that you have [installed and configured the `expo` package](https://docs.expo.dev/bare/installing-expo-modules/) before continuing.

### Add the package to your npm dependencies

```
npm install expo-live-updates
```

### Configure for Android

### Configure for iOS

Run `npx pod-install` after installing the npm package.

# Contributing

Contributions are very welcome! Please refer to guidelines described in the [contributing guide](https://github.com/expo/expo#contributing).
