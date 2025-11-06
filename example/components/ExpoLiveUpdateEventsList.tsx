import type { NotificationStateChangeEvent } from 'expo-live-updates/types'
import { useRef } from 'react'
import { ScrollView, StyleSheet, Text, View } from 'react-native'

type ExpoLiveUpdateEventsListProps = {
  events: NotificationStateChangeEvent[]
}

export default function ExpoLiveUpdateEventsList({
  events,
}: ExpoLiveUpdateEventsListProps) {
  const scrollViewRef = useRef<ScrollView>(null)

  return (
    <View style={styles.eventsContainer}>
      <ScrollView
        ref={scrollViewRef}
        onContentSizeChange={() => scrollViewRef.current?.scrollToEnd()}>
        {events.length === 0 ? (
          <Text style={styles.noEventsText}>No events yet</Text>
        ) : (
          events.map((event, index) => (
            <Text key={index} style={styles.eventText}>
              {event.action} (ID: {event.notificationId}) -{' '}
              {new Date(event.timestamp).toLocaleTimeString()}
            </Text>
          ))
        )}
      </ScrollView>
    </View>
  )
}

const styles = StyleSheet.create({
  eventText: {
    fontSize: 14,
    marginBottom: 5,
  },
  eventsContainer: {
    backgroundColor: '#e5e5e5ff',
    borderRadius: 8,
    height: 200,
    padding: 16,
  },
  noEventsText: {
    color: '#666',
    fontStyle: 'italic',
  },
})
