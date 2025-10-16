import {
  AndroidConfig,
  ConfigPlugin,
  withAndroidManifest,
} from 'expo/config-plugins'

const DEFAULT_SCHEME = 'myapp'
const EXPO_MODULE_SCHEME_KEY = 'expo.modules.scheme'

const withAppScheme: ConfigPlugin<void> = (config) => {
  const scheme = Array.isArray(config.scheme) 
    ? config.scheme[0] 
    : config.scheme || DEFAULT_SCHEME
  
  config = withAndroidManifest(config, configWithManifest => {
    const mainApplication = AndroidConfig.Manifest.getMainApplicationOrThrow(
      configWithManifest.modResults
    )
    
    AndroidConfig.Manifest.addMetaDataItemToMainApplication(
      mainApplication,
      EXPO_MODULE_SCHEME_KEY,
      scheme
    )
    
    return configWithManifest
  })
  
  return config
}

export default withAppScheme