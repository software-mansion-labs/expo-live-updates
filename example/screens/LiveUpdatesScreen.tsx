import IOSPlaceholder from './IOSPlaceholder'
import CreateLiveUpdatesScreen from './CreateLiveUpdatesScreen'
import { Platform } from 'react-native'

export default function LiveUpdatesScreen() {
  if (Platform.OS === 'ios') {
    return <IOSPlaceholder />
  } else {
    return <CreateLiveUpdatesScreen />
  }
}
