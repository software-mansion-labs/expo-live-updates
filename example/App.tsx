import {init, LiveActivityState, startForegroundService, stopForegroundService, updateForegroundService} from "expo-live-updates";
import { useEffect } from "react";
import { Button, SafeAreaView, ScrollView, Text, View } from "react-native";

export default function App() {

  const startState: LiveActivityState =  {
  title: 'Test title 2',
  subtitle: 'test subtitle',
  date: 1725794623,
  imageName: 'someimagename',
  dynamicIslandImageName: 'someimagename2',
}
  const updateState: LiveActivityState =  {
  title: 'Test updated title 3',
  subtitle: 'test updated subtitle',
  date: 1599564223,
  imageName: 'someimagename',
  dynamicIslandImageName: 'someimagename2',
}
useEffect(()=> {    init()
}, [])

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView style={styles.container}>
        <Text style={styles.header}>Live Updates</Text>
        <Group name="Foreground service">
          <View style={styles.buttons}>
            <Button title="Start Service" onPress={() => startForegroundService(startState)} />
            <Button title="Stop Service" onPress={() => stopForegroundService()} />
            <Button title="updateForegroundService" onPress={() => updateForegroundService(updateState)} />
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
