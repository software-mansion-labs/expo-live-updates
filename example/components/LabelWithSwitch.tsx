import type { Dispatch, SetStateAction } from 'react'
import type { SwitchProps } from 'react-native'
import { StyleSheet, Switch, Text, View } from 'react-native'

export type LabelWithSwitchProps = {
  label: string
  switchProps?: Pick<SwitchProps, 'value' | 'disabled'> & {
    setValue: Dispatch<SetStateAction<boolean>>
  }
}

const toggle = (previousState: boolean) => !previousState

export default function LabelWithSwitch({
  label,
  switchProps,
}: LabelWithSwitchProps) {
  return (
    <View style={styles.container}>
      <Text style={styles.text}>{label}</Text>
      {switchProps && (
        <Switch
          onValueChange={() => switchProps.setValue(toggle)}
          {...switchProps}
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
