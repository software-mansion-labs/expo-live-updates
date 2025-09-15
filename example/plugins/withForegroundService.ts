import type { ExpoConfig } from 'expo/config'
import { AndroidConfig, withAndroidManifest } from 'expo/config-plugins'

export default function withForegroundService(config: ExpoConfig) {
  return withAndroidManifest(config, manifestConfig => {
    const mainApplication = AndroidConfig.Manifest.getMainApplicationOrThrow(
      manifestConfig.modResults,
    )
    mainApplication.service = []
    mainApplication.service.push({
      $: {
        'android:name': 'expo.modules.liveupdates.LiveUpdatesForegroundService',
        'android:foregroundServiceType': 'specialUse',
      },
      // dodajemy property wewnątrz serwisu
      property: [
        {
          $: {
            'android:name': 'android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE',
            'android:value': 'explanation_for_special_use',
          },
        },
      ],
    })
    return manifestConfig
  })
}
