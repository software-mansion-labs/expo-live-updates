import type { EventSubscription } from 'expo-modules-core'
import { Platform } from 'react-native'
import ExpoLiveUpdatesModule from './ExpoLiveUpdatesModule'
import type {
  LiveUpdateState,
  LiveUpdateConfig,
  TokenChangeEvent,
  NotificationStateChangeEvent,
} from './types'

type Voidable<T> = T | void

function assertAndroid(name: string): boolean {
  const isAndroid = Platform.OS === 'android'

  if (!isAndroid) {
    console.error(`${name} is only available on Android`)
    return false
  }

  if (!ExpoLiveUpdatesModule) {
    console.error(`${name} is not available: native module not found`)
    return false
  }

  return true
}

/**
 * @param {LiveUpdateState} state The state for the Live Update.
 * @param {LiveUpdateConfig} config Optional configuration for the Live Update.
 * @returns {number} The identifier of the started Live Update or undefined if failed.
 */
export function startLiveUpdate(
  state: LiveUpdateState,
  config?: LiveUpdateConfig,
): Voidable<number> {
  if (assertAndroid('startLiveUpdate')) {
    return ExpoLiveUpdatesModule?.startLiveUpdate(state, config)
  }
}

/**
 * @param {number} notificationId The identifier of the Live Update to stop.
 */
export function stopLiveUpdate(notificationId: number) {
  if (assertAndroid('stopLiveUpdate')) {
    return ExpoLiveUpdatesModule?.stopLiveUpdate(notificationId)
  }
}

/**
 * @param {number} notificationId The identifier of the Live Update to update.
 * @param {LiveUpdateState} state The updated state for the Live Update.
 * @param {LiveUpdateConfig} config Optional configuration for the Live Update.
 */
export function updateLiveUpdate(
  notificationId: number,
  state: LiveUpdateState,
  config?: LiveUpdateConfig,
) {
  if (assertAndroid('updateLiveUpdate')) {
    return ExpoLiveUpdatesModule?.updateLiveUpdate(
      notificationId,
      state,
      config,
    )
  }
}

/**
 * Add a listener for Firebase Cloud Messaging token changes.
 * @param {function} listener The listener function to be called when the token changes.
 * @returns {EventSubscription} The subscription object or undefined if not supported.
 */
export function addTokenChangeListener(
  listener: (event: TokenChangeEvent) => void,
): Voidable<EventSubscription> {
  if (assertAndroid('addTokenChangeListener')) {
    return ExpoLiveUpdatesModule?.addListener('onTokenChange', listener)
  }
}

/**
 * Add a listener for Live Update notification state changes.
 * @param {function} listener The listener function to be called when notification state changes.
 * @returns {EventSubscription} The subscription object or undefined if not supported.
 */
export function addNotificationStateChangeListener(
  listener: (event: NotificationStateChangeEvent) => void,
): Voidable<EventSubscription> {
  if (assertAndroid('addNotificationStateChangeListener')) {
    return ExpoLiveUpdatesModule?.addListener(
      'onNotificationStateChange',
      listener,
    )
  }
}
