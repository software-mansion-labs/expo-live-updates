import { NativeModule, requireNativeModule } from 'expo'
import type {
  LiveUpdateState,
  LiveUpdateConfig,
  NotificationStateChangeEvent,
} from './types'

declare class ExpoLiveUpdatesModule extends NativeModule<DirectionsHeadlessModuleEvents> {
  init: (channelId: string, channelName: string) => void
  startLiveUpdate: (state: LiveUpdateState, config: LiveUpdateConfig) => number
  stopLiveUpdate: (notificationId: number) => void
  updateLiveUpdate: (notificationId: number, state: LiveUpdateState) => void
  getDevicePushTokenAsync: () => Promise<string> | null
  addNotificationStateChangeListener: () => void
}

const module = requireNativeModule<ExpoLiveUpdatesModule>(
  'ExpoLiveUpdatesModule',
)

export type DirectionsHeadlessModuleEvents = {
  onNotificationStateChange: (event: NotificationStateChangeEvent) => void
}

export default module
