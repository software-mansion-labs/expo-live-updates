import type { Dispatch, SetStateAction } from 'react'
import { StyleSheet, Switch, Text, View } from 'react-native'

export type CustomLabelProps = {
  text: string
  switchProps?: {
    value: boolean
    setValue: Dispatch<SetStateAction<boolean>>
    disabled?: boolean
  }
}

const toggle = (previousState: boolean) => !previousState

export default function CustomLabel({ text, switchProps }: CustomLabelProps) {
  return (
    <View style={styles.container}>
      <Text style={styles.text}>{text}</Text>
      {switchProps && (
        <Switch
          onValueChange={() => switchProps.setValue(toggle)}
          value={switchProps.value}
          disabled={switchProps.disabled}
        />
      )}
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    alignItems: 'flex-end',
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  text: {
    fontSize: 16,
  },
})
