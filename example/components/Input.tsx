import { useMemo } from 'react'
import type { TextInputProps } from 'react-native'
import { StyleSheet, TextInput, View } from 'react-native'
import type { LabelWithSwitchProps } from './LabelWithSwitch'
import LabelWithSwitch from './LabelWithSwitch'

type InputProps = {
  labelProps: LabelWithSwitchProps
  numeric?: boolean
} & Pick<TextInputProps, 'value' | 'onChangeText' | 'placeholder' | 'editable'>

export default function Input({
  value,
  onChangeText,
  labelProps,
  placeholder,
  numeric,
  editable,
}: InputProps) {
  const editableText = useMemo(() => {
    if (editable !== undefined) {
      return editable
    } else {
      const switchValue = labelProps.switchProps?.value
      return switchValue ?? true
    }
  }, [editable, labelProps.switchProps])

  return (
    <View style={styles.container}>
      <LabelWithSwitch {...labelProps} />
      <TextInput
        style={[styles.input, !editableText && styles.disabledInput]}
        value={value}
        onChangeText={onChangeText}
        placeholder={placeholder}
        editable={editableText}
        keyboardType={numeric ? 'numeric' : undefined}
      />
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    gap: 6,
  },
  disabledInput: {
    backgroundColor: '#ECECEC',
    borderColor: '#DEDEDE',
    color: '#808080',
  },
  input: {
    borderColor: '#808080',
    borderRadius: 10,
    borderWidth: 1,
    height: 44,
    padding: 10,
  },
})
