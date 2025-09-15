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
