export type LiveUpdateImage = {
  url: string
  isRemote: boolean
}

export type LiveUpdateProgressPoint = {
  position: number
  color?: string
}

export type LiveUpdateProgressSegment = {
  length: number
  color?: string
}

export type LiveUpdateProgress = {
  max?: number
  progress?: number
  indeterminate?: boolean
  points?: LiveUpdateProgressPoint[]
  segments?: LiveUpdateProgressSegment[]
}

export type LiveUpdateState = {
  title: string
  text?: string
  subText?: string
  image?: LiveUpdateImage
  icon?: LiveUpdateImage
  progress?: LiveUpdateProgress
  shortCriticalText?: string
  showTime?: boolean
  time?: number
}

export type LiveUpdateConfig = {
  deepLinkUrl?: string
  iconBackgroundColor?: string // only SDK < 16
  playSound?: boolean
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
