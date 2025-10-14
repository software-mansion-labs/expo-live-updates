import React from 'react'
import { StyleSheet, Text, View, Button } from 'react-native'

interface TestScreenProps {
  navigation: any
}

export default function TestScreen({ navigation }: TestScreenProps) {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Test Subpage</Text>
      <Text style={styles.description}>
        This is the test page that can be accessed via deep link /test
      </Text>
      <Button
        title="Go Back"
        onPress={() => navigation.goBack()}
      />
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
    backgroundColor: '#f5f5f5',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
    color: '#333',
  },
  description: {
    fontSize: 16,
    textAlign: 'center',
    marginBottom: 30,
    color: '#666',
    lineHeight: 24,
  },
})
