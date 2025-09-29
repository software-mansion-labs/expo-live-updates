import { Platform } from 'react-native'
import ExpoLiveUpdatesModule from './ExpoLiveUpdatesModule'
import type { LiveUpdateState, LiveUpdateConfig } from './types'

type Voidable<T> = T | void

function assertAndroid(name: string) {
  const isAndroid = Platform.OS === 'android'

  if (!isAndroid) console.error(`${name} is only available on Android`)

  return isAndroid
}

/**
 * @param {string} channelId The state for the live live update.
 * @param {string} channelName Live live update config object.
 */
export function init(channelId: string, channelName: string) {
  if (assertAndroid('init'))
    return ExpoLiveUpdatesModule.init(channelId, channelName)
}

/**
 * @param {LiveUpdateState} state The state for the live live update.
 * @param {LiveUpdateConfig} config Live live update config object.
 * @returns {string} The identifier of the started live update or undefined if creating live live update failed.
 */

// config?: LiveUpdateConfig
export function startLiveUpdate(
  state: LiveUpdateState,
  config: LiveUpdateConfig,
): Voidable<string> {
  if (assertAndroid('startLiveUpdate')) {
    return ExpoLiveUpdatesModule.startLiveUpdate(state, config)
  }
}

/**
 * @param {string} id The identifier of the live update to cancel.
 * @param {LiveUpdateState} state The updated state for the live live update.
 */
// id: string, state: LiveUpdateState
export function cancelLiveUpdate() {
  if (assertAndroid('cancelLiveUpdate'))
    return ExpoLiveUpdatesModule.cancelLiveUpdate()
}

/**
 * @param {string} id The identifier of the live update to update.
 * @param {LiveUpdateState} state The updated state for the live live update.
 */
// id: string, state: LiveUpdateState
export function updateLiveUpdate(state: LiveUpdateState) {
  if (assertAndroid('updateLiveUpdate'))
    return ExpoLiveUpdatesModule.updateLiveUpdate(state)
}

export async function getDevicePushTokenAsync() {
  if (assertAndroid('getDevicePushTokenAsync')) {
    return await ExpoLiveUpdatesModule.getDevicePushTokenAsync()
  } else {
    return null
  }
}
