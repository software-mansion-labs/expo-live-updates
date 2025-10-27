# expo-live-updates

Expo module that enables Android Live Updates functionality, allowing you to display real-time, ongoing notifications with progress tracking and dynamic content updates directly from your React Native app or Firebase Cloud.

> ⚠️ **WARNING**  
> This library is in early development stage; breaking changes can be introduced in minor version upgrades.

## Features

- **Live Notifications**: Display persistent, ongoing notifications that stay visible until dismissed
- **Progress Tracking**: Show determinate or indeterminate progress bars in notifications
- **Firebase Cloud Messaging integration**: Manage Live Updates remotely via FCM push notifications
- **Deep Linking**: Navigate to specific app screens when users tap notifications
- **Event Listeners**: Track notification state changes (started, updated, stopped, dismissed, clicked)

## Platform Compatibility

**Android Only**: This library is currently available exclusively for Android. Live Updates functionality is supported starting from **Android Baklava Preview (Android 16.0)** SDK. Note that the standard `Android 16.0 ("Baklava")` SDK won't support Live Updates; you must use the **Baklava Preview** SDK. If Live Updates are not available on the device's SDK version, standard notifications will be displayed instead.

**Looking for iOS?** If you need similar functionality for iOS, check out [expo-live-activity](https://github.com/software-mansion-labs/expo-live-activity) which provides Live Activities support for iOS 16.2+.

## How to run example app

1. Prepare Android emulator with `Android Baklava Preview` SDK.
2. Run `npm i` in root & `/example` directories.
3. Run `npm run android` (or `npx expo run:android --device` to select proper emulator) in `example/` directory.

## Installation

### 1. Install the module

You can install this package from the repository:

```sh
npm install git+https://github.com/software-mansion-labs/expo-live-updates.git
```

Or if you have access to this repository locally:

```sh
npm install /path/to/expo-live-updates
```

### 2. Configure the plugin

Use the `expo-live-updates` plugin in your app config:

```ts
plugins: [
  [
    'expo-live-updates',
    {
      channelId: 'NotificationsChannelId',
      channelName: 'Notifications Channel Name',
    },
  ],
  // ... other plugins
]
```

### 3. Handle permissions

Expo-live-updates require 2 Android permissions to work. Add them to `android.permissions` in app config and remember to request for them in React Native app.

```ts
permissions: [
  'android.permission.POST_NOTIFICATIONS',
  'android.permission.POST_PROMOTED_NOTIFICATIONS',
],
```

### 4. Prebuild your app

Then prebuild your app with:

```sh
npx expo prebuild --clean
```

Now you can test Live Updates:

```ts
startLiveUpdate({ title: 'Test notifications' })
```

## API

### Managing Live Updates

- `startLiveUpdate(state: LiveUpdateState, config?: LiveUpdateConfig): number | undefined` Creates and displays a new Live Update notification. Returns notification ID or undefined if failed.
- `updateLiveUpdate(notificationId: number, state: LiveUpdateState, config?: LiveUpdateConfig): void` Updates an existing Live Update notification.

- `stopLiveUpdate(notificationId: number): void` Stops an existing Live Update notification.

### Handling Push Notification Tokens

- `addTokenChangeListener(listener: (event: TokenChangeEvent) => void): EventSubscription | undefined` Subscribes to FCM token changes. Returns current token (if it already exists) on start listening. Call `.remove()` to unsubscribe.

### Handling Notification Events Listener

- `addNotificationStateChangeListener(listener: (event: NotificationStateChangeEvent) => void): EventSubscription | undefined` Subscribes to notification state changes (started, updated, stopped, dismissed, clicked). Call .remove() to unsubscribe

### LiveUpdateState Object Structure

Defines the visual content and progress information for a Live Update notification:

```ts
type LiveUpdateState = {
  title: string // Main title text
  text?: string // Additional descriptive text
  subText?: string // Additional short text
  imageName?: string // Name of image resource
  dynamicIslandImageName?: string // Custom image for dynamic notification area
  progress?: LiveUpdateProgress // Progress bar configuration
  shortCriticalText?: string // Critical text (max 7 chars recommended)
}

type LiveUpdateProgress = {
  max?: number // Maximum progress value (default: 100)
  progress?: number // Current progress value
  indeterminate?: boolean // Whether to show indeterminate progress bar
}
```

### LiveUpdateConfig Object Structure

Configuration options for the Live Update notification. Separated from state to allow in the future updating only state without passing config every time:

```ts
type LiveUpdateConfig = {
  backgroundColor?: string // Background color (only SDK < 16)
  deepLinkUrl?: string // Deep link URL to navigate when tapped
}
```

## Deep Linking

The `LiveUpdateConfig` supports a `deepLinkUrl` property that allows you to specify an in-app route to navigate to when the notification is clicked. If no `deepLinkUrl` is provided, the default behavior is to open the app.

#### Setup

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
}

