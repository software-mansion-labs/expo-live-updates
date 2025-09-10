import { useEffect } from 'react'

import { init } from 'expo-live-updates'
import HomeScreen from './screens/HomeScreen'

export default function App() {
  useEffect(() => {
    init()
  }, [])

  return <HomeScreen />
}
