import { Linking, StyleSheet, Text, TouchableOpacity, View } from 'react-native'
import { SafeAreaView } from 'react-native-safe-area-context'

const LIVE_ACTIVITY_URL =
  'https://github.com/software-mansion-labs/expo-live-activity'

export default function LiveUpdatesScreen() {
  const handleOpenLink = () => {
    Linking.openURL(LIVE_ACTIVITY_URL)
  }

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.emoji}>📱</Text>
        <Text style={styles.title}>Android Only</Text>
        <Text style={styles.description}>
          Expo Live Updates is designed for Android and provides persistent
          notification functionality.
        </Text>
        <View style={styles.divider} />
        <Text style={styles.subtitle}>Looking for iOS?</Text>
        <Text style={styles.description}>
          Check out <Text style={styles.boldText}>expo-live-activity</Text> for
          similar functionality on iOS using Live Activities!
        </Text>
        <TouchableOpacity
          style={styles.linkButton}
          onPress={handleOpenLink}
          activeOpacity={0.7}>
          <Text style={styles.linkButtonText}>
            View expo-live-activity on GitHub
          </Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  )
}

const styles = StyleSheet.create({
  boldText: {
    fontWeight: '600',
  },
  container: {
    alignItems: 'center',
    backgroundColor: '#F8F9FA',
    flex: 1,
    justifyContent: 'center',
  },
  content: {
    alignItems: 'center',
    backgroundColor: '#FFFFFF',
    borderRadius: 16,
    elevation: 2,
    marginHorizontal: 24,
    padding: 32,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
  },
  description: {
    color: '#666666',
    fontSize: 16,
    lineHeight: 24,
    marginTop: 12,
    textAlign: 'center',
  },
  divider: {
    backgroundColor: '#E0E0E0',
    height: 1,
    marginVertical: 24,
    width: '100%',
  },
  emoji: {
    fontSize: 64,
    marginBottom: 16,
  },
  linkButton: {
    backgroundColor: '#007AFF',
    borderRadius: 12,
    marginTop: 24,
    paddingHorizontal: 24,
    paddingVertical: 14,
  },
  linkButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: '600',
    textAlign: 'center',
  },
  subtitle: {
    color: '#333333',
    fontSize: 20,
    fontWeight: '600',
    marginBottom: 8,
  },
  title: {
    color: '#333333',
    fontSize: 28,
    fontWeight: 'bold',
  },
})
