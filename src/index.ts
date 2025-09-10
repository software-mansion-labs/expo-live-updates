import { Platform } from 'react-native'
import ExpoLiveUpdatesModule from './ExpoLiveUpdatesModule'
import type { LiveUpdateState, LiveUpdateConfig } from './types'

type Voidable<T> = T | void

function assertAndroid(name: string) {
  const isAndroid = Platform.OS === 'android'

  if (!isAndroid) console.error(`${name} is only available on Android`)

  return isAndroid
}

export function init() {
  if (assertAndroid('init')) return ExpoLiveUpdatesModule.init()
}

/**
 * @param {LiveUpdateState} state The state for the live live update.
 * @param {LiveUpdateConfig} config Live live update config object.
 * @returns {string} The identifier of the started live update or undefined if creating live live update failed.
 */

// config?: LiveUpdateConfig
export function startForegroundService(
  state: LiveUpdateState,
  config: LiveUpdateConfig,
): Voidable<string> {
  if (assertAndroid('startForegroundService')) {
    return ExpoLiveUpdatesModule.startForegroundService(state, config)
  }
}

/**
 * @param {string} id The identifier of the live update to stop.
 * @param {LiveUpdateState} state The updated state for the live live update.
 */
// id: string, state: LiveUpdateState
export function stopForegroundService() {
  if (assertAndroid('stopForegroundService'))
    return ExpoLiveUpdatesModule.stopForegroundService()
}

/**
 * @param {string} id The identifier of the live update to update.
 * @param {LiveUpdateState} state The updated state for the live live update.
 */
// id: string, state: LiveUpdateState
export function updateForegroundService(state: LiveUpdateState) {
  if (assertAndroid('updateForegroundService'))
    return ExpoLiveUpdatesModule.updateForegroundService(state)
}
