import RNDateTimePicker from '@react-native-community/datetimepicker'
import { Asset } from 'expo-asset'
import {
  startForegroundService,
  stopForegroundService,
  updateForegroundService,
} from 'expo-live-updates'
import { LiveUpdateConfig, LiveUpdateState } from 'expo-live-updates/types'
import { useEffect, useState } from 'react'
import {
  Button,
  Keyboard,
  Platform,
  StyleSheet,
  Switch,
  Text,
  TextInput,
  View,
} from 'react-native'

const toggle = (previousState: boolean) => !previousState

export default function CreateLiveUpdatesScreen() {
  const [title, onChangeTitle] = useState('Title')
  const [backgroundColor, setBackgroundColor] = useState('red')
  const [subtitle, onChangeSubtitle] = useState('This is a subtitle')
  const [imageUri, setImageUri] = useState<string>()
  const [iconImageUri, setIconImageUri] = useState<string>()
  const [date, setDate] = useState(new Date())
  const [isTimerTypeDigital, setTimerTypeDigital] = useState(false)
  const [passSubtitle, setPassSubtitle] = useState(true)
  const [passImage, setPassImage] = useState(true)
  const [passIconImage, setPassIconImage] = useState(true)
  const [passDate, setPassDate] = useState(true)

  useEffect(() => {
    const loadImages = async () => {
      const images = await getImgsUri()
      setImageUri(images.logo)
      setIconImageUri(images.logoIsland)
    }

    loadImages()
  }, [])

  const getState = (): LiveUpdateState => ({
    title,
    subtitle: passSubtitle ? subtitle : undefined,
    date: passDate ? date.getTime() : undefined,
    imageName: passImage ? imageUri : undefined,
    dynamicIslandImageName: passIconImage ? iconImageUri : undefined,
  })

  const startLiveUpdate = async () => {
    Keyboard.dismiss()

    try {
      const liveUpdateConfig: LiveUpdateConfig = {
        backgroundColor,
      }
      startForegroundService(getState(), liveUpdateConfig)
    } catch (e) {
      console.error('Starting Live Update failed! ' + e)
    }
  }

  const stopLiveUpdate = () => {
    try {
      stopForegroundService()
    } catch (e) {
      console.error('Stopping live update failed! ' + e)
    }
  }

  const updateLiveUpdate = () => {
    try {
      updateForegroundService(getState())
    } catch (e) {
      console.error('Updating live update failed! ' + e)
    }
  }

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
        <Text style={styles.label}>
          Set Live Update background color (hex):
        </Text>
      </View>
      <TextInput
        style={styles.input}
        onChangeText={setBackgroundColor}
        autoCapitalize="none"
        placeholder="Live Update background color"
        value={backgroundColor}
      />
      {Platform.OS === 'ios' && (
        <>
          <View style={styles.labelWithSwitch}>
            <Text style={styles.label}>Set Live Update timer:</Text>
            <Switch
              onValueChange={() => setPassDate(toggle)}
              value={passDate}
            />
          </View>
          <View style={styles.timerControlsContainer}>
            {passDate && (
              <RNDateTimePicker
                value={date}
                mode="time"
                onChange={(event, date) => {
                  date && setDate(date)
                }}
                minimumDate={new Date(Date.now() + 60 * 1000)}
              />
            )}
          </View>
          <View style={styles.labelWithSwitch}>
            <Text style={styles.label}>Timer shown as text:</Text>
            <Switch
              onValueChange={setTimerTypeDigital}
              value={isTimerTypeDigital}
            />
          </View>
        </>
      )}
      <View style={styles.buttonsContainer}>
        <Button
          title="Start Live Update"
          onPress={startLiveUpdate}
          disabled={title === ''}
        />
        <Button title="Stop Live Update" onPress={stopLiveUpdate} />
        <Button title="Update Live Update" onPress={updateLiveUpdate} />
      </View>
    </View>
  )
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

// const liveUpdateConfig: LiveUpdateConfig = {
//   backgroundColor: '001A72',
//   // titleColor: 'EBEBF0',
//   // subtitleColor: '#FFFFFF75',
//   // progressViewTint: '38ACDD',
//   // progressViewLabelColor: '#FFFFFF',
//   // deepLinkUrl: '/dashboard',
// }

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
    color: 'gray',
    height: 45,
    margin: 12,
    padding: 10,
    width: '90%',
  },
  input: {
    borderColor: 'gray',
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
  timerCheckboxContainer: {
    alignItems: 'flex-start',
    justifyContent: 'center',
    width: '90%',
  },
  timerControlsContainer: {
    alignItems: 'center',
    flexDirection: 'row',
    justifyContent: 'center',
    marginBottom: 15,
    marginTop: 15,
    width: '90%',
  },
})
