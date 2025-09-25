import type { ExpoConfig } from 'expo/config'
import {
  AndroidConfig,
  ConfigPlugin,
  withAndroidManifest,
} from 'expo/config-plugins'

const SERVICE_NAME = 'expo.modules.liveupdates.FirebaseService'

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

const withFirebaseService: ConfigPlugin<void> = (config: ExpoConfig) => {
  return withAndroidManifest(config, configWithManifest => {
    ensureService(configWithManifest.modResults)
    return configWithManifest
  })
}

export default withFirebaseService
