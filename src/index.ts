import { Platform } from 'react-native'

import ExpoLiveUpdatesModule from './ExpoLiveUpdatesModule'

type Voidable<T> = T | void

export type DynamicIslandTimerType = 'circular' | 'digital'

export type LiveActivityState = {
  title: string
  subtitle?: string
  date?: number
  imageName?: string
  dynamicIslandImageName?: string
}

export type LiveActivityConfig = {
  backgroundColor?: string
  // titleColor?: string
  // subtitleColor?: string
  // progressViewTint?: string
  // progressViewLabelColor?: string
  // deepLinkUrl?: string
  // timerType?: DynamicIslandTimerType
}

export type ActivityTokenReceivedEvent = {
  activityID: string
  activityName: string
  activityPushToken: string
}

export type ActivityPushToStartTokenReceivedEvent = {
  activityPushToStartToken: string
}

type ActivityState = 'active' | 'dismissed' | 'pending' | 'stale' | 'ended'

export type ActivityUpdateEvent = {
  activityID: string
  activityName: string
  activityState: ActivityState
}

export type LiveActivityModuleEvents = {
  onTokenReceived: (params: ActivityTokenReceivedEvent) => void
  onPushToStartTokenReceived: (params: ActivityPushToStartTokenReceivedEvent) => void
  onStateChange: (params: ActivityUpdateEvent) => void
}

function assertAndroid(name: string) {
  const isAndroid = Platform.OS === 'android'

  if (!isAndroid) console.error(`${name} is only available on Android`)

  return isAndroid
}

export function init() {
  if (assertAndroid('init')) return ExpoLiveUpdatesModule.init()
}


/**
 * @param {LiveActivityState} state The state for the live activity.
 * @param {LiveActivityConfig} config Live activity config object.
 * @returns {string} The identifier of the started activity or undefined if creating live activity failed.
 */ 

// config?: LiveActivityConfig
export function startForegroundService(state: LiveActivityState, config: LiveActivityConfig): Voidable<string> {
  if (assertAndroid('startForegroundService')) {
    return ExpoLiveUpdatesModule.startForegroundService(state, config)}
}

/**
 * @param {string} id The identifier of the activity to stop.
 * @param {LiveActivityState} state The updated state for the live activity.
 */
// id: string, state: LiveActivityState
export function stopForegroundService() {
  if (assertAndroid('stopForegroundService')) return ExpoLiveUpdatesModule.stopForegroundService()
}

/**
 * @param {string} id The identifier of the activity to update.
 * @param {LiveActivityState} state The updated state for the live activity.
 */
// id: string, state: LiveActivityState
export function updateForegroundService(state: LiveActivityState) {
  if (assertAndroid('updateForegroundService')) return ExpoLiveUpdatesModule.updateForegroundService(state)
}

// -----------------------------------------------------------

// export function addActivityTokenListener(
//   listener: (event: ActivityTokenReceivedEvent) => void
// ): Voidable<EventSubscription> {
//   if (assertAndroid('addActivityTokenListener')) return ExpoLiveActivityModule.addListener('onTokenReceived', listener)
// }

// export function addActivityPushToStartTokenListener(
//   listener: (event: ActivityPushToStartTokenReceivedEvent) => void
// ): Voidable<EventSubscription> {
//   if (assertAndroid('addActivityPushToStartTokenListener'))
//     return ExpoLiveActivityModule.addListener('onPushToStartTokenReceived', listener)
// }

// export function addActivityUpdatesListener(
//   listener: (event: ActivityUpdateEvent) => void
// ): Voidable<EventSubscription> {
//   if (assertAndroid('addActivityUpdatesListener')) return ExpoLiveActivityModule.addListener('onStateChange', listener)
// }
