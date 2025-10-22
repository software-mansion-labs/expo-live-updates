import type { ExpoConfig } from 'expo/config'
import {
  AndroidConfig,
  type ConfigPlugin,
  withAndroidManifest,
} from 'expo/config-plugins'

export interface LiveUpdatesPluginProps {
  channelId: string
  channelName: string
}

const EXPO_MODULE_SCHEME_KEY = 'expo.modules.scheme'
const CHANNEL_ID_KEY = 'expo.modules.liveupdates.channelId'
const CHANNEL_NAME_KEY = 'expo.modules.liveupdates.channelName'
const SERVICE_NAME = 'expo.modules.liveupdates.FirebaseService'
const RECEIVER_NAME = 'expo.modules.liveupdates.NotificationDismissedReceiver'
const LOG_PREFIX = 'ExpoLiveUpdatesModule: '

let warnedMissingScheme = false

const isFirebaseConfigured = (config: ExpoConfig): boolean => {
  return !!config.android?.googleServicesFile
}

const log = (message: string, icon: string = 'ℹ️') =>
  console.log(`${icon} ${LOG_PREFIX} ${message}`)
const checkConfigProperty = (property: string, propertyName: string) => {
  if (!property)
    throw new Error(
      LOG_PREFIX +
        `${propertyName} is required. Please provide ${propertyName} in plugin configuration.`,
    )
}

const ensureService = (
  config: ExpoConfig,
  androidManifest: AndroidConfig.Manifest.AndroidManifest,
) => {
  if (!isFirebaseConfigured(config)) {
    log('Firebase not configured - skipping Firebase service registration')
    return
  }

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
  props: LiveUpdatesPluginProps,
) => {
  const { channelId, channelName } = props

  checkConfigProperty(channelId, 'channelId')
  checkConfigProperty(channelName, 'channelName')

  const scheme = Array.isArray(config.scheme) ? config.scheme[0] : config.scheme

  return withAndroidManifest(config, configWithManifest => {
    const mainApplication = AndroidConfig.Manifest.getMainApplicationOrThrow(
      configWithManifest.modResults,
    )

    // Add app scheme metadata
    if (scheme) {
      AndroidConfig.Manifest.addMetaDataItemToMainApplication(
        mainApplication,
        EXPO_MODULE_SCHEME_KEY,
        scheme,
      )
    } else if (!warnedMissingScheme) {
      log('scheme is not configured, deeplinks will not work.', '⚠️')
      warnedMissingScheme = true
    }

    // Add channel configuration metadata
    AndroidConfig.Manifest.addMetaDataItemToMainApplication(
      mainApplication,
      CHANNEL_ID_KEY,
      channelId,
    )

    AndroidConfig.Manifest.addMetaDataItemToMainApplication(
      mainApplication,
      CHANNEL_NAME_KEY,
      channelName,
    )

    // Ensure Firebase service is configured
    ensureService(config, configWithManifest.modResults)

    // Ensure notification dismissed receiver is configured
    ensureReceiver(configWithManifest.modResults)

    return configWithManifest
  })
}

export default withLiveUpdates
