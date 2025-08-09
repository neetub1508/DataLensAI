export const SUPPORTED_LOCALES = {
  EN: 'en',
  ES: 'es',
  FR: 'fr',
  DE: 'de',
} as const

export const DEFAULT_LOCALE = SUPPORTED_LOCALES.EN

export type LocaleType = typeof SUPPORTED_LOCALES[keyof typeof SUPPORTED_LOCALES]

export const LOCALE_NAMES = {
  [SUPPORTED_LOCALES.EN]: 'English',
  [SUPPORTED_LOCALES.ES]: 'Español',
  [SUPPORTED_LOCALES.FR]: 'Français',
  [SUPPORTED_LOCALES.DE]: 'Deutsch',
} as const