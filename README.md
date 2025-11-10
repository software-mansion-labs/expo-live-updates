
![expo-live-updates by Software Mansion](https://github.com/user-attachments/assets/46edb342-4577-4fcd-8ccf-c87759346a45)

> ⚠️ **WARNING**  
> This library is in early development stage; breaking changes can be introduced in minor version upgrades.

# expo-live-updates

Expo module that enables Android Live Updates functionality, allowing you to display real-time, ongoing notifications with progress tracking and dynamic content updates directly from your React Native app or Firebase Cloud.

## Features

- **Live Notifications**: Display persistent, ongoing notifications that stay visible until dismissed
- **Progress Tracking**: Show determinate or indeterminate progress bars in notifications
- **Firebase Cloud Messaging integration**: Manage Live Updates remotely via FCM push notifications
- **Deep Linking**: Navigate to specific app screens when users tap notifications
- **Event Listeners**: Track notification state changes (started, updated, stopped, dismissed, clicked)

## Platform Compatibility

**Android Only**: This library is currently available exclusively for Android. Live Updates functionality is supported starting from **API 36.1**. Note that the standard API 36.0 won't support Live Updates; you must use **API 36.1**. If Live Updates are not available, standard notifications will be displayed instead.

**Looking for iOS?** If you need similar functionality for iOS, check out [expo-live-activity](https://github.com/software-mansion-labs/expo-live-activity) which provides Live Activities support for iOS 16.2+.

## How to run example app

1. Prepare Android emulator with `API 36.1`.
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

expo-live-updates require 2 Android permissions to work. Add them to `android.permissions` in app config and remember to request for them in React Native app.

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

- `startLiveUpdate(state: LiveUpdateState, config?: LiveUpdateConfig): number | undefined`: Creates and displays a new Live Update notification. Returns id of the created notification or undefined if creation failed.
- `updateLiveUpdate(notificationId: number, state: LiveUpdateState, config?: LiveUpdateConfig): void`: Updates an existing Live Update notification.

- `stopLiveUpdate(notificationId: number): void`: Stops an existing Live Update notification.
### Handling Push Notification Tokens

- `addTokenChangeListener(listener: (event: TokenChangeEvent) => void): EventSubscription | undefined`: Subscribes to FCM token changes. Returns current token (if it already exists) on start listening. Call `.remove()` to unsubscribe.

### Handling Notification Events Listener

- `addNotificationStateChangeListener(listener: (event: NotificationStateChangeEvent) => void): EventSubscription | undefined`: Subscribes to notification state changes (started, updated, stopped, dismissed, clicked). Call .remove() to unsubscribe

### LiveUpdateState Object Structure

Defines the visual content and progress information for a Live Update notification:

```ts
type LiveUpdateState = {
  title: string
  text?: string
  subText?: string
  image?: LiveUpdateImage // { url: string, isRemote: boolean}
  icon?: LiveUpdateImage // { url: string, isRemote: boolean}
  progress?: LiveUpdateProgress
  shortCriticalText?: string
  showTime?: boolean
  time?: number
}
```

Important notes:

- Adding `icon` results in changing icon inside notification and device status bar. However, on API 36.1 only the icon inside status bar will be changed - icon inside notification is your app's icon and cannot be changed with `icon` property.
- `shortCriticalText` is recommended to be not longer than 7 characters. If this limit is exceeded, there is no guarantee how much text will be displayed, based on [Android documentation](<https://developer.android.com/reference/android/app/Notification.Builder#setShortCriticalText(java.lang.String)>).
- `subText` is displayed in the notification header area since Android Nougat version, but there is no guarantee where exactly it will be located, based on [Android documentation](<https://developer.android.com/reference/android/app/Notification.Builder#setSubText(java.lang.CharSequence)>).
- `showTime` indicates wether notification time should be displayed. By default, time is always visible - if you want to hide it, you need to directly set `showTime` to `false`.
- `time` should be a timestamp based on which time inside notification will be displayed. Time will be visible inside status chip as well, but only when the given timestamp is at least 2 minutes in the future and `shortCriticalText` is not defined.
- Notification time is based on timestamp provided with `time` property. If `time` is undefined, the time of notification's creation (on the native side) is displayed - keep in mind that for Live Updates created with FCM it will be the time of Firebase message delivery.

### LiveUpdateProgress Object Structure

Defines progress representation for a Live Update notification:

```ts
type LiveUpdateProgress = {
  max?: number
  progress?: number // default: 100
  indeterminate?: boolean
  points?: LiveUpdateProgressPoint[] // { position: number, color?: string }
  segments?: LiveUpdateProgressSegment[] // { position: number, color?: string }
}
```

Important notes:

- Progress will not be displayed unless `progressIndeterminate` is set to `true` or `progress` is passed.
- Progress maximum value is specified with `max`. However, if `segments` property is defined, maximum value will be calculated based on provided `segments` and the `max` property will be ignored.

### LiveUpdateConfig Object Structure

Configuration options for the Live Update notification. Separated from state to allow in the future updating only state without passing config every time:

```ts
type LiveUpdateConfig = {
  deepLinkUrl?: string
  iconBackgroundColor?: string
}
```

Important notes:

- On API 36.1 adding `iconBackgroundColor` will have no effect, because icon inside notification is your app's icon and it's background cannot be changed with `iconBackgroundColor` property.

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

## Example Usage


Managing a Live Update:

```typescript
const state: LiveUpdateState = {
  title: "This is a title",
  text: "This is a text",
  progress: {
    progress: 70,
    segments: [
      { length: 50, color: "red" },
      { length: 100, color: "blue" },
    ],
    points: [
      { position: 10, color: "red" },
      { position: 50, color: "blue" },
    ],
  },
};

const config: LiveUpdateConfig = {
  deepLinkUrl: "/dashboard",
};

const notificationId = LiveUpdates.startLiveUpdate(state, config);
// Store notificationId for future reference
```

This will initiate a Live Update with the specified title, text and progress with points and segment.

Subscribing to notifications state changes:

```typescript
useEffect(() => {
  const notificationStateChangeSubscription =
    LiveUpdates.addNotificationStateChangeListener(
      ({ notificationId, action, timestamp }) => {
        // Handle notification state change
      }
    );

  return () => {
    notificationStateChangeSubscription?.remove();
  };
}, []);
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
          "subText":"Firebase", // optional
          "imageUrl":"", // optional
          "iconUrl":"", // optional
          "progressMax":"100", // optional: if not provided = 100
          "progressValue":"50", // optional
          "progressIndeterminate":"false", // optional
          "progressPoints":"[{\"position\":10,\"color\":\"red\"},{\"position\":50,\"color\":\"blue\"}]", // optional: should be a string with JSON
          "progressSegments":"[{\"length\":50,\"color\":\"red\"},{\"length\":100,\"color\":\"blue\"}]", // optional: should be a string with JSON
          "iconBackgroundColor":"red", // optional
          "shortCriticalText":"text", // optional: shouldn't be longer than 7 characters
          "deepLinkUrl":"/Test", // optional: default it will just open the app
          "showTime":"true", // optional: if not provided = true
          "time":"1761313668279" // optional: should be timestamp
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
- `progressPoints` must be a string with specific format. Convert your points of type `LiveUpdateProgressPoint[]` to JSON and pass it as string to `progressPoints`.
- `progressSegments` must be a string with specific format. Convert your segments of type `LiveUpdateProgressSegment[]` to JSON and pass it string to `progressSegments`.

### Push token listener

Subscribing to push token changes:

```typescript
useEffect(() => {
  const tokenChangeSubscription = LiveUpdates.addTokenChangeListener(
    ({ token }) => {
      // Handle push token change
    }
  );

  return () => {
    tokenChangeSubscription?.remove();
  };
}, []);
```

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
