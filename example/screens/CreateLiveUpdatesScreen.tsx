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
import { useEffect, useState } from 'react'
import {
  Button,
  Keyboard,
  PermissionsAndroid,
  Platform,
  StyleSheet,
  Switch,
  Text,
  TextInput,
  View,
} from 'react-native'
import * as Clipboard from 'expo-clipboard'

const toggle = (previousState: boolean) => !previousState

export default function CreateLiveUpdatesScreen() {
  const [title, onChangeTitle] = useState('Title')
  const [subtitle, onChangeSubtitle] = useState('This is a subtitle')
  const [imageUri, setImageUri] = useState<string>()
  const [iconImageUri, setIconImageUri] = useState<string>()
  const [backgroundColor, setBackgroundColor] = useState('red')
  const [shortCriticalText, setShortCriticalText] = useState('SWM')

  const [passSubtitle, setPassSubtitle] = useState(true)
  const [passImage, setPassImage] = useState(true)
  const [passIconImage, setPassIconImage] = useState(true)
  const [passShortCriticalText, setPassShortCriticalText] = useState(true)

  const [notificationId, setNotificationId] = useState<number | undefined>(
    undefined,
  )
  const [token, setToken] = useState<string | undefined>(undefined)
  const [notificationEvents, setNotificationEvents] = useState<
    NotificationStateChangeEvent[]
  >([])

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

  const getConfig = (): LiveUpdateConfig => ({
    backgroundColor,
    shortCriticalText: passShortCriticalText ? shortCriticalText : undefined,
  })

  const handleStartLiveUpdate = () => {
    Keyboard.dismiss()

    try {
      const id = startLiveUpdate(getState(), getConfig())
      if (id) {
        setNotificationId(id)
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
        setNotificationId(undefined)
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
        updateLiveUpdate(notificationId, getState(), getConfig())
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
    <View style={styles.container}>
      <Text style={styles.label}>Set Live live update title:</Text>
      <TextInput
        style={styles.input}
        onChangeText={onChangeTitle}
        placeholder="Live Update title"
        value={title}
      />
      <View style={styles.labelWithSwitch}>
        <Text style={styles.label}>Set Live Update subtitle:</Text>
        <Switch
          onValueChange={() => setPassSubtitle(toggle)}
          value={passSubtitle}
        />
      </View>
      <TextInput
        style={passSubtitle ? styles.input : styles.diabledInput}
        onChangeText={onChangeSubtitle}
        placeholder="Live Update title"
        value={subtitle}
        editable={passSubtitle}
      />
      <View style={styles.labelWithSwitch}>
        <Text style={styles.label}>Set Live Update image:</Text>
        <Switch onValueChange={() => setPassImage(toggle)} value={passImage} />
      </View>
      <View style={styles.labelWithSwitch}>
        <Text style={styles.label}>Set Live Update icon image:</Text>
        <Switch
          onValueChange={() => setPassIconImage(toggle)}
          value={passIconImage}
        />
      </View>

      <View style={styles.labelWithSwitch}>
        <Text style={styles.label}>Set Live Update short critical text:</Text>
        <Switch
          onValueChange={() => setPassShortCriticalText(toggle)}
          value={passShortCriticalText}
        />
      </View>
      <TextInput
        style={passShortCriticalText ? styles.input : styles.diabledInput}
        onChangeText={setShortCriticalText}
        autoCapitalize="none"
        placeholder="Live Update short critical text"
        value={shortCriticalText}
        editable={passShortCriticalText}
      />

      {!isBaklava() && (
        <>
          <View style={styles.labelWithSwitch}>
            <Text style={styles.label}>
              Set Live Update background color (hex) (for SDK &lt; 16 Baklava):
            </Text>
          </View>
          <TextInput
            style={styles.input}
            onChangeText={setBackgroundColor}
            autoCapitalize="none"
            placeholder="Live Update background color"
            value={backgroundColor}
          />
        </>
      )}
      <View style={styles.buttonsContainer}>
        <Button
          title="Start"
          onPress={handleStartLiveUpdate}
          disabled={title === ''}
        />
        <Button title="Stop" onPress={handleStopLiveUpdate} />
        <Button title="Update" onPress={handleUpdateLiveUpdate} />
        <Button title="Copy Push Token" onPress={handleCopyPushToken} />
      </View>

      <View style={styles.eventsContainer}>
        <Text style={styles.eventsTitle}>Notification Events:</Text>
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
  buttonsContainer: {
    gap: 15,
    padding: 30,
  },
  container: {
    alignItems: 'center',
    flex: 1,
    justifyContent: 'center',
  },
  diabledInput: {
    backgroundColor: '#ECECEC',
    borderColor: '#DEDEDE',
    borderRadius: 10,
    borderWidth: 1,
    color: '#808080',
    height: 45,
    margin: 12,
    padding: 10,
    width: '90%',
  },
  eventText: {
    fontSize: 14,
    marginBottom: 5,
  },
  eventsContainer: {
    backgroundColor: '#f0f0f0',
    borderRadius: 8,
    margin: 20,
    padding: 15,
    width: '90%',
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
    height: 45,
    marginVertical: 12,
    padding: 10,
    width: '90%',
  },
  label: {
    fontSize: 17,
    width: '90%',
  },
  labelWithSwitch: {
    flexDirection: 'row',
    paddingEnd: 15,
    width: '90%',
  },
  noEventsText: {
    color: '#666',
    fontStyle: 'italic',
  },
})
