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
2. Add android app to created project. To work with example app set package name to `com.test.test` and download `google-service.json`. Skip other steps of Firebase instructions.
3. Place `google-service.json` in `/example` folder.

# Send Firebase Message

Live updates can be started, updated and stopped using FCM. To manage live update via FCM you need to send data message:

```
POST /v1/projects/<YOUR_PROJECT_ID>/messages:send
Host: fcm.googleapis.com
Authorization: Bearer <YOUR_BEARER_TOKEN>
{
  "message":{
      "token":"<DEVICE_PUSH_TOKEN>",
      "data":{
          "event":"update",
          "notificationId":"1", // shouldn't be passed when event is set to 'start'
          "title":"Firebase message",
          "subtitle":"This is a message sent via Firebase", // optional
          "progressMax":"100", // optional: maximum progress value, if no provided = 100
          "progressValue":"50", // optional: current progress value
          "progressIndeterminate":"false", // optional: whether progress is indeterminate
          "backgroundColor":"red", // optional, works only on SDK < Baklava
          "shortCriticalText":"text" // optional: shouldn't be longer than 7 characters
          "deepLinkUrl":"/Test" // optional: default it will just open the app
      }
   }
}
```

Request variables:

- `<YOUR_PROJECT_ID>` - can be found in `google-service.json`
- testing `<YOUR_BEARER_TOKEN>` - can be generated using [Google OAuth Playground](https://developers.google.com/oauthplayground/)
- `<DEVICE_PUSH_TOKEN>` - can be copied from the example app

There are some restrictions that should be followed while managing Live Updates via Firebase Cloud Messaging. Keep in mind that passing:

- `notificationId` with event `'start'` is prohibited and will result in error. Notification id is generated on Live Update start and cannot be customized.
- `shortCriticalText` of length longer than 7 characters is not recommended. There is no guarantee how much text will be displayed if this limit is exceeded, based on [Android documentation](<https://developer.android.com/reference/android/app/Notification.Builder#setShortCriticalText(java.lang.String)>).
- `progressIndeterminate` as `true`, the notification will show an indeterminate progress bar. When `false`, it will show a determinate progress bar with the current progress relative to the maximum value. All progress fields are optional. At least `progressIndeterminate: true` or `progressValue` must be included for the progress to be displayed.

# Notification state updates

`ExpoLiveUpdatesModule.addNotificationStateChangeListener` API allows you to subscribe to changes in notification state. This is useful, for example, when you want to react to a user interacting with a notification or when a notification is updated or dismissed.

The handler will receive a `NotificationStateChangeEvent` object, which contains:

- `notificationId` – the ID of the notification.
- `action` – the type of change, which can be `'started'`, `'updated'`, `'stopped'`, `'dismissed'` or `'clicked'`.
- `timestamp` – the time when the change occurred, in milliseconds.

Example usage in a React component:

```ts
useEffect(() => {
  const subscription = ExpoLiveUpdatesModule.addNotificationStateChangeListener(
    (event: NotificationStateChangeEvent) => {
      console.log(
        `Notification ${event.notificationId} was ${event.action} at ${event.timestamp}`,
      )
    },
  )

  return () => {
    subscription?.remove()
  }
}, [])
```

# Deep Linking

The `LiveUpdateConfig` supports a `deepLinkUrl` property that allows you to specify an in-app route to navigate to when the notification is clicked. If no `deepLinkUrl` is provided, the default behavior is to open the app.

## Setup

1. Define a scheme in your `app.config.ts`:

```ts
export default {
  scheme: 'myapp', // Your custom scheme
  // ... other config
}
```

2. Use the `withAppScheme` plugin in your config:

```ts
plugins: [
  '../plugin/withAppScheme',
  // ... other plugins
]
```

3. Handle deep links, f.e. with [React Navigation](https://reactnavigation.org/docs/deep-linking/?config=static#setup-with-expo-projects):

```ts
  const linking = {
    prefixes: [prefix],
  };

  return <Navigation linking={linking} />;
```

# TODO

- Make short critical text customizable
- Handle notification ID after live update start triggered by FCM
- Save config passed to `startLiveUpdate` by id to apply it when updating notification until `stopLiveUpdate` invoked
- Delete `CHANNEL_ID` and `CHANNEL_NAME` - make notification channel id and name configurable, use `channelId` and `channelName` props
- Handle deepLinks by FCM
- Handle progress bar
- Support more Live Updates features

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
