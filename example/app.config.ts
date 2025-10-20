import type { ConfigContext, ExpoConfig } from '@expo/config'
import 'tsx/cjs'

export default ({ config }: ConfigContext): ExpoConfig => ({
  ...config,
  name: 'expo-live-updates-example',
  slug: 'expo-live-updates-example',
  version: '1.0.0',
  orientation: 'portrait',
  icon: './assets/icon.png',
  userInterfaceStyle: 'light',
  newArchEnabled: true,
  splash: {
    image: './assets/splash-icon.png',
    resizeMode: 'contain',
    backgroundColor: '#ffffff',
  },
  ios: {
    supportsTablet: true,
    bundleIdentifier: 'expo.modules.liveupdates.example',
  },
  scheme: 'expo-live-updates-example',
  android: {
    adaptiveIcon: {
      foregroundImage: './assets/adaptive-icon.png',
      backgroundColor: '#ffffff',
    },
    edgeToEdgeEnabled: true,
    package: 'expo.modules.liveupdates.example',
    permissions: [
      'android.permission.POST_NOTIFICATIONS',
      'android.permission.POST_PROMOTED_NOTIFICATIONS',
    ],
    googleServicesFile: './google-services.json',
  },
  web: {
    favicon: './assets/favicon.png',
  },
  plugins: [
    'expo-asset',
    [
      '../app.plugin.js',
      {
        channelId: 'LiveUpdatesServiceChannelId',
        channelName: 'Live Updates Service Channel Name',
      },
    ],
  ],
})
