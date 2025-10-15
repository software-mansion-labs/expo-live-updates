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

const CHANNEL_ID_KEY = "expo.modules.liveupdates.channelId"
const CHANNEL_NAME_KEY = "expo.modules.liveupdates.channelName"

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
      CHANNEL_ID_KEY,
      channelId
    )

    AndroidConfig.Manifest.addMetaDataItemToMainApplication(
      mainApplication,
      CHANNEL_NAME_KEY,
      channelName
    )

    return configWithManifest
  })
}

export default withChannelConfig
