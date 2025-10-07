import { NativeModule, requireNativeModule } from 'expo'
import type {
  LiveUpdateState,
  LiveUpdateConfig,
  NotificationStateChangeEvent,
} from './types'
import type { EventSubscription } from 'react-native'

declare class ExpoLiveUpdatesModule extends NativeModule<DirectionsHeadlessModuleEvents> {
  init: (channelId: string, channelName: string) => void
  startLiveUpdate: (state: LiveUpdateState, config: LiveUpdateConfig) => number
  stopLiveUpdate: (notificationId: number) => void
  updateLiveUpdate: (notificationId: number, state: LiveUpdateState) => void
  getDevicePushTokenAsync: () => Promise<string> | null
  addNotificationStateChangeListener: () => EventSubscription | undefined
}

const module = requireNativeModule<ExpoLiveUpdatesModule>(
  'ExpoLiveUpdatesModule',
)

export type DirectionsHeadlessModuleEvents = {
  onNotificationStateChange: (event: NotificationStateChangeEvent) => void
}

export default module
