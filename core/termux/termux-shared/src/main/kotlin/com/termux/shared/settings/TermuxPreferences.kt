package com.termux.shared.settings

import android.content.Context
import android.content.SharedPreferences

class TermuxPreferences private constructor(context: Context) {

  companion object {
    private const val PREFERENCES_NAME = "com.termux_preferences"

    @Volatile private var instance: TermuxPreferences? = null

    fun getInstance(context: Context): TermuxPreferences {
      return instance
        ?: synchronized(this) {
          instance ?: TermuxPreferences(context.applicationContext).also { instance = it }
        }
    }

    object Keys {
      const val FONT_SIZE = "fontsize"
      const val CURRENT_SESSION = "current_session"
      const val BELL_BEHAVIOUR = "bell_behaviour"
      const val CURSOR_BLINK_RATE = "cursor_blink_rate"
      const val CURSOR_STYLE = "cursor_style"
      const val TERMINAL_TRANSCRIPT_ROWS = "terminal_transcript_rows"
      const val TERMINAL_MARGIN_HORIZONTAL = "terminal_margin_horizontal"
      const val TERMINAL_MARGIN_VERTICAL = "terminal_margin_vertical"
      const val EXTRA_KEYS_STYLE = "extra_keys_style"
      const val SOFT_KEYBOARD_ENABLED = "soft_keyboard_enabled"
      const val CTRL_WORKAROUND = "ctrl_workaround"
      const val USE_BLACK_UI = "use_black_ui"
      const val ENFORCE_CHAR_BASED_INPUT = "enforce_char_based_input"
      const val BACK_KEY_BEHAVIOUR = "back_key_behaviour"
      const val TERMINAL_CURSOR_KEYS = "terminal_cursor_keys"
    }

    object Defaults {
      const val FONT_SIZE = 14
      const val BELL_BEHAVIOUR = "vibrate"
      const val CURSOR_BLINK_RATE = 0
      const val CURSOR_STYLE = "block"
      const val TERMINAL_TRANSCRIPT_ROWS = 2000
      const val TERMINAL_MARGIN_HORIZONTAL = 0
      const val TERMINAL_MARGIN_VERTICAL = 0
      const val EXTRA_KEYS_STYLE = "default"
      const val SOFT_KEYBOARD_ENABLED = true
      const val CTRL_WORKAROUND = true
      const val USE_BLACK_UI = false
      const val ENFORCE_CHAR_BASED_INPUT = true
      const val BACK_KEY_BEHAVIOUR = "back"
      const val TERMINAL_CURSOR_KEYS = "normal"
    }
  }

  private val preferences: SharedPreferences =
    context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

  var fontSize: Int
    get() = preferences.getInt(Keys.FONT_SIZE, Defaults.FONT_SIZE)
    set(value) = preferences.edit().putInt(Keys.FONT_SIZE, value).apply()

  var currentSession: Int
    get() = preferences.getInt(Keys.CURRENT_SESSION, 0)
    set(value) = preferences.edit().putInt(Keys.CURRENT_SESSION, value).apply()

  var bellBehaviour: String
    get() =
      preferences.getString(Keys.BELL_BEHAVIOUR, Defaults.BELL_BEHAVIOUR) ?: Defaults.BELL_BEHAVIOUR
    set(value) = preferences.edit().putString(Keys.BELL_BEHAVIOUR, value).apply()

  var cursorBlinkRate: Int
    get() = preferences.getInt(Keys.CURSOR_BLINK_RATE, Defaults.CURSOR_BLINK_RATE)
    set(value) = preferences.edit().putInt(Keys.CURSOR_BLINK_RATE, value).apply()

  var cursorStyle: String
    get() = preferences.getString(Keys.CURSOR_STYLE, Defaults.CURSOR_STYLE) ?: Defaults.CURSOR_STYLE
    set(value) = preferences.edit().putString(Keys.CURSOR_STYLE, value).apply()

  var transcriptRows: Int
    get() = preferences.getInt(Keys.TERMINAL_TRANSCRIPT_ROWS, Defaults.TERMINAL_TRANSCRIPT_ROWS)
    set(value) = preferences.edit().putInt(Keys.TERMINAL_TRANSCRIPT_ROWS, value).apply()

  var marginHorizontal: Int
    get() = preferences.getInt(Keys.TERMINAL_MARGIN_HORIZONTAL, Defaults.TERMINAL_MARGIN_HORIZONTAL)
    set(value) = preferences.edit().putInt(Keys.TERMINAL_MARGIN_HORIZONTAL, value).apply()

  var marginVertical: Int
    get() = preferences.getInt(Keys.TERMINAL_MARGIN_VERTICAL, Defaults.TERMINAL_MARGIN_VERTICAL)
    set(value) = preferences.edit().putInt(Keys.TERMINAL_MARGIN_VERTICAL, value).apply()

  var softKeyboardEnabled: Boolean
    get() = preferences.getBoolean(Keys.SOFT_KEYBOARD_ENABLED, Defaults.SOFT_KEYBOARD_ENABLED)
    set(value) = preferences.edit().putBoolean(Keys.SOFT_KEYBOARD_ENABLED, value).apply()

  var ctrlWorkaround: Boolean
    get() = preferences.getBoolean(Keys.CTRL_WORKAROUND, Defaults.CTRL_WORKAROUND)
    set(value) = preferences.edit().putBoolean(Keys.CTRL_WORKAROUND, value).apply()

  var useBlackUI: Boolean
    get() = preferences.getBoolean(Keys.USE_BLACK_UI, Defaults.USE_BLACK_UI)
    set(value) = preferences.edit().putBoolean(Keys.USE_BLACK_UI, value).apply()

  var enforceCharBasedInput: Boolean
    get() = preferences.getBoolean(Keys.ENFORCE_CHAR_BASED_INPUT, Defaults.ENFORCE_CHAR_BASED_INPUT)
    set(value) = preferences.edit().putBoolean(Keys.ENFORCE_CHAR_BASED_INPUT, value).apply()

  var backKeyBehaviour: String
    get() =
      preferences.getString(Keys.BACK_KEY_BEHAVIOUR, Defaults.BACK_KEY_BEHAVIOUR)
        ?: Defaults.BACK_KEY_BEHAVIOUR
    set(value) = preferences.edit().putString(Keys.BACK_KEY_BEHAVIOUR, value).apply()

  var terminalCursorKeys: String
    get() =
      preferences.getString(Keys.TERMINAL_CURSOR_KEYS, Defaults.TERMINAL_CURSOR_KEYS)
        ?: Defaults.TERMINAL_CURSOR_KEYS
    set(value) = preferences.edit().putString(Keys.TERMINAL_CURSOR_KEYS, value).apply()

  fun reset() {
    preferences.edit().clear().apply()
  }

  fun getCursorStyleAsInt(): Int {
    return when (cursorStyle) {
      "block" -> 0
      "underline" -> 1
      "bar" -> 2
      else -> 0
    }
  }
}
