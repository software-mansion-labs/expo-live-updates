import { init } from 'expo-live-updates'
import { useEffect } from 'react'
import { NavigationContainer } from '@react-navigation/native'
import { createNativeStackNavigator } from '@react-navigation/native-stack'
import * as Linking from 'expo-linking'

import TestScreen from './screens/TestScreen'
import CreateLiveUpdatesScreen from './screens/CreateLiveUpdatesScreen'

const CHANNEL_ID = 'LiveUpdatesServiceChannelId'
const CHANNEL_NAME = 'Live Updates Service Channel Name'

const Stack = createNativeStackNavigator()
const prefix = Linking.createURL('/')

export default function App() {
  useEffect(() => {
    init(CHANNEL_ID, CHANNEL_NAME)
  }, [])

  const linking = {
    prefixes: [prefix],
  }

  return (
    <NavigationContainer linking={linking}>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        <Stack.Screen name="Home" component={CreateLiveUpdatesScreen} />
        <Stack.Screen name="test" component={TestScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  )
}
