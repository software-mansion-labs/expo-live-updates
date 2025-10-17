import type { ExpoConfig } from 'expo/config'
import {
  AndroidConfig,
  ConfigPlugin,
  withAndroidManifest,
} from 'expo/config-plugins'

export interface LiveUpdatesPluginProps {
  channelId: string
  channelName: string
  scheme?: string
}

const DEFAULT_SCHEME = 'myapp'
const EXPO_MODULE_SCHEME_KEY = 'expo.modules.scheme'
const CHANNEL_ID_KEY = 'expo.modules.liveupdates.channelId'
const CHANNEL_NAME_KEY = 'expo.modules.liveupdates.channelName'
const SERVICE_NAME = 'expo.modules.liveupdates.FirebaseService'
const RECEIVER_NAME = 'expo.modules.liveupdates.NotificationDismissedReceiver'

const ensureService = (
  androidManifest: AndroidConfig.Manifest.AndroidManifest,
) => {
  const mainApplication =
    AndroidConfig.Manifest.getMainApplicationOrThrow(androidManifest)

  const existingServices = (mainApplication.service ??= [])

  const existingServiceIndex = existingServices.findIndex(
    (svc: any) => svc?.$?.['android:name'] === SERVICE_NAME,
  )

  const baseService = {
    '$': {
      'android:name': SERVICE_NAME,
      'android:exported': 'false',
    },
    'intent-filter': [
      {
        action: [
          {
            $: {
              'android:name': 'com.google.firebase.MESSAGING_EVENT',
            },
          },
        ],
      },
    ],
  } as any

  if (existingServiceIndex >= 0) {
    existingServices[existingServiceIndex] = baseService
  } else {
    existingServices.push(baseService)
  }
}

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

const withLiveUpdates: ConfigPlugin<LiveUpdatesPluginProps> = (
  config: ExpoConfig,
  props: LiveUpdatesPluginProps
) => {
  if (!props.channelId) {
    throw new Error('withLiveUpdates: channelId is required. Please provide channelId in plugin configuration.')
  }
  
  if (!props.channelName) {
    throw new Error('withLiveUpdates: channelName is required. Please provide channelName in plugin configuration.')
  }

  const { channelId, channelName } = props
  
  const scheme = Array.isArray(config.scheme) 
    ? config.scheme[0] 
    : config.scheme || DEFAULT_SCHEME

  return withAndroidManifest(config, configWithManifest => {
    const mainApplication = AndroidConfig.Manifest.getMainApplicationOrThrow(
      configWithManifest.modResults
    )

    // Add app scheme metadata
    AndroidConfig.Manifest.addMetaDataItemToMainApplication(
      mainApplication,
      EXPO_MODULE_SCHEME_KEY,
      scheme
    )

    // Add channel configuration metadata
    AndroidConfig.Manifest.addMetaDataItemToMainApplication(
      mainApplication,
      CHANNEL_ID_KEY,
      channelId
    )

    AndroidConfig.Manifest.addMetaDataItemToMainApplication(
      mainApplication,
      CHANNEL_NAME_KEY,
      channelName
    )

    // Ensure Firebase service is configured
    ensureService(configWithManifest.modResults)

    // Ensure notification dismissed receiver is configured
    ensureReceiver(configWithManifest.modResults)

    return configWithManifest
  })
}

export default withLiveUpdates
