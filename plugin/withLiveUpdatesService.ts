import type { ExpoConfig } from 'expo/config'
import {
  AndroidConfig,
  ConfigPlugin,
  withAndroidManifest,
} from 'expo/config-plugins'

const SERVICE_NAME = 'expo.modules.liveupdates.LiveUpdatesService'

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
    $: {
      'android:name': SERVICE_NAME,
    },
  } as any

  if (existingServiceIndex >= 0) {
    existingServices[existingServiceIndex] = baseService
  } else {
    existingServices.push(baseService)
  }
}

const withLiveUpdatesService: ConfigPlugin = (
  config: ExpoConfig,
) => {
  return withAndroidManifest(config, configWithManifest => {
    ensureService(configWithManifest.modResults)
    return configWithManifest
  })
}

export default withLiveUpdatesService