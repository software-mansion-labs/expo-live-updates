import type { EventSubscription } from 'expo-modules-core'
import { Platform } from 'react-native'
import ExpoLiveUpdatesModule from './ExpoLiveUpdatesModule'
import type {
  LiveUpdateState,
  LiveUpdateConfig,
  TokenChangeEvent,
} from './types'

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
 * @returns {number} The identifier of the started live update or undefined if creating live live update failed.
 */
export function startLiveUpdate(
  state: LiveUpdateState,
  config: LiveUpdateConfig,
): Voidable<number> {
  if (assertAndroid('startLiveUpdate')) {
    return ExpoLiveUpdatesModule.startLiveUpdate(state, config)
  }
}

/**
 * @param {number} notificationId The identifier of the live update to stop.
 */
export function stopLiveUpdate(notificationId: number) {
  if (assertAndroid('stopLiveUpdate'))
    return ExpoLiveUpdatesModule.stopLiveUpdate(notificationId)
}

/**
 * @param {number} notificationId The identifier of the live update to update.
 * @param {LiveUpdateState} state The updated state for the live live update.
 */
export function updateLiveUpdate(
  notificationId: number,
  state: LiveUpdateState,
) {
  if (assertAndroid('updateLiveUpdate'))
    return ExpoLiveUpdatesModule.updateLiveUpdate(notificationId, state)
}

export function addTokenListener(
  listener: (event: TokenChangeEvent) => void,
): Voidable<EventSubscription> {
  if (assertAndroid('addTokenListener'))
    return ExpoLiveUpdatesModule.addListener('onTokenChange', listener)
}
