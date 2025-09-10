import { NativeModule, requireNativeModule } from 'expo'
import type { LiveUpdateState, LiveUpdateConfig } from './types'

declare class ExpoLiveUpdatesModule extends NativeModule {
  init: () => void
  startForegroundService: (
    state: LiveUpdateState,
    config: LiveUpdateConfig,
  ) => void
  stopForegroundService: () => void
  updateForegroundService: (state: LiveUpdateState) => void
}

const module = requireNativeModule<ExpoLiveUpdatesModule>(
  'ExpoLiveUpdatesModule',
)

export default module
