package com.scto.codelikebastimove.core.datastore

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

data class GitCredentials(val username: String = "", val password: String = "") {
  fun isConfigured(): Boolean = username.isNotBlank() && password.isNotBlank()
}

class GitCredentialsStore(private val context: Context) {

  private val masterKey: MasterKey by lazy {
    MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
  }

  private val encryptedPrefs: SharedPreferences by lazy {
    EncryptedSharedPreferences.create(
      context,
      PREFS_FILE_NAME,
      masterKey,
      EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
      EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )
  }

  fun getCredentials(): GitCredentials {
    val username = encryptedPrefs.getString(KEY_USERNAME, "") ?: ""
    val password = encryptedPrefs.getString(KEY_PASSWORD, "") ?: ""
    return GitCredentials(username, password)
  }

  fun saveCredentials(username: String, password: String) {
    encryptedPrefs
      .edit()
      .putString(KEY_USERNAME, username)
      .putString(KEY_PASSWORD, password)
      .apply()
  }

  fun clearCredentials() {
    encryptedPrefs.edit().remove(KEY_USERNAME).remove(KEY_PASSWORD).apply()
  }

  fun hasCredentials(): Boolean {
    val creds = getCredentials()
    return creds.isConfigured()
  }

  companion object {
    private const val PREFS_FILE_NAME = "git_credentials_encrypted"
    private const val KEY_USERNAME = "git_username"
    private const val KEY_PASSWORD = "git_password"
  }
}
