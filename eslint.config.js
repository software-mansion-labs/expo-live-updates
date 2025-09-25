const js = require('@eslint/js')
const tseslint = require('@typescript-eslint/eslint-plugin')
const tsparser = require('@typescript-eslint/parser')
const react = require('eslint-plugin-react')
const reactNative = require('eslint-plugin-react-native')
const reactHooks = require('eslint-plugin-react-hooks')
const prettier = require('eslint-plugin-prettier')
const prettierConfig = require('eslint-config-prettier')
const globals = require('globals')

module.exports = [
  // Base JavaScript recommended rules
  js.configs.recommended,

  // React and React Native configuration
  {
    files: ['**/*.{js,jsx,ts,tsx}'],
    languageOptions: {
      parser: tsparser,
      parserOptions: {
        ecmaFeatures: { jsx: true },
        projectService: true,
        tsconfigRootDir: __dirname,
      },
      globals: {
        ...globals.es2022,
        ...globals.node,
      },
    },
    plugins: {
      'react': react,
      'react-native': reactNative,
      'react-hooks': reactHooks,
      'prettier': prettier,
      '@typescript-eslint': tseslint,
    },
    settings: {
      'import/resolver': {
        'babel-module': {
          root: ['.'],
          extensions: ['.ts', '.tsx', '.js', '.jsx', '.json'],
          alias: {
            'expo-live-updates': './src/*',
          },
        },
      },
    },
    rules: {
      ...tseslint.configs['recommended-type-checked'].rules,
      // React Native rules
      ...reactNative.configs.all.rules,
      'react-native/no-color-literals': 'off',

      // React Hooks rules
      'react-hooks/rules-of-hooks': 'error',
      'react-hooks/exhaustive-deps': 'error',

      // General rules
      'no-shadow': 'error',
      '@typescript-eslint/no-floating-promises': 'off',

      // TypeScript rules
      '@typescript-eslint/consistent-type-imports': [
        'error',
        { disallowTypeAnnotations: false },
      ],

      // Import order disabled (as per comment in original config)
      'import/order': 'off',

      // Prettier rules
      ...prettierConfig.rules,
      'prettier/prettier': 'error',
    },
    ignores: ["./build/*"],
  },
]