return <Navigation linking={linking} />
```

## Firebase Cloud Messaging integration

1. Create project at [Firebase](https://firebase.google.com/).
2. Add android app to created project and download `google-services.json`. To work with example app set package name to `expo.modules.liveupdates.example` and skip other steps of Firebase instructions.
3. Place `google-services.json` in `/example` app or your app folder.
4. Set `android.googleServicesFile` in app config to the path of `google-services.json` file (like in `example/app.config.ts`). This will inform module to init Firebase service.
5. Prebuild app with `npx expo prebuild --clean`

### Send Firebase Message

Live Updates can be started, updated and stopped using FCM. To manage Live Update via FCM you need to send data message:

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
          "text":"This is a message sent via Firebase", // optional
          "subText":"Firebase", // optional: subText value
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

- `<YOUR_PROJECT_ID>` - can be found in `google-services.json`
- testing `<YOUR_BEARER_TOKEN>` - can be generated using [Google OAuth Playground](https://developers.google.com/oauthplayground/)
- `<DEVICE_PUSH_TOKEN>` - can be copied from the example app

There are some restrictions that should be followed while managing Live Updates via Firebase Cloud Messaging. Keep in mind that passing:

- `notificationId` with event `'start'` is prohibited and will result in error. Notification id is generated on Live Update start and cannot be customized.
- `shortCriticalText` of length longer than 7 characters is not recommended. There is no guarantee how much text will be displayed if this limit is exceeded, based on [Android documentation](<https://developer.android.com/reference/android/app/Notification.Builder#setShortCriticalText(java.lang.String)>).
- `progressIndeterminate` as `true`, the notification will show an indeterminate progress bar. When `false`, it will show a determinate progress bar with the current progress relative to the maximum value. All progress fields are optional. At least `progressIndeterminate: true` or `progressValue` must be included for the progress to be displayed.
- `subText` provides information displayed in the notification, but there are no guarantees where exactly it will be located. Usually it is placed in notification header.

## expo-live-updates is created by Software Mansion

[![swm](https://logo.swmansion.com/logo?color=white&variant=desktop&width=150&tag=typegpu-github 'Software Mansion')](https://swmansion.com)

Since 2012 [Software Mansion](https://swmansion.com) is a software agency with
experience in building web and mobile apps. We are Core React Native
Contributors and experts in dealing with all kinds of React Native issues. We
can help you build your next dream product –
[Hire us](https://swmansion.com/contact/projects?utm_source=typegpu&utm_medium=readme).

<!-- automd:contributors author="software-mansion" -->

Made by [@software-mansion](https://github.com/software-mansion) and
[community](https://github.com/software-mansion-labs/expo-live-updates/graphs/contributors) 💛
<br><br>
<a href="https://github.com/software-mansion-labs/expo-live-updates/graphs/contributors">
<img src="https://contrib.rocks/image?repo=software-mansion-labs/expo-live-updates" />
</a>

<!-- /automd -->
