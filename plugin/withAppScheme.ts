import {
  AndroidConfig,
  ConfigPlugin,
  withAndroidManifest,
} from 'expo/config-plugins'

const DEFAULT_SCHEME = 'myapp'

const withAppScheme: ConfigPlugin<void> = (config) => {
  const scheme = Array.isArray(config.scheme) 
    ? config.scheme[0] 
    : config.scheme || DEFAULT_SCHEME
  
  config = withAndroidManifest(config, config => {
    const mainApplication = AndroidConfig.Manifest.getMainApplicationOrThrow(
      config.modResults
    )
    
    AndroidConfig.Manifest.addMetaDataItemToMainApplication(
      mainApplication,
      'expo.modules.scheme',
      scheme
    )
    
    return config
  })
  
  return config
}

export default withAppScheme