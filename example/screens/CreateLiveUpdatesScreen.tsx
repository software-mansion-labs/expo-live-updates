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
import { SafeAreaView } from 'react-native-safe-area-context'
import { Asset } from 'expo-asset'

const toggle = (previousState: boolean) => !previousState

export default function CreateLiveUpdatesScreen() {
  const [title, onChangeTitle] = useState('This is a title')
  const [subtitle, onChangeSubtitle] = useState('This is a subtitle')
  const [deepLinkUrl, setDeepLinkUrl] = useState('/Test')
  const [imageUri, setImageUri] = useState<string>()
  const [iconImageUri, setIconImageUri] = useState<string>()
  const [backgroundColor, setBackgroundColor] = useState('red')
  const [shortCriticalText, setShortCriticalText] = useState('SWM')

  const [passSubtitle, setPassSubtitle] = useState(true)
  const [passImage, setPassImage] = useState(true)
  const [passIconImage, setPassIconImage] = useState(true)
  const [passDeepLink, setPassDeepLink] = useState(true)
  const [passShortCriticalText, setPassShortCriticalText] = useState(true)

  const [notificationIdString, setNotificationIdString] = useState<string>('')
  const [token, setToken] = useState<string | undefined>(undefined)
  const [notificationEvents, setNotificationEvents] = useState<
    NotificationStateChangeEvent[]
  >([])
  const [passProgress, setPassProgress] = useState(false)
  const [progressMax, setProgressMax] = useState('100')
  const [progressValue, setProgressValue] = useState('50')
  const [progressIndeterminate, setProgressIndeterminate] = useState(false)
  const [passSegments, setPassSegments] = useState(false)

  const scrollViewRef = useRef<ScrollView>(null)

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
    progress: passProgress
      ? {
          max: isNaN(parseInt(progressMax)) ? 100 : parseInt(progressMax),
          progress: isNaN(parseInt(progressValue))
            ? 0
            : parseInt(progressValue),
          indeterminate: progressIndeterminate,
          segments: passSegments
            ? [
                { value: 30, color: 'red' },
                { value: 70, color: 'blue' },
              ]
            : undefined,
        }
      : undefined,
    shortCriticalText: passShortCriticalText ? shortCriticalText : undefined,
  })

  const getConfig = (): LiveUpdateConfig => ({
    backgroundColor,
    deepLinkUrl: passDeepLink ? deepLinkUrl : undefined,
  })

  const handleStartLiveUpdate = () => {
    Keyboard.dismiss()

    try {
      const id = startLiveUpdate(getState(), getConfig())
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
      console.error('Stopping Live Update failed! ' + e)
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
      console.error('Updating Live Update failed! ' + e)
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
    <SafeAreaView>
      <ScrollView contentContainerStyle={styles.screenContainer}>
        <View style={styles.sectionContainer}>
          <View style={styles.inputContainer}>
            <Text style={styles.label}>Title</Text>
            <TextInput
              style={styles.input}
              onChangeText={onChangeTitle}
              placeholder="Live Update title"
              value={title}
            />
          </View>

          <View style={styles.inputContainer}>
            <View style={styles.labelWithSwitch}>
              <Text style={styles.label}>Subtitle:</Text>
              <Switch
                onValueChange={() => setPassSubtitle(toggle)}
                value={passSubtitle}
              />
            </View>
            <TextInput
              style={[styles.input, !passSubtitle && styles.disabledInput]}
              onChangeText={onChangeSubtitle}
              placeholder="Subtitle"
              value={subtitle}
              editable={passSubtitle}
            />
          </View>

          <View style={styles.labelWithSwitch}>
            <Text style={styles.label}>Image:</Text>
            <Switch
              onValueChange={() => setPassImage(toggle)}
              value={passImage}
            />
          </View>

          <View style={styles.labelWithSwitch}>
            <Text style={styles.label}>Icon image:</Text>
            <Switch
              onValueChange={() => setPassIconImage(toggle)}
              value={passIconImage}
            />
          </View>

          <View style={styles.inputContainer}>
            <View style={styles.labelWithSwitch}>
              <Text style={styles.label}>Short critical text:</Text>
              <Switch
                onValueChange={() => setPassShortCriticalText(toggle)}
                value={passShortCriticalText}
              />
            </View>
            <TextInput
              style={[
                styles.input,
                !passShortCriticalText && styles.disabledInput,
              ]}
              onChangeText={setShortCriticalText}
              placeholder="Live Update short critical text"
              value={shortCriticalText}
              editable={passShortCriticalText}
            />
          </View>

          <View style={styles.labelWithSwitch}>
            <Text style={styles.label}>Deep link URL:</Text>
            <Switch
              onValueChange={() => setPassDeepLink(toggle)}
              value={passDeepLink}
            />
          </View>
          <TextInput
            style={[styles.input, !passDeepLink && styles.disabledInput]}
            onChangeText={setDeepLinkUrl}
            placeholder="Deep link URL (e.g., /Test)"
            value={deepLinkUrl}
            editable={passDeepLink}
            autoCapitalize="none"
          />

          <View style={styles.inputContainer}>
            <View style={styles.labelWithSwitch}>
              <Text style={styles.label}>Progress</Text>
              <Switch
                onValueChange={() => setPassProgress(toggle)}
                value={passProgress}
              />
            </View>

            {passProgress && (
              <>
                <View style={styles.inputsRow}>
                  <View style={styles.inputInRowContainer}>
                    <Text style={styles.label}>Progress value</Text>
                    <TextInput
                      style={[
                        styles.input,
                        progressIndeterminate && styles.disabledInput,
                      ]}
                      onChangeText={setProgressValue}
                      value={progressValue}
                      placeholder="Value, e.g. 50"
                      editable={!progressIndeterminate}
                      keyboardType="numeric"
                    />
                  </View>

                  <View style={styles.inputInRowContainer}>
                    <Text style={styles.label}>Progress max</Text>
                    <TextInput
                      style={[
                        styles.input,
                        (progressIndeterminate || passSegments) &&
                          styles.disabledInput,
                      ]}
                      onChangeText={setProgressMax}
                      value={progressMax}
                      placeholder="Maximum, default 100"
                      editable={!(progressIndeterminate || passSegments)}
                      keyboardType="numeric"
                    />
                  </View>
                </View>

                <View style={styles.labelWithSwitch}>
                  <Text style={styles.label}>Indeterminate progress</Text>
                  <Switch
                    onValueChange={() => setProgressIndeterminate(toggle)}
                    value={progressIndeterminate}
                  />
                </View>

                <View style={styles.labelWithSwitch}>
                  <Text style={styles.label}>Segments</Text>
                  <Switch
                    onValueChange={() => setPassSegments(toggle)}
                    value={passSegments}
                  />
                </View>
              </>
            )}

            {!isBaklava() && (
              <View style={styles.inputContainer}>
                <View style={styles.labelWithSwitch}>
                  <Text style={styles.label}>
                    Background color (hex) (for SDK &lt; 16 Baklava):
                  </Text>
                </View>
                <TextInput
                  style={styles.input}
                  onChangeText={setBackgroundColor}
                  autoCapitalize="none"
                  placeholder="Background color"
                  value={backgroundColor}
                />
              </View>
            )}
          </View>
          <View style={styles.buttonsContainer}>
            <Button
              title="Start"
              onPress={handleStartLiveUpdate}
              disabled={title === ''}
            />
            <Button
              title={token ? 'Copy Push Token' : 'FCM not configured'}
              disabled={token === undefined}
              onPress={handleCopyPushToken}
            />
          </View>
        </View>

        <View style={styles.sectionContainer}>
          <Text style={styles.sectionTitle}>Manage existing Live Update</Text>

          <View style={styles.inputContainer}>
            <Text style={styles.label}>Live Update ID</Text>
            <View style={styles.manageUpdatesContainer}>
              <TextInput
                style={[styles.input, styles.manageUpdatesInput]}
                placeholder="Live Update ID"
                onChangeText={setNotificationIdString}
                value={notificationIdString}
                keyboardType="numeric"
              />
              <Button
                title="Update"
                onPress={handleUpdateLiveUpdate}
                disabled={notificationId === undefined}
              />
              <Button
                title="Stop"
                onPress={handleStopLiveUpdate}
                disabled={notificationId === undefined}
              />
            </View>
          </View>
        </View>

        <View style={styles.eventsContainer}>
          <Text style={styles.eventsTitle}>Notification Events</Text>

          <ScrollView
            ref={scrollViewRef}
            onContentSizeChange={() => scrollViewRef.current?.scrollToEnd()}>
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
      </ScrollView>
    </SafeAreaView>
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
    flexDirection: 'row',
    gap: 12,
    justifyContent: 'center',
    marginTop: 16,
    paddingHorizontal: '20%',
  },
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
  inputInRowContainer: {
    gap: 6,
    width: '50%',
  },
  inputsRow: {
    flexDirection: 'row',
    gap: 12,
  },
  label: {
    fontSize: 16,
  },
  labelWithSwitch: {
    alignItems: 'flex-end',
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  manageUpdatesContainer: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: 12,
  },
  manageUpdatesInput: {
    flex: 1,
  },
  noEventsText: {
    color: '#666',
    fontStyle: 'italic',
  },
  screenContainer: {
    display: 'flex',
    gap: 42,
    padding: 24,
  },
  sectionContainer: {
    gap: 12,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
  },
})
