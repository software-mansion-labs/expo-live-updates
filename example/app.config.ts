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
  android: {
    adaptiveIcon: {
      foregroundImage: './assets/adaptive-icon.png',
      backgroundColor: '#ffffff',
    },
    edgeToEdgeEnabled: true,
    package: 'expo.modules.liveupdates.example',
    permissions: [
      'android.permission.ACCESS_COARSE_LOCATION',
      'android.permission.ACCESS_FINE_LOCATION',
      'android.permission.ACCESS_WIFI_STATE',
      'android.permission.CHANGE_NETWORK_STATE',
      'android.permission.CHANGE_WIFI_STATE',
      'android.permission.FOREGROUND_SERVICE_LOCATION',
      'android.permission.POST_NOTIFICATIONS',
      'android.permission.FOREGROUND_SERVICE',
    ],
  },
  web: {
    favicon: './assets/favicon.png',
  },
  plugins: ['./plugins/withForegroundService.ts'],
})
