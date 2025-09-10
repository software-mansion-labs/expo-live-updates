import { useLinkingURL } from 'expo-linking'
import * as React from 'react'
import { StyleSheet, Text } from 'react-native'
import CreateLiveUpdatesScreen from './CreateLiveUpdatesScreen'

export default function HomeScreen() {
  const url = useLinkingURL()

  return (
    <>
      <CreateLiveUpdatesScreen />
      <Text style={styles.urlText}>URL: {url}</Text>
    </>
  )
}

const styles = StyleSheet.create({
  urlText: {
    padding: 20,
  },
})
