import { useMemo } from 'react'
import { StyleSheet, TextInput, View } from 'react-native'
import type { CustomLabelProps } from './CustomLabel'
import CustomLabel from './CustomLabel'

type CustomInputProps = {
  value: string
  onChangeText: (text: string) => void
  labelProps: CustomLabelProps
  placeholder?: string
  numeric?: boolean
  editable?: boolean
}

export default function CustomInput({
  value,
  onChangeText,
  labelProps,
  placeholder,
  numeric,
  editable,
}: CustomInputProps) {
  const editableText = useMemo(() => {
    const switchValue = labelProps.switchProps?.value
    return editable !== undefined ? editable : (switchValue ?? true)
  }, [editable, labelProps.switchProps])

  return (
    <View style={styles.container}>
      <CustomLabel {...labelProps} />
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
