import { NativeModule, requireNativeModule } from 'expo'
import type { LiveUpdateState, LiveUpdateConfig } from './types'

declare class ExpoLiveUpdatesModule extends NativeModule {
  init: (channelId: string, channelName: string) => void
  startLiveUpdate: (state: LiveUpdateState, config: LiveUpdateConfig) => number
  stopLiveUpdate: (notificationId: number) => void
  updateLiveUpdate: (notificationId: number, state: LiveUpdateState) => void
  getDevicePushTokenAsync: () => Promise<string> | null
}

const module = requireNativeModule<ExpoLiveUpdatesModule>(
  'ExpoLiveUpdatesModule',
)

export default module
