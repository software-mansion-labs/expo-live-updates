import { Asset } from 'expo-asset'
import {
  startLiveUpdate,
  stopLiveUpdate,
  updateLiveUpdate,
  getDevicePushTokenAsync,
} from 'expo-live-updates'
import type { LiveUpdateConfig, LiveUpdateState } from 'expo-live-updates/types'
import { useEffect, useMemo, useState } from 'react'
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
  const [backgroundColor, setBackgroundColor] = useState('red')
  const [subtitle, onChangeSubtitle] = useState('This is a subtitle')
  const [imageUri, setImageUri] = useState<string>()
  const [iconImageUri, setIconImageUri] = useState<string>()
  const [passSubtitle, setPassSubtitle] = useState(true)
  const [passImage, setPassImage] = useState(true)
  const [passIconImage, setPassIconImage] = useState(true)
  const [tokenClipboardLoading, setTokenClipboardLoading] = useState(false)
  const [notificationIdString, setNotificationIdString] = useState<string>('')

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
    requestCameraPermission()
  }, [])

  const getState = (): LiveUpdateState => ({
    title,
    subtitle: passSubtitle ? subtitle : undefined,
    // date: passDate ? date.getTime() : undefined,
    date: undefined,
    imageName: passImage ? imageUri : undefined,
    dynamicIslandImageName: passIconImage ? iconImageUri : undefined,
  })

  const handleStartLiveUpdate = () => {
    Keyboard.dismiss()
    console.log('+++++++++++++++++++++++' + Platform.Version)

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
    if (!tokenClipboardLoading) {
      setTokenClipboardLoading(true)
      getDevicePushTokenAsync()
        .then(token => Clipboard.setStringAsync(token ?? 'someting went wrong'))
        .finally(() => setTokenClipboardLoading(false))
    }
  }

  return (
    <View style={styles.container}>
      <Text style={styles.label}>Set Live live update ID:</Text>
      <TextInput
        style={styles.input}
        onChangeText={setNotificationIdString}
        value={notificationIdString}
        keyboardType="numeric"
      />
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
        style={passSubtitle ? styles.input : styles.disabledInput}
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
          disabled={title === '' || notificationId !== undefined}
        />
        <Button
          title="Stop"
          onPress={handleStopLiveUpdate}
          disabled={notificationId === undefined}
        />
        <Button
          title="Update"
          onPress={handleUpdateLiveUpdate}
          disabled={notificationId === undefined}
        />
        <Button
          title="Copy Push Token"
          onPress={handleCopyPushToken}
          disabled={tokenClipboardLoading}
        />
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

// TODO: add POST_PROMOTED_NOTIFICATIONS also
const requestCameraPermission = async () => {
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
  disabledInput: {
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
})
