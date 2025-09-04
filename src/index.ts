// Reexport the native module. On web, it will be resolved to ExpoLiveUpdatesModule.web.ts
// and on native platforms to ExpoLiveUpdatesModule.ts
export { default } from './ExpoLiveUpdatesModule';
export * from  './ExpoLiveUpdates.types';
