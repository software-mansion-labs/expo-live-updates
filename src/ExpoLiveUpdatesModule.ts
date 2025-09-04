import { NativeModule, requireNativeModule } from 'expo';

import { ExpoLiveUpdatesModuleEvents } from './ExpoLiveUpdates.types';

declare class ExpoLiveUpdatesModule extends NativeModule<ExpoLiveUpdatesModuleEvents> {
  PI: number;
  hello(): string;
  startService(): ()=> void;
  stopService(): ()=> void;
  setValueAsync(value: string): Promise<void>;
}

// This call loads the native module object from the JSI.
const module = requireNativeModule<ExpoLiveUpdatesModule>('ExpoLiveUpdatesModule');

export function initLiveUpdates() {
  module.init()
}

export default module