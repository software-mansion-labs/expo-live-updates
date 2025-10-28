import { NavigationContainer } from '@react-navigation/native'
import { createNativeStackNavigator } from '@react-navigation/native-stack'
import * as Linking from 'expo-linking'

import TestScreen from './screens/TestScreen'
import LiveUpdatesScreen from './screens/LiveUpdatesScreen'

export type RootStackParamList = {
  LiveUpdates: undefined
  Test: undefined
}

const Stack = createNativeStackNavigator<RootStackParamList>()
const prefix = Linking.createURL('/')

export default function App() {
  const linking = {
    prefixes: [prefix],
  }

  return (
    <NavigationContainer linking={linking}>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        <Stack.Screen name="LiveUpdates" component={LiveUpdatesScreen} />
        <Stack.Screen name="Test" component={TestScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  )
}
