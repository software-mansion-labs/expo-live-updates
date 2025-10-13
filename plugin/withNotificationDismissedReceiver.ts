import type { ExpoConfig } from 'expo/config'
import {
  AndroidConfig,
  ConfigPlugin,
  withAndroidManifest,
} from 'expo/config-plugins'

const RECEIVER_NAME = 'expo.modules.liveupdates.NotificationDismissedReceiver'

const ensureReceiver = (
  androidManifest: AndroidConfig.Manifest.AndroidManifest,
) => {
  const mainApplication =
    AndroidConfig.Manifest.getMainApplicationOrThrow(androidManifest)

  const existingReceivers = (mainApplication.receiver ??= [])

  const existingReceiverIndex = existingReceivers.findIndex(
    (rcv: any) => rcv?.$?.['android:name'] === RECEIVER_NAME,
  )

  const baseReceiver = {
    $: {
      'android:name': RECEIVER_NAME,
      'android:exported': 'false',
      'android:enabled': 'true',
    },
  } as any

  if (existingReceiverIndex >= 0) {
    existingReceivers[existingReceiverIndex] = baseReceiver
  } else {
    existingReceivers.push(baseReceiver)
  }
}

const withNotificationDismissedReceiver: ConfigPlugin = (
  config: ExpoConfig,
) => {
  return withAndroidManifest(config, configWithManifest => {
    ensureReceiver(configWithManifest.modResults)
    return configWithManifest
  })
}

export default withNotificationDismissedReceiver
