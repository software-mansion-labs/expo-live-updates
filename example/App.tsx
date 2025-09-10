import { init } from 'expo-live-updates'
import { useEffect } from 'react'

import HomeScreen from './screens/HomeScreen'

export default function App() {
  useEffect(() => {
    init()
  }, [])

  return <HomeScreen />
}
