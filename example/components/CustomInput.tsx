import type { Dispatch, SetStateAction } from 'react'
import { useMemo } from 'react'
import type { KeyboardTypeOptions } from 'react-native'
import { StyleSheet, TextInput, View } from 'react-native'
import CustomLabel from './CustomLabel'

type CustomInputProps = {
  value: string
  onValueChange: (value: string) => void
  label: string
  placeholder?: string
  switchProps?: {
    value: boolean
    setValue: Dispatch<SetStateAction<boolean>>
  }
  keyboardType?: KeyboardTypeOptions
  editable?: boolean
}

export default function CustomInput({
  value,
  onValueChange,
  label,
  placeholder,
  switchProps,
  keyboardType,
  editable,
}: CustomInputProps) {
  const CanEditText = useMemo(() => {
    if (editable !== undefined) {
      return editable
    } else {
      return switchProps ? switchProps.value : true
    }
  }, [editable, switchProps])

  return (
    <View style={styles.inputContainer}>
      <CustomLabel label={label} switchProps={switchProps} />

      <TextInput
        style={[styles.input, !CanEditText && styles.disabledInput]}
        onChangeText={onValueChange}
        placeholder={placeholder}
        value={value}
        editable={CanEditText}
        keyboardType={keyboardType}
      />
    </View>
  )
}

const styles = StyleSheet.create({
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
  inputContainer: {
    flex: 1,
    gap: 6,
  },
})
