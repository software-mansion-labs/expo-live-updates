# expo-live-updates

Library based on expo modules for Android Live Updates

# How to run example

To run example app:

1. Prepare Android emulator with `Android Baklava Preview` SDK. Just `Android 16.0 ("Baklava")` won't allow to run Live Updates.
2. `npm i`
3. Go to `example/` directory and run `npm i` & `npm run android`.
4. Run `npm run android` (or `npx expo run:android --device` to select proper emulator) in `example/` directory again.

# How to add Firebase Cloud Messaging

1. Create project at [Firebase](https://firebase.google.com/).
2. Add android app to created project. Set package name to `com.com.test` and download `google-service.json`. Skip other steps of Firebase instructions.
3. Place `google-service.json` in `/example` folder.

# Send Firebase Message

To create/update live update via FCM you need to send data message:

```
POST /v1/projects/<YOUR_PROJECT_ID>/messages:send HTTP/1.1
Host: fcm.googleapis.com
Content-Type: application/json
Authorization: Bearer <YOUR_BEARER_TOKEN>
Content-Length: 399
{
   "message":{
      "token":"<DEVICE_PUSH_TOKEN>",
      "data":{
        "notificationId":"1",
        "title":"Firebase message",
        "body":"This is a message sent via Firebase",
        "progress":"20",
        "progressPointOne":"40",
        "progressPointTwo":"80"
      }
   }
}
```

Request variables:

- `<YOUR_PROJECT_ID>` - can be found in `google-service.json`
- testing `<YOUR_BEARER_TOKEN>` - can be generated using [Google OAuth Playground](https://developers.google.com/oauthplayground/)
- `<DEVICE_PUSH_TOKEN>` - can be copied from the example app

# Notification state updates

`ExpoLiveUpdatesModule.addNotificationStateChangeListener` API allows you to subscribe to changes in notification state. This is useful, for example, when you want to react to a user interacting with a notification or when a notification is updated or dismissed.

The handler will receive a `NotificationStateChangeEvent` object, which contains:

- `notificationId` – the ID of the notification.
- `action` – the type of change, which can be `'dismissed'`, or `'updated'`.
- `timestamp` – the time when the change occurred, in milliseconds.

Example usage in a React component:

```ts
useEffect(() => {
   const subscription = ExpoLiveUpdatesModule.addNotificationStateChangeListener(
      (event: NotificationStateChangeEvent) => {
         console.log(`Notification ${event.notificationId} was ${event.action} at ${event.timestamp}`);
      }
   );

   return () => {
      subscription?.remove();
   };
}, []);
```

# TODO
- Handle click with deeplink functionality
- Handle progress bar
- Make short critical text customizable
- Handle push token change
- Delete live update using FCM
- Support missing fields of live update
- Support multiple live updates at once
- Handle notification ID after live update start triggered by FCM
- Save config passed to `startLiveUpdate` by id to apply it when updating notification until `stopLiveUpdate` invoked
- Delete `CHANNEL_ID` and `CHANNEL_NAME` - make notification channel id and name configurable, use `channelId` and `channelName` props

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
