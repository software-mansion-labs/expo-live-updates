import RNDateTimePicker from "@react-native-community/datetimepicker";
import { useEffect, useState } from "react";
import {
  Button,
  Keyboard,
  Platform,
  StyleSheet,
  Switch,
  Text,
  TextInput,
  View,
} from "react-native";
import {
  LiveActivityConfig,
  LiveActivityState,
  startForegroundService,
  stopForegroundService,
  updateForegroundService,
} from "expo-live-updates";
import { Asset } from "expo-asset";

const toggle = (previousState: boolean) => !previousState;

export default function CreateLiveUpdatesScreen() {
  const [activityId, setActivityID] = useState<string | null>();
  const [title, onChangeTitle] = useState("Title");
  const [backgroundColor, setBackgroundColor] = useState("red");
  const [subtitle, onChangeSubtitle] = useState("This is a subtitle");
  const [imageUri, setImageUri] = useState<string>();
  const [iconImageUri, setIconImageUri] = useState<string>();
  const [date, setDate] = useState(new Date());
  const [isTimerTypeDigital, setTimerTypeDigital] = useState(false);
  const [passSubtitle, setPassSubtitle] = useState(true);
  const [passImage, setPassImage] = useState(true);
  const [passIconImage, setPassIconImage] = useState(true);
  const [passDate, setPassDate] = useState(true);

  useEffect(()=> {
    const loadImages = async () => {
      const images = await getImgsUri()
      setImageUri(images.logo)
      setIconImageUri(images.logoIsland)
    }

    loadImages()

  }, [])

  const getState = (): LiveActivityState =>({
      title,
      subtitle: passSubtitle ? subtitle : undefined,
      date: passDate ? date.getTime() : undefined,
      imageName: passImage ? imageUri: undefined,
      dynamicIslandImageName: passIconImage ? iconImageUri : undefined,
    });

  const startActivity = async () => {
    Keyboard.dismiss();

    try {
            const activityConfig: LiveActivityConfig = {
        backgroundColor,
      };
      const id = startForegroundService(getState(), activityConfig);

      //  {
      //   ...activityConfig,
      //   timerType: isTimerTypeDigital ? 'digital' : 'circular',
      // }
      if (id) setActivityID(id);
    } catch (e) {
      console.error("Starting Live Update failed! " + e);
    }
  };

  const stopActivity = () => {

    try {
      // activityId && LiveActivity.stopActivity(activityId, state)
      activityId && stopForegroundService();
      setActivityID(null);
    } catch (e) {
      console.error("Stopping activity failed! " + e);
    }
  };

  const updateActivity = () => {
    try {
      // activityId && LiveActivity.updateActivity(activityId, state)
      activityId && updateForegroundService(getState());
    } catch (e) {
      console.error("Updating activity failed! " + e);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.label}>Set Live Activity title:</Text>
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
        <Switch onValueChange={() => setPassIconImage(toggle)} value={passIconImage} />
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
      {Platform.OS === "ios" && (
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
                  date && setDate(date);
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
          title="Start Update"
          onPress={startActivity}
          disabled={title === "" || !!activityId}
        />
        <Button
          title="Stop Update"
          onPress={stopActivity}
          disabled={!activityId}
        />
        <Button title="Update Update" disabled={!activityId} onPress={updateActivity} />
      </View>
    </View>
  );
}


async function getImgsUri() {
  const [{ localUri: logoLocalUri }] = await Asset.loadAsync(require(`./../assets/liveActivity/logo.png`));
  const [{ localUri: logoIslandLocalUri }] = await Asset.loadAsync(require(`./../assets/liveActivity/logo-island.png`));

  return {logo: logoLocalUri ?? undefined, logoIsland: logoIslandLocalUri ?? undefined}
}

// const activityConfig: LiveActivityConfig = {
//   backgroundColor: '001A72',
//   // titleColor: 'EBEBF0',
//   // subtitleColor: '#FFFFFF75',
//   // progressViewTint: '38ACDD',
//   // progressViewLabelColor: '#FFFFFF',
//   // deepLinkUrl: '/dashboard',
// }

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  timerControlsContainer: {
    flexDirection: "row",
    marginTop: 15,
    marginBottom: 15,
    width: "90%",
    alignItems: "center",
    justifyContent: "center",
  },
  buttonsContainer: {
    padding: 30,
    gap: 15,
  },
  label: {
    width: "90%",
    fontSize: 17,
  },
  labelWithSwitch: {
    flexDirection: "row",
    width: "90%",
    paddingEnd: 15,
  },
  input: {
    height: 45,
    width: "90%",
    marginVertical: 12,
    borderWidth: 1,
    borderColor: "gray",
    borderRadius: 10,
    padding: 10,
  },
  diabledInput: {
    height: 45,
    width: "90%",
    margin: 12,
    borderWidth: 1,
    borderColor: "#DEDEDE",
    backgroundColor: "#ECECEC",
    color: "gray",
    borderRadius: 10,
    padding: 10,
  },
  timerCheckboxContainer: {
    alignItems: "flex-start",
    width: "90%",
    justifyContent: "center",
  },
});
