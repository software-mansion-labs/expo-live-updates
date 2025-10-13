import { Asset } from 'expo-asset'
import {
  startLiveUpdate,
  stopLiveUpdate,
  updateLiveUpdate,
  addTokenChangeListener,
  addNotificationStateChangeListener,
} from 'expo-live-updates'
import type {
  LiveUpdateConfig,
  LiveUpdateState,
  NotificationStateChangeEvent,
} from 'expo-live-updates/types'
import { useEffect, useMemo, useRef, useState } from 'react'
import {
  Button,
  Keyboard,
  PermissionsAndroid,
  Platform,
  ScrollView,
  StyleSheet,
  Switch,
  Text,
  TextInput,
  View,
} from 'react-native'
import * as Clipboard from 'expo-clipboard'

const toggle = (previousState: boolean) => !previousState

export default function CreateLiveUpdatesScreen() {
  const [title, onChangeTitle] = useState('This is title')
  const [backgroundColor, setBackgroundColor] = useState('red')
  const [subtitle, onChangeSubtitle] = useState('This is a subtitle')
  const [imageUri, setImageUri] = useState<string>()
  const [iconImageUri, setIconImageUri] = useState<string>()
  const [passSubtitle, setPassSubtitle] = useState(true)
  const [passImage, setPassImage] = useState(true)
  const [passIconImage, setPassIconImage] = useState(true)
  const [notificationIdString, setNotificationIdString] = useState<string>('')
  const [token, setToken] = useState<string | undefined>(undefined)
  const [notificationEvents, setNotificationEvents] = useState<
    NotificationStateChangeEvent[]
  >([])

  const ref = useRef<ScrollView>(null)

  const notificationId = useMemo(() => {
    const parsedNotificationId = parseInt(notificationIdString)
    return !isNaN(parsedNotificationId) ? parsedNotificationId : undefined
  }, [notificationIdString])

  useEffect(() => {
    const loadImages = async () => {
      const images = await getImgsUri()
      setImageUri(images.logo)
      setIconImageUri(images.logoIsland)
    }

    loadImages()
    requestNotificationsPermission()

    const handleNotificationStateChange = (
      event: NotificationStateChangeEvent,
    ) => {
      setNotificationEvents(prev => [...prev, event])
    }

    const subscription = addNotificationStateChangeListener(
      handleNotificationStateChange,
    )

    return () => {
      subscription?.remove()
    }
  }, [])

  const getState = (): LiveUpdateState => ({
    title,
    subtitle: passSubtitle ? subtitle : undefined,
    imageName: passImage ? imageUri : undefined,
    dynamicIslandImageName: passIconImage ? iconImageUri : undefined,
  })

  const handleStartLiveUpdate = () => {
    Keyboard.dismiss()

    try {
      const liveUpdateConfig: LiveUpdateConfig = {
        backgroundColor,
      }
      const id = startLiveUpdate(getState(), liveUpdateConfig)
      if (id) {
        console.log('Notification started with id: ', id)
      } else {
        throw new Error('no notificationId returned')
      }
    } catch (e) {
      console.error('Starting Live Update failed! ' + e)
    }
  }

  const handleStopLiveUpdate = () => {
    try {
      if (notificationId) {
        stopLiveUpdate(notificationId)
      } else {
        throw Error('notificationId is undefined')
      }
    } catch (e) {
      console.error('Stopping live update failed! ' + e)
    }
  }

  const handleUpdateLiveUpdate = () => {
    try {
      if (notificationId) {
        updateLiveUpdate(notificationId, getState())
      } else {
        throw Error('notificationId is undefined')
      }
    } catch (e) {
      console.error('Updating live update failed! ' + e)
    }
  }

  const handleCopyPushToken = () => {
    try {
      if (token !== undefined) {
        Clipboard.setStringAsync(token)
      } else {
        throw Error('push token is undefined')
      }
    } catch (e) {
      console.error('Copying push token failed! ' + e)
    }
  }

  useEffect(() => {
    const subscription = addTokenChangeListener(({ token: receivedToken }) =>
      setToken(receivedToken),
    )

    return () => subscription?.remove()
  }, [setToken])

  return (
    <View style={styles.screenContainer}>
      <View style={styles.sectionContainer}>
        <View style={styles.inputContainer}>
          <Text style={styles.label}>Set Live Update title:</Text>
          <TextInput
            style={styles.input}
            onChangeText={onChangeTitle}
            placeholder="Live Update title"
            value={title}
          />
        </View>

        <View style={styles.inputContainer}>
          <View style={styles.labelWithSwitch}>
            <Text style={styles.label}>Set Live Update subtitle:</Text>
            <Switch
              onValueChange={() => setPassSubtitle(toggle)}
              value={passSubtitle}
            />
          </View>
          <TextInput
            style={[styles.input, !passSubtitle && styles.disabledInput]}
            onChangeText={onChangeSubtitle}
            placeholder="Live Update subtitle"
            value={subtitle}
            editable={passSubtitle}
          />
        </View>

        <View style={styles.labelWithSwitch}>
          <Text style={styles.label}>Set Live Update image:</Text>
          <Switch
            onValueChange={() => setPassImage(toggle)}
            value={passImage}
          />
        </View>

        <View style={styles.labelWithSwitch}>
          <Text style={styles.label}>Set Live Update icon image:</Text>
          <Switch
            onValueChange={() => setPassIconImage(toggle)}
            value={passIconImage}
          />
        </View>

        {!isBaklava() && (
          <View style={styles.inputContainer}>
            <View style={styles.labelWithSwitch}>
              <Text style={styles.label}>
                Set Live Update background color (hex) (for SDK &lt; 16
                Baklava):
              </Text>
            </View>
            <TextInput
              style={styles.input}
              onChangeText={setBackgroundColor}
              autoCapitalize="none"
              placeholder="Live Update background color"
              value={backgroundColor}
            />
          </View>
        )}

        <View
          style={[styles.sectionContainer, styles.verticalButtonsContainer]}>
          <Button
            title="Start"
            onPress={handleStartLiveUpdate}
            disabled={title === ''}
          />
          <Button title="Copy Push Token" onPress={handleCopyPushToken} />
        </View>
      </View>

      <View style={styles.sectionContainer}>
        <Text style={styles.sectionTitle}>Manage existing Live Update</Text>

        <View style={styles.inputContainer}>
          <Text style={styles.label}>Set Live Update ID:</Text>
          <View style={styles.manageUpdatesContainer}>
            <TextInput
              style={[styles.input, styles.manageInput]}
              placeholder="Live Update ID"
              onChangeText={setNotificationIdString}
              value={notificationIdString}
              keyboardType="numeric"
            />

            <Button title="Stop" onPress={handleStopLiveUpdate} />
            <Button title="Update" onPress={handleUpdateLiveUpdate} />
          </View>
        </View>
      </View>

      <View style={styles.eventsContainer}>
        <Text style={styles.eventsTitle}>Notification Events:</Text>

        <ScrollView
          ref={ref}
          onContentSizeChange={() => ref.current?.scrollToEnd()}>
          {notificationEvents.length === 0 ? (
            <Text style={styles.noEventsText}>No events yet</Text>
          ) : (
            notificationEvents.map((event, index) => (
              <Text key={index} style={styles.eventText}>
                {event.action} (ID: {event.notificationId}) -{' '}
                {new Date(event.timestamp).toLocaleTimeString()}
              </Text>
            ))
          )}
        </ScrollView>
      </View>
    </View>
  )
}

