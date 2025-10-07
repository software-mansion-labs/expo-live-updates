import { NativeModule, requireNativeModule } from 'expo'
import type {
  LiveUpdateState,
  LiveUpdateConfig,
  ExpoLiveUpdatesModuleEvents,
} from './types'

declare class ExpoLiveUpdatesModule extends NativeModule<ExpoLiveUpdatesModuleEvents> {
  init: (channelId: string, channelName: string) => void
  startLiveUpdate: (state: LiveUpdateState, config: LiveUpdateConfig) => number
  stopLiveUpdate: (notificationId: number) => void
  updateLiveUpdate: (notificationId: number, state: LiveUpdateState) => void
}

const module = requireNativeModule<ExpoLiveUpdatesModule>(
  'ExpoLiveUpdatesModule',
)

export default module
