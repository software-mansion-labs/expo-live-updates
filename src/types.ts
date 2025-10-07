export type LiveUpdateState = {
  title: string
  subtitle?: string
  date?: number
  imageName?: string
  dynamicIslandImageName?: string
}

export type LiveUpdateConfig = {
  backgroundColor?: string // only SDK < 16
}

export type NotificationStateChangeEvent = {
  notificationId: number
  action: 'dismissed' | 'updated'
  timestamp: number
}

export type NotificationStateChangeListener = (
  event: NotificationStateChangeEvent,
) => void
