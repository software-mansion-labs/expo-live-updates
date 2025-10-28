import { NativeModule, requireOptionalNativeModule } from 'expo'
import type {
  LiveUpdateState,
  LiveUpdateConfig,
  NotificationStateChangeEvent,
  TokenChangeEvent,
} from './types'
import type { EventSubscription } from 'react-native'

type ExpoLiveUpdatesModuleEvents = {
  onNotificationStateChange: (event: NotificationStateChangeEvent) => void
  onTokenChange: (event: TokenChangeEvent) => void
}
declare class ExpoLiveUpdatesModule extends NativeModule<ExpoLiveUpdatesModuleEvents> {
  startLiveUpdate: (state: LiveUpdateState, config?: LiveUpdateConfig) => number
  stopLiveUpdate: (notificationId: number) => void
  updateLiveUpdate: (
    notificationId: number,
    state: LiveUpdateState,
    config?: LiveUpdateConfig,
  ) => void
  addNotificationStateChangeListener: () => EventSubscription | undefined
  addTokenChangeListener: () => EventSubscription | undefined
}

const module = requireOptionalNativeModule<ExpoLiveUpdatesModule>(
  'ExpoLiveUpdatesModule',
)

export default module
