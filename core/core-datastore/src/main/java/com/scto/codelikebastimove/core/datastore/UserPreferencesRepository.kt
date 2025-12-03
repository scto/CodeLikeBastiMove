package com.scto.codelikebastimove.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.scto.codelikebastimove.core.datastore.proto.UserPreferencesProto
import com.scto.codelikebastimove.core.datastore.proto.ThemeMode as ProtoThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesStore: DataStore<UserPreferencesProto> by dataStore(
    fileName = "user_preferences.pb",
    serializer = UserPreferencesSerializer
)

class UserPreferencesRepository(private val context: Context) {

    val userPreferences: Flow<UserPreferences> = context.userPreferencesStore.data.map { proto ->
        UserPreferences(
            themeMode = proto.themeMode.toThemeMode(),
            dynamicColorsEnabled = proto.dynamicColorsEnabled
        )
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.userPreferencesStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setThemeMode(themeMode.toProtoThemeMode())
                .build()
        }
    }

    suspend fun setDynamicColorsEnabled(enabled: Boolean) {
        context.userPreferencesStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setDynamicColorsEnabled(enabled)
                .build()
        }
    }

    private fun ProtoThemeMode.toThemeMode(): ThemeMode = when (this) {
        ProtoThemeMode.THEME_MODE_LIGHT -> ThemeMode.LIGHT
        ProtoThemeMode.THEME_MODE_DARK -> ThemeMode.DARK
        ProtoThemeMode.THEME_MODE_FOLLOW_SYSTEM -> ThemeMode.FOLLOW_SYSTEM
        ProtoThemeMode.THEME_MODE_UNSPECIFIED, ProtoThemeMode.UNRECOGNIZED -> ThemeMode.FOLLOW_SYSTEM
    }

    private fun ThemeMode.toProtoThemeMode(): ProtoThemeMode = when (this) {
        ThemeMode.LIGHT -> ProtoThemeMode.THEME_MODE_LIGHT
        ThemeMode.DARK -> ProtoThemeMode.THEME_MODE_DARK
        ThemeMode.FOLLOW_SYSTEM -> ProtoThemeMode.THEME_MODE_FOLLOW_SYSTEM
    }
}