function isBaklava() {
  return Platform.OS === 'android' && Platform.Version >= 36
}

async function getImgsUri() {
  const [{ localUri: logoLocalUri }] = await Asset.loadAsync(
    require(`./../assets/LiveUpdates/logo.png`),
  )
  const [{ localUri: logoIslandLocalUri }] = await Asset.loadAsync(
    require(`./../assets/LiveUpdates/logo-island.png`),
  )

  return {
    logo: logoLocalUri ?? undefined,
    logoIsland: logoIslandLocalUri ?? undefined,
  }
}

const requestNotificationsPermission = async () => {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS,
      {
        title: 'Notifications Permission',
        message: 'Expo Live Updates Example needs access to the notifications',
        buttonNeutral: 'Ask Me Later',
        buttonNegative: 'Cancel',
        buttonPositive: 'OK',
      },
    )
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      console.log('Notifications permission granted')
    } else {
      console.log('Notifications permission denied')
    }
  } catch (err) {
    console.warn(err)
  }
}

const styles = StyleSheet.create({
  disabledInput: {
    backgroundColor: '#ECECEC',
    borderColor: '#DEDEDE',
    color: '#808080',
  },
  eventText: {
    fontSize: 14,
    marginBottom: 5,
  },
  eventsContainer: {
    backgroundColor: '#f0f0f0',
    borderRadius: 8,
    height: 200,
    padding: 16,
  },
  eventsTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  input: {
    borderColor: '#808080',
    borderRadius: 10,
    borderWidth: 1,
    height: 44,
    padding: 10,
  },
  inputContainer: {
    gap: 6,
    width: '100%',
  },
  label: {
    fontSize: 16,
  },
  labelWithSwitch: {
    alignItems: 'flex-end',
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  manageInput: {
    flex: 1,
  },
  manageUpdatesContainer: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: 12,
  },
  noEventsText: {
    color: '#666',
    fontStyle: 'italic',
  },
  screenContainer: {
    display: 'flex',
    flex: 1,
    gap: 42,
    justifyContent: 'center',
    padding: 24,
  },
  sectionContainer: {
    gap: 12,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
  },
  verticalButtonsContainer: {
    marginTop: 16,
    paddingHorizontal: '20%',
  },
})
