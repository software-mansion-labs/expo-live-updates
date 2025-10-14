import type { ExpoConfig } from 'expo/config'
import {
  AndroidConfig,
  ConfigPlugin,
  withAndroidManifest,
} from 'expo/config-plugins'

export interface ChannelConfigPluginProps {
  channelId: string
  channelName: string
}

const withChannelConfig: ConfigPlugin<ChannelConfigPluginProps> = (
  config: ExpoConfig,
  props: ChannelConfigPluginProps
) => {
  if (!props.channelId) {
    throw new Error('withChannelConfig: channelId is required. Please provide channelId in plugin configuration.')
  }
  
  if (!props.channelName) {
    throw new Error('withChannelConfig: channelName is required. Please provide channelName in plugin configuration.')
  }

  const { channelId, channelName } = props

  return withAndroidManifest(config, configWithManifest => {
    const mainApplication = AndroidConfig.Manifest.getMainApplicationOrThrow(
      configWithManifest.modResults
    )

    AndroidConfig.Manifest.addMetaDataItemToMainApplication(
      mainApplication,
      'expo.modules.liveupdates.channelId',
      channelId
    )

    AndroidConfig.Manifest.addMetaDataItemToMainApplication(
      mainApplication,
      'expo.modules.liveupdates.channelName',
      channelName
    )

    return configWithManifest
  })
}

export default withChannelConfig
