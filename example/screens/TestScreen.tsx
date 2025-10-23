import React from 'react'
import { StyleSheet, Text, View, Button } from 'react-native'
import type { NativeStackScreenProps } from '@react-navigation/native-stack'
import type { RootStackParamList } from '../App'

type TestScreenProps = NativeStackScreenProps<RootStackParamList, 'Test'>

export default function TestScreen({ navigation }: TestScreenProps) {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Test Subpage</Text>
      <Text style={styles.description}>
        This is the test page that can be accessed via deep link /Test
      </Text>
      <Button
        title="Go Back"
        onPress={() =>
          navigation.canGoBack()
            ? navigation.goBack()
            : navigation.navigate('LiveUpdates')
        }
      />
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    flex: 1,
    justifyContent: 'center',
    padding: 20,
  },
  description: {
    fontSize: 16,
    lineHeight: 24,
    marginBottom: 30,
    textAlign: 'center',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
})
