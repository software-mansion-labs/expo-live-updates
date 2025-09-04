import { useEvent } from "expo";
import ExpoLiveUpdates from "expo-live-updates";
import { initLiveUpdates } from "expo-live-updates/ExpoLiveUpdatesModule";
import { Button, SafeAreaView, ScrollView, Text, View } from "react-native";

export default function App() {
  initLiveUpdates();
  const onChangePayload = useEvent(ExpoLiveUpdates, "onChange");
  const startService = () => {
    console.log(ExpoLiveUpdates.startService());
  };

  const stopService = () => {
    ExpoLiveUpdates.stopService();
  };

  const updateNotification = () => {
    ExpoLiveUpdates.updateNotification();
  };

  const updateNotificationInterval = () => {
    ExpoLiveUpdates.updateNotificationInterval();
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView style={styles.container}>
        <Text style={styles.header}>Live Updates</Text>
        <Group name="Foreground service">
          <View style={styles.buttons}>
            <Button title="Start Service" onPress={startService} />
            <Button title="Stop Service" onPress={stopService} />
            <Button title="updateNotification" onPress={updateNotification} />
            <Button title="updateNotification every 5s" onPress={updateNotificationInterval} />
          </View>
        </Group>
      </ScrollView>
    </SafeAreaView>
  );
}

function Group(props: { name: string; children: React.ReactNode }) {
  return (
    <View style={styles.group}>
      <Text style={styles.groupHeader}>{props.name}</Text>
      {props.children}
    </View>
  );
}

const styles = {
  header: {
    fontSize: 30,
    margin: 20,
  },
  groupHeader: {
    fontSize: 20,
    marginBottom: 20,
  },
  group: {
    margin: 20,
    backgroundColor: "#fff",
    borderRadius: 10,
    padding: 20,
  },
  container: {
    flex: 1,
    backgroundColor: "#eee",
  },
  view: {
    flex: 1,
    height: 200,
  },
  buttons: {
    gap: "15",
  },
};
