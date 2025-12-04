package com.scto.codelikebastimove.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore

import com.scto.codelikebastimove.core.datastore.proto.ClonedRepositoryProto
import com.scto.codelikebastimove.core.datastore.proto.GitConfigProto
import com.scto.codelikebastimove.core.datastore.proto.UserPreferencesProto
import com.scto.codelikebastimove.core.datastore.proto.ThemeMode as ProtoThemeMode

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.userPreferencesStore: DataStore<UserPreferencesProto> by dataStore(
    fileName = "user_preferences.pb",
    serializer = UserPreferencesSerializer
)

class UserPreferencesRepository(private val context: Context) {

    val userPreferences: Flow<UserPreferences> = context.userPreferencesStore.data.map { proto ->
        UserPreferences(
            themeMode = proto.themeMode.toThemeMode(),
            dynamicColorsEnabled = proto.dynamicColorsEnabled,
            gitConfig = proto.gitConfig.toGitConfig(),
            clonedRepositories = proto.clonedRepositoriesList.map { it.toClonedRepository() }
        )
    }

    val gitConfig: Flow<GitConfig> = context.userPreferencesStore.data.map { proto ->
        proto.gitConfig.toGitConfig()
    }

    suspend fun getGitConfigOnce(): GitConfig {
        return gitConfig.first()
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

    suspend fun setGitConfig(userName: String, userEmail: String) {
        context.userPreferencesStore.updateData { currentPrefs ->
            val gitConfigProto = GitConfigProto.newBuilder()
                .setUserName(userName)
                .setUserEmail(userEmail)
                .build()
            currentPrefs.toBuilder()
                .setGitConfig(gitConfigProto)
                .build()
        }
    }

    suspend fun addClonedRepository(repository: ClonedRepository) {
        context.userPreferencesStore.updateData { currentPrefs ->
            val repoProto = ClonedRepositoryProto.newBuilder()
                .setPath(repository.path)
                .setUrl(repository.url)
                .setBranch(repository.branch)
                .setClonedAt(repository.clonedAt)
                .build()
            currentPrefs.toBuilder()
                .addClonedRepositories(repoProto)
                .build()
        }
    }

    suspend fun removeClonedRepository(path: String) {
        context.userPreferencesStore.updateData { currentPrefs ->
            val updatedRepos = currentPrefs.clonedRepositoriesList.filter { it.path != path }
            currentPrefs.toBuilder()
                .clearClonedRepositories()
                .addAllClonedRepositories(updatedRepos)
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

    private fun GitConfigProto?.toGitConfig(): GitConfig {
        return if (this == null) {
            GitConfig()
        } else {
            GitConfig(
                userName = userName ?: "",
                userEmail = userEmail ?: ""
            )
        }
    }

    private fun ClonedRepositoryProto.toClonedRepository(): ClonedRepository {
        return ClonedRepository(
            path = path,
            url = url,
            branch = branch,
            clonedAt = clonedAt
        )
    }
}
