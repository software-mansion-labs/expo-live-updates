import { init } from 'expo-live-updates'
import { useEffect } from 'react'

import HomeScreen from './screens/HomeScreen'

const CHANNEL_ID = 'LiveUpdatesServiceChannelId'
const CHANNEL_NAME = 'Live Updates Service Channel Name'

export default function App() {
  useEffect(() => {
    init(CHANNEL_ID, CHANNEL_NAME)
  }, [])

  return <HomeScreen />
}
