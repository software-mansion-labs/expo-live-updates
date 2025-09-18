import type { LiveUpdateState, LiveUpdateConfig } from './types';
type Voidable<T> = T | void;
/**
 * @param {string} channelId The state for the live live update.
 * @param {string} channelName Live live update config object.
 */
export declare function init(channelId: string, channelName: string): void;
/**
 * @param {LiveUpdateState} state The state for the live live update.
 * @param {LiveUpdateConfig} config Live live update config object.
 * @returns {string} The identifier of the started live update or undefined if creating live live update failed.
 */
export declare function startForegroundService(state: LiveUpdateState, config: LiveUpdateConfig): Voidable<string>;
/**
 * @param {string} id The identifier of the live update to stop.
 * @param {LiveUpdateState} state The updated state for the live live update.
 */
export declare function stopForegroundService(): void;
/**
 * @param {string} id The identifier of the live update to update.
 * @param {LiveUpdateState} state The updated state for the live live update.
 */
export declare function updateForegroundService(state: LiveUpdateState): void;
export {};
//# sourceMappingURL=index.d.ts.map