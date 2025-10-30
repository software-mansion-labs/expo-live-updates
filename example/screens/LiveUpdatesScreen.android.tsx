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
  Text,
  View,
} from 'react-native'
import * as Clipboard from 'expo-clipboard'
import { SafeAreaView } from 'react-native-safe-area-context'
import { Asset } from 'expo-asset'
import CustomInput from '../components/CustomInput'
import CustomLabel from '../components/CustomLabel'

export default function LiveUpdatesScreen() {
  const [title, onChangeTitle] = useState('This is a title')
  const [text, onChangeText] = useState('This is a text')
  const [subText, onChangeSubText] = useState('SWM')
  const [deepLinkUrl, setDeepLinkUrl] = useState('/Test')
  const [imageUri, setImageUri] = useState<string>()
  const [iconImageUri, setIconImageUri] = useState<string>()
  const [backgroundColor, setBackgroundColor] = useState('red')
  const [shortCriticalText, setShortCriticalText] = useState('SWM')
  const [showTime, setShowTime] = useState(false)

  const [hours, setHours] = useState('0')
  const [minutes, setMinutes] = useState('10')
  const [isPast, setIsPast] = useState(false)

  const [passText, setPassText] = useState(true)
  const [passImage, setPassImage] = useState(true)
  const [passIconImage, setPassIconImage] = useState(true)
  const [passDeepLink, setPassDeepLink] = useState(true)
  const [passShortCriticalText, setPassShortCriticalText] = useState(true)
  const [passSubText, setPassSubText] = useState(true)
  const [passTime, setPassTime] = useState(false)

  const [notificationIdString, setNotificationIdString] = useState<string>('')
  const [token, setToken] = useState<string | undefined>(undefined)
  const [notificationEvents, setNotificationEvents] = useState<
    NotificationStateChangeEvent[]
  >([])
  const [passProgress, setPassProgress] = useState(false)
  const [progressMax, setProgressMax] = useState('100')
  const [progressValue, setProgressValue] = useState('50')
  const [progressIndeterminate, setProgressIndeterminate] = useState(false)
  const [passPoints, setPassPoints] = useState(false)
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
    text: passText ? text : undefined,
    subText: passSubText ? subText : undefined,
    imageName: passImage ? imageUri : undefined,
    dynamicIslandImageName: passIconImage ? iconImageUri : undefined,
    progress: passProgress
      ? {
          max: isNaN(parseInt(progressMax)) ? 100 : parseInt(progressMax),
          progress: isNaN(parseInt(progressValue))
            ? 0
            : parseInt(progressValue),
          indeterminate: progressIndeterminate,
          points: passPoints
            ? [
                { position: 30, color: 'red' },
                { position: 70, color: 'blue' },
              ]
            : undefined,
          segments: passSegments
            ? [
                { length: 30, color: 'red' },
                { length: 300, color: 'blue' },
              ]
            : undefined,
        }
      : undefined,
    shortCriticalText: passShortCriticalText ? shortCriticalText : undefined,
    showTime,
    time: passTime ? getTimeTimestamp() : undefined,
  })

  const getTimeTimestamp = () => {
    const parsedHours = parseInt(hours)
    const parsedMinutes = parseInt(minutes)

    const hrs = isNaN(parsedHours) ? 0 : parsedHours
    const mins = isNaN(parsedMinutes) ? 0 : parsedMinutes

    const diffInMs = (hrs * 60 + mins) * 60 * 1000

    const nowTimestamp = new Date().valueOf()
    return isPast ? nowTimestamp - diffInMs : nowTimestamp + diffInMs
  }

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
          <CustomInput
            value={title}
            onValueChange={onChangeTitle}
            label="Title:"
            placeholder="Live Update title"
          />
          <CustomInput
            value={text}
            onValueChange={onChangeText}
            label="Text:"
            placeholder="Text"
            switchProps={{
              value: passText,
              setValue: setPassText,
            }}
          />
          <CustomInput
            value={subText}
            onValueChange={onChangeSubText}
            label="SubText:"
            placeholder="SubText"
            switchProps={{ value: passSubText, setValue: setPassSubText }}
          />
          <CustomLabel
            label="Image"
            switchProps={{ value: passImage, setValue: setPassImage }}
          />
          <CustomLabel
            label="Icon image"
            switchProps={{ value: passIconImage, setValue: setPassIconImage }}
          />
          <CustomLabel
            label="Show time"
            switchProps={{ value: showTime, setValue: setShowTime }}
          />
          {showTime && (
            <View style={styles.subSectionContainer}>
              <CustomLabel
                label="Time"
                switchProps={{ value: passTime, setValue: setPassTime }}
              />
              {passTime && (
                <>
                  <View style={styles.inputsRow}>
                    <CustomInput
                      value={hours}
                      onValueChange={setHours}
                      label="Hours:"
                      keyboardType="numeric"
                    />
                    <CustomInput
                      value={minutes}
                      onValueChange={setMinutes}
                      label="Minutes:"
                      keyboardType="numeric"
                    />
                  </View>
                  <CustomLabel
                    label="Past"
                    switchProps={{
                      value: isPast,
                      setValue: setIsPast,
                    }}
                  />
                </>
              )}
            </View>
          )}

          <CustomInput
            value={shortCriticalText}
            onValueChange={setShortCriticalText}
            label="Short critical text:"
            placeholder="Live Update short critical text"
            switchProps={{
              value: passShortCriticalText,
              setValue: setPassShortCriticalText,
            }}
          />
          <CustomInput
            value={deepLinkUrl}
            onValueChange={setDeepLinkUrl}
            label="Deep link URL:"
            placeholder="Deep link URL (e.g., /Test)"
            switchProps={{
              value: passDeepLink,
              setValue: setPassDeepLink,
            }}
          />

          <View style={styles.subSectionContainer}>
            <CustomLabel
              label="Progress"
              switchProps={{ value: passProgress, setValue: setPassProgress }}
            />

            {passProgress && (
              <>
                <View style={styles.inputsRow}>
                  <CustomInput
                    value={progressValue}
                    onValueChange={setProgressValue}
                    label="Progress value:"
                    placeholder="Value, e.g. 50"
                    keyboardType="numeric"
                    editable={!progressIndeterminate}
                  />
                  <CustomInput
                    value={progressMax}
                    onValueChange={setProgressMax}
                    label="Progress max:"
                    placeholder="Maximum, default 100"
                    keyboardType="numeric"
                    editable={!(progressIndeterminate || passSegments)}
                  />
                </View>
                <CustomLabel
                  label="Indeterminate progress"
                  switchProps={{
                    value: progressIndeterminate,
                    setValue: setProgressIndeterminate,
                  }}
                />
                <CustomLabel
                  label="Points"
                  switchProps={{
                    value: passPoints,
                    setValue: setPassPoints,
                  }}
                />
                <CustomLabel
                  label="Segments"
                  switchProps={{
                    value: passSegments,
                    setValue: setPassSegments,
                  }}
                />
              </>
            )}

            {!isBaklava() && (
              <CustomInput
                value={backgroundColor}
                onValueChange={setBackgroundColor}
                label="Background color (hex) (for SDK &lt; 16 Baklava):"
                placeholder="Background color"
              />
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
          <CustomInput
            value={notificationIdString}
            onValueChange={setNotificationIdString}
            label="Live Update ID:"
            placeholder="Live Update ID"
            keyboardType="numeric"
          />
          <View style={styles.buttonsContainer}>
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
    paddingHorizontal: '20%',
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
  inputsRow: {
    flexDirection: 'row',
    gap: 12,
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
    gap: 14,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
  },
  subSectionContainer: {
    gap: 6,
  },
})
