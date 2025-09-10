import type { StyleProp, ViewStyle } from 'react-native'

export type OnLoadEventPayload = {
  url: string
}

export type ExpoLiveUpdatesModuleEvents = {
  onChange: (params: ChangeEventPayload) => void
}

export type ChangeEventPayload = {
  value: string
}

export type ExpoLiveUpdatesViewProps = {
  url: string
  onLoad: (event: { nativeEvent: OnLoadEventPayload }) => void
  style?: StyleProp<ViewStyle>
}
