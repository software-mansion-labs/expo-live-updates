import type { ExpoConfig } from 'expo/config'
import { AndroidConfig, ConfigPlugin, withAndroidManifest } from 'expo/config-plugins'

type PluginProps = {
  foregroundServiceType: string
  explanationForSpecialUse?: string
}

const SERVICE_NAME = 'expo.modules.liveupdates.LiveUpdatesForegroundService'

const ensureService = (
  androidManifest: AndroidConfig.Manifest.AndroidManifest,
  { foregroundServiceType, explanationForSpecialUse }: PluginProps,
) => {
  const mainApplication = AndroidConfig.Manifest.getMainApplicationOrThrow(androidManifest)

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

const withLiveUpdatesForegroundService: ConfigPlugin<PluginProps> = (
  config: ExpoConfig,
  props: PluginProps,
) => {
  return withAndroidManifest(config, (configWithManifest) => {
    ensureService(configWithManifest.modResults, props)
    return configWithManifest
  })
}

export default withLiveUpdatesForegroundService


