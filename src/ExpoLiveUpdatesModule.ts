import { NativeModule, requireNativeModule } from 'expo'
import type {
  LiveUpdateState,
  LiveUpdateConfig,
  TokenChangeEvent,
} from './types'

declare class ExpoLiveUpdatesModule extends NativeModule {
  init: (channelId: string, channelName: string) => void
  startLiveUpdate: (state: LiveUpdateState, config: LiveUpdateConfig) => number
  stopLiveUpdate: (notificationId: number) => void
  updateLiveUpdate: (notificationId: number, state: LiveUpdateState) => void
  onTokenChange: (params: TokenChangeEvent) => void
}

const module = requireNativeModule<ExpoLiveUpdatesModule>(
  'ExpoLiveUpdatesModule',
)

export default module
