import { NativeModule } from 'expo';
import type { LiveUpdateState, LiveUpdateConfig } from './types';
declare class ExpoLiveUpdatesModule extends NativeModule {
    init: (channelId: string, channelName: string) => void;
    startForegroundService: (state: LiveUpdateState, config: LiveUpdateConfig) => void;
    stopForegroundService: () => void;
    updateForegroundService: (state: LiveUpdateState) => void;
}
declare const module: ExpoLiveUpdatesModule;
export default module;
//# sourceMappingURL=ExpoLiveUpdatesModule.d.ts.map