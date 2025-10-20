# expo-live-updates

Library based on expo modules for Android Live Updates

# How to run example

To run example app:

1. Prepare Android emulator with `Android Baklava Preview` SDK. Just `Android 16.0 ("Baklava")` won't allow to run Live Updates.
2. `npm i`
3. Go to `example/` directory and run `npm i` & `npm run android`.
4. Run `npm run android` (or `npx expo run:android --device` to select proper emulator) in `example/` directory again.

# Installation

## 1. Install the module

You can install this package from the repository:

```sh
npm install git+https://github.com/software-mansion-labs/expo-live-updates.git
```

Or if you have access to this repository locally:

```sh
npm install /path/to/expo-live-updates
```

## 2. Configure the plugin

Use the `expo-live-updates` plugin in your app config:

```ts
plugins: [
  "expo-live-updates",
  {
    "channelId": "NotificationsChannelId", 
    "channelName": "Notifications Channel Name"
  }
  // ... other plugins
]
```

## 3. Handle permissions

Expo-live-updates require 2 Android permissions to work. Add them to `android.permissions` in app config and remember to request for them in React Native app.

```ts
permissions: [
  'android.permission.POST_NOTIFICATIONS',
  'android.permission.POST_PROMOTED_NOTIFICATIONS',
],
```

## 4. Prebuild your app

Then prebuild your app with:

```sh
npx expo prebuild --clean
```

Now you can test Live Updates:

```ts
startLiveUpdate({title: "Test notifications"})
```


# How to add Firebase Cloud Messaging

1. Create project at [Firebase](https://firebase.google.com/).
2. Add android app to created project and download `google-service.json`. To work with example app set package name to `expo.modules.liveupdates.example` and skip other steps of Firebase instructions.
3. Place `google-service.json` in `/example` app or your app folder.
4. Set `android.googleServicesFile` in app config to the path of `google-services.json` file (like in `example/app.config.ts`). This will inform module to init Firebase service.
5. Prebuild app with `npx expo prebuild --clean`

# Send Firebase Message

Live updates can be started, updated and stopped using FCM. To manage live update via FCM you need to send data message:

```
POST /v1/projects/<YOUR_PROJECT_ID>/messages:send HTTP/1.1
Host: fcm.googleapis.com
Content-Type: application/json
Authorization: Bearer <YOUR_BEARER_TOKEN>
Content-Length: 481
{
  "message":{
      "token":"<DEVICE_PUSH_TOKEN>",
      "data":{
          "event":"update",
          "notificationId":"1", // shouldn't be passed when event is set to 'start'
          "title":"Firebase message",
          "subtitle":"This is a message sent via Firebase",
          "backgroundColor":"red",
          "shortCriticalText":"text" // shouldn't be longer than 7 characters
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

2. Handle deep links in React Native, f.e. with [React Navigation](https://reactnavigation.org/docs/deep-linking/?config=static#setup-with-expo-projects):

```ts
  const linking = {
    prefixes: [prefix],
  };

  return <Navigation linking={linking} />;
```

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
