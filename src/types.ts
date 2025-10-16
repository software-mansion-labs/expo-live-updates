export type LiveUpdateState = {
  title: string
  subtitle?: string
  imageName?: string
  dynamicIslandImageName?: string
  shortCriticalText?: string
}

export type LiveUpdateConfig = {
  backgroundColor?: string // only SDK < 16
}

export type TokenChangeEvent = {
  token: string
}

export type NotificationStateChangeEvent = {
  notificationId: number
  action: 'dismissed' | 'updated' | 'started' | 'stopped'
  timestamp: number
}

export type NotificationStateChangeListener = (
  event: NotificationStateChangeEvent,
) => void
