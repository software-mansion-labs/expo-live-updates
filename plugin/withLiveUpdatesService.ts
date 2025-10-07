import type { ExpoConfig } from 'expo/config'
import {
  AndroidConfig,
  ConfigPlugin,
  withAndroidManifest,
} from 'expo/config-plugins'

type PluginProps = {
  foregroundServiceType: string
  explanationForSpecialUse?: string
}

const SERVICE_NAME = 'expo.modules.liveupdates.LiveUpdatesService'
const RECEIVER_NAME = 'expo.modules.liveupdates.service.NotificationDismissedReceiver'

const ensureService = (
  androidManifest: AndroidConfig.Manifest.AndroidManifest,
  { foregroundServiceType, explanationForSpecialUse }: PluginProps,
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
      'android:foregroundServiceType': foregroundServiceType,
    },
  } as any

  if (explanationForSpecialUse && explanationForSpecialUse.length > 0) {
    baseService.property = [
      {
        $: {
          'android:name': 'android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE',
          'android:value': explanationForSpecialUse,
        },
      },
    ]
  }

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

const withLiveUpdatesForegroundService: ConfigPlugin<PluginProps> = (
  config: ExpoConfig,
  props: PluginProps,
) => {
  return withAndroidManifest(config, configWithManifest => {
    ensureService(configWithManifest.modResults, props)
    ensureReceiver(configWithManifest.modResults)
    return configWithManifest
  })
}

export default withLiveUpdatesForegroundService
