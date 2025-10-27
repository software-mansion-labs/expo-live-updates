export type LiveUpdateProgress = {
  max?: number
  progress?: number
  indeterminate?: boolean
}

export type LiveUpdateState = {
  title: string
  text?: string
  subText?: string
  imageName?: string
  dynamicIslandImageName?: string
  progress?: LiveUpdateProgress
  shortCriticalText?: string
}

export type LiveUpdateConfig = {
  backgroundColor?: string // only SDK < 16
  deepLinkUrl?: string
}

export type TokenChangeEvent = {
  token: string
}

export type NotificationStateChangeEvent = {
  notificationId: number
  action: 'dismissed' | 'updated' | 'started' | 'stopped' | 'clicked'
  timestamp: number
}

export type NotificationStateChangeListener = (
  event: NotificationStateChangeEvent,
) => void
